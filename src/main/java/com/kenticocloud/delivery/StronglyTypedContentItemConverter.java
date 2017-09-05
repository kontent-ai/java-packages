/*
 * MIT License
 *
 * Copyright (c) 2017
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.kenticocloud.delivery;

import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConstructorUtils;
import org.apache.commons.beanutils.PropertyUtilsBean;

import java.beans.PropertyDescriptor;
import java.lang.reflect.*;
import java.util.*;

public class StronglyTypedContentItemConverter {

    private HashMap<String, Class<?>> contentTypeToClassMapping = new HashMap<>();
    private HashMap<Class<?>, String> classToContentTypeMapping = new HashMap<>();
    private HashMap<Type, InlineContentItemsResolver> typeToInlineResolverMapping = new HashMap<>();

    protected StronglyTypedContentItemConverter() {
        //protected constructor
    }

    protected void registerType(String contentType, Class<?> clazz) {
        contentTypeToClassMapping.put(contentType, clazz);
        classToContentTypeMapping.put(clazz, contentType);
    }

    protected void registerType(Class<?> clazz) {
        ContentItemMapping clazzContentItemMapping = clazz.getAnnotation(ContentItemMapping.class);
        if (clazzContentItemMapping == null) {
            throw new IllegalArgumentException("Passed in class must be annotated with @ContentItemMapping, " +
                    "if this is not possible, please use registerType(String, Class)");
        }
        registerType(clazzContentItemMapping.value(), clazz);
    }

    protected void registerInlineContentItemsResolver(InlineContentItemsResolver resolver) {
        typeToInlineResolverMapping.put(resolver.getType(), resolver);
    }

    protected InlineContentItemsResolver getResolverForType(String contentType) {
        if (contentTypeToClassMapping.containsKey(contentType) &&
                typeToInlineResolverMapping.containsKey(contentTypeToClassMapping.get(contentType))) {
            return typeToInlineResolverMapping.get(contentTypeToClassMapping.get(contentType));
        }
        return null;
    }

    protected InlineContentItemsResolver getResolverForType(ContentItem contentItem) {
        System system = contentItem.getSystem();
        if (system != null) {
            return getResolverForType(system.getType());
        }
        return null;
    }

    protected void scanClasspathForMappings(String basePackage) {
        FastClasspathScanner scanner = new FastClasspathScanner(basePackage);
        scanner.matchClassesWithAnnotation(ContentItemMapping.class, classWithAnnotation -> {
            ContentItemMapping contentItemMapping = classWithAnnotation.getAnnotation(ContentItemMapping.class);
            registerType(contentItemMapping.value(), classWithAnnotation);
        }).matchSubclassesOf(InlineContentItemsResolver.class, subclass -> {
            try {
                registerInlineContentItemsResolver(ConstructorUtils.invokeConstructor(subclass, null));
            } catch (NoSuchMethodException |
                    IllegalAccessException |
                    InvocationTargetException |
                    InstantiationException e) {
                // No default constructor, no InlineContentItemsResolver.
            }
        }).scan();
    }

    //TODO: All of the reflection that happens in here should be stored in a plan object and cached in a WeakHashMap to avoid doing this over and over for performance
    <T> T convert(ContentItem item, Map<String, ContentItem> modularContent, Class<T> tClass) {
        if (tClass == Object.class) {
            Class<?> mappingClass = contentTypeToClassMapping.get(item.getSystem().getType());
            if (mappingClass == null) {
                return (T) item;
            }
            return (T) convert(item, modularContent, mappingClass);
        }
        T bean = null;
        try {
            //Invoke the default constructor
            bean = ConstructorUtils.invokeConstructor(tClass, null);

            //Get the bean properties
            Field[] fields = tClass.getDeclaredFields();

            //Inject mappings
            for (Field field : fields) {
                Object value = getValueForField(item, modularContent, bean, field);
                if (value != null) {
                    BeanUtils.setProperty(bean, field.getName(), value);
                }
            }
        } catch (NoSuchMethodException |
                IllegalAccessException |
                InvocationTargetException |
                InstantiationException e) {
            handleReflectionException(e);
        }
        //Return bean
        return bean;
    }

    private Object getValueForField(ContentItem item, Map<String, ContentItem> modularContent, Object bean, Field field) {
        //Explicit checks
        //Check to see if this is an explicitly mapped Element
        ElementMapping elementMapping = field.getAnnotation(ElementMapping.class);
        if (elementMapping != null && item.getElements().containsKey(elementMapping.value())) {
            return item.getElements().get(elementMapping.value()).getValue();
        }
        //Check to see if this is an explicitly mapped ContentItem
        ContentItemMapping contentItemMapping = field.getAnnotation(ContentItemMapping.class);
        if (contentItemMapping != null && modularContent.containsKey(contentItemMapping.value())) {
            return getCastedModularContentForField(field.getType(), modularContent.get(contentItemMapping.value()));
        }

        //Implicit checks
        String candidateCodename = fromCamelCase(field.getName());
        //Check to see if this is an implicitly mapped Element
        if (item.getElements().containsKey(candidateCodename)){
            return item.getElements().get(candidateCodename).getValue();
        }
        //Check to see if this is an implicitly mapped ContentItem
        if (modularContent.containsKey(candidateCodename)) {
            return getCastedModularContentForField(field.getType(), modularContent.get(candidateCodename));
        }

        //Check to see if this is a collection of implicitly mapped ContentItem
        if (isListOrMap(field.getType())){
            return getCastedModularContentForListOrMap(bean, field, modularContent);
        }
        return null;
    }

    private Object getCastedModularContentForField(Class<?> clazz, ContentItem modularContentItem) {
        if (clazz == ContentItem.class) {
            return modularContentItem;
        }
        //TODO: Should pass in a modified list of modular content instead of empty map.  Passing empty map now to avoid infinite recursion when types are nested
        return convert(modularContentItem, new HashMap<>(), clazz);
    }

    private Object getCastedModularContentForListOrMap(Object bean, Field field, Map<String, ContentItem> modularContent) {
        Type type = getType(bean, field);
        if (type == null) {
            //We have failed to get the type, probably due to a missing setter, skip this field
            return null;
        }
        if (type == ContentItem.class) {
            return castCollection(field.getType(), modularContent);
        }
        Class<?> listClass = (Class<?>) type;
        String contentType = null;
        ContentItemMapping clazzContentItemMapping = listClass.getAnnotation(ContentItemMapping.class);
        if (clazzContentItemMapping != null) {
            contentType = clazzContentItemMapping.value();
        }
        if (contentType == null && classToContentTypeMapping.containsKey(listClass)) {
            contentType = classToContentTypeMapping.get(listClass);
        }
        if (contentType != null) {
            HashMap convertedModularContent = new HashMap<>();
            for (Map.Entry<String, ContentItem> entry : modularContent.entrySet()) {
                if (contentType.equals(entry.getValue().getSystem().getType())) {
                    //TODO: Should pass in a modified list of modular content instead of empty map.  Passing empty map now to avoid infinite recursion when types are nested
                    convertedModularContent.put(entry.getKey(), convert(entry.getValue(), new HashMap<>(), listClass));
                }
            }
            return castCollection(field.getType(), convertedModularContent);
        }
        return null;
    }

    private static String fromCamelCase(String s) {
        String regex = "([a-z])([A-Z]+)";
        String replacement = "$1_$2";
        return s.replaceAll(regex, replacement).toLowerCase();
    }

    private static boolean isListOrMap(Class<?> type) {
        return List.class.isAssignableFrom(type) || Map.class.isAssignableFrom(type);
    }

    private static Object castCollection(Class<?> type, Map<String, ?> items) {
        if (List.class.isAssignableFrom(type)) {
            return new ArrayList(items.values());
        }
        if (Map.class.isAssignableFrom(type)){
            return items;
        }
        return items;
    }

    private static Type getType(Object bean, Field field) {
        //Because of type erasure, we will find the setter method and get the generic types off it's arguments
        PropertyUtilsBean propertyUtils = BeanUtilsBean.getInstance().getPropertyUtils();
        PropertyDescriptor propertyDescriptor = null;
        try {
            propertyDescriptor = propertyUtils.getPropertyDescriptor(bean, field.getName());
        } catch (IllegalAccessException |
                InvocationTargetException |
                NoSuchMethodException e) {
            handleReflectionException(e);
        }
        if (propertyDescriptor == null) {
            //Likely no accessors
            return null;
        }
        Method writeMethod = propertyUtils.getWriteMethod(propertyDescriptor);
        if (writeMethod == null) {
            return null;
        }
        Type[] actualTypeArguments = ((ParameterizedType) writeMethod.getGenericParameterTypes()[0]).getActualTypeArguments();
        if (Map.class.isAssignableFrom(field.getType())) {
            return actualTypeArguments[1];
        }
        return actualTypeArguments[0];

    }

    private static void handleReflectionException(Exception ex) {
        if (ex instanceof NoSuchMethodException) {
            throw new IllegalStateException("Method not found: " + ex.getMessage());
        }
        if (ex instanceof IllegalAccessException) {
            throw new IllegalStateException("Could not access method: " + ex.getMessage());
        }
        if (ex instanceof RuntimeException) {
            throw (RuntimeException) ex;
        }
        throw new UndeclaredThrowableException(ex);
    }
}
