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

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConstructorUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.HashMap;
import java.util.Map;

public class StronglyTypedContentItemConverter {

    private StronglyTypedContentItemConverter() {
        throw new IllegalStateException("Utility class");
    }

    //TODO: All of the reflection that happens in here should be stored in a plan object and cached in a WeakHashMap to avoid doing this over and over for performance
    static <T> T convert(ContentItem item, Map<String, ContentItem> modularContent, Class<T> tClass) {
        try {
            //Invoke the default constructor
            T bean = ConstructorUtils.invokeConstructor(tClass, null);

            //Get the bean properties
            Field[] fields = tClass.getDeclaredFields();

            //Inject mappings
            for (Field field : fields) {
                Object value = getValueForField(item, modularContent, field);
                if (value != null) {
                    BeanUtils.setProperty(bean, field.getName(), value);
                }
            }

            //Return bean
            return bean;
        } catch (NoSuchMethodException |
                IllegalAccessException |
                InvocationTargetException |
                InstantiationException e) {
            handleReflectionException(e);
        }
        return null;
    }

    private static Object getValueForField(ContentItem item, Map<String, ContentItem> modularContent, Field field) {
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
        return null;
    }

    private static Object getCastedModularContentForField(Class<?> clazz, ContentItem modularContentItem) {
        //TODO: Detect if they want a collection filled
        if (clazz == ContentItem.class) {
            return modularContentItem;
        }
        ContentItemMapping clazzContentItemMapping = clazz.getAnnotation(ContentItemMapping.class);
        if (clazzContentItemMapping != null) {
            //TODO: Should pass in a modified list of modular content instead of empty map.  Passing empty map now to avoid infinite recursion when types are nested
            return convert(modularContentItem, new HashMap<>(), clazz);
        }
        return null;
    }

    private static String fromCamelCase(String s) {
        String regex = "([a-z])([A-Z]+)";
        String replacement = "$1_$2";
        return s.replaceAll(regex, replacement).toLowerCase();
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
