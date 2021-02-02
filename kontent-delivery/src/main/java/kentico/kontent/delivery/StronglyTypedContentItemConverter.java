/*
 * MIT License
 *
 * Copyright (c) 2019
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

package kentico.kontent.delivery;

import com.madrobot.beans.BeanInfo;
import com.madrobot.beans.IntrospectionException;
import com.madrobot.beans.Introspector;
import com.madrobot.beans.PropertyDescriptor;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.*;
import java.util.*;

@lombok.extern.slf4j.Slf4j
public class StronglyTypedContentItemConverter {

    private HashMap<String, String> contentTypeToClassNameMapping = new HashMap<>();
    private HashMap<String, String> classNameToContentTypeMapping = new HashMap<>();
    private HashMap<String, InlineContentItemsResolver> typeNameToInlineResolverMapping = new HashMap<>();

    protected StronglyTypedContentItemConverter() {
        //protected constructor
    }

    protected void registerType(String contentType, Class<?> clazz) {
        contentTypeToClassNameMapping.put(contentType, clazz.getName());
        classNameToContentTypeMapping.put(clazz.getName(), contentType);
    }

    protected void registerType(Class<?> clazz) {
        ContentItemMapping clazzContentItemMapping = clazz.getAnnotation(ContentItemMapping.class);
        if (clazzContentItemMapping == null) {
            throw new IllegalArgumentException("Passed in class must be annotated with @ContentItemMapping, " +
                    "if this is not possible, please use registerType(String, Class)");
        }
        registerType(clazzContentItemMapping.value(), clazz);
        log.debug("Registered type for {}", clazz.getSimpleName());
    }

    protected String getContentType(Class tClass) {
        if (classNameToContentTypeMapping.containsKey(tClass.getName())) {
            return classNameToContentTypeMapping.get(tClass.getName());
        }
        return null;
    }

    protected void registerInlineContentItemsResolver(InlineContentItemsResolver resolver) {
        typeNameToInlineResolverMapping.put(resolver.getType().getTypeName(), resolver);
    }

    protected InlineContentItemsResolver getResolverForType(String contentType) {
        if (contentTypeToClassNameMapping.containsKey(contentType) &&
                typeNameToInlineResolverMapping.containsKey(contentTypeToClassNameMapping.get(contentType))) {
            return typeNameToInlineResolverMapping.get(contentTypeToClassNameMapping.get(contentType));
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

    /**
     * Not working on Android platform because of JVM and Dalvik differences, please use {@link DeliveryClient#registerType(Class)} instead
     * @param basePackage name of the base package
     */
    protected void scanClasspathForMappings(String basePackage) {
        try (ScanResult scanResult = new ClassGraph()
                .enableAllInfo()
                .acceptPackages(basePackage)
                .scan()) {
            ClassInfoList mappings = scanResult.getClassesWithAnnotation(ContentItemMapping.class.getName());
            mappings.loadClasses().forEach(classWithAnnotation -> {
                ContentItemMapping contentItemMapping = classWithAnnotation.getAnnotation(ContentItemMapping.class);
                registerType(contentItemMapping.value(), classWithAnnotation);
            });

            ClassInfoList inlineResolvers = scanResult.getSubclasses(InlineContentItemsResolver.class.getName());
            inlineResolvers.loadClasses(InlineContentItemsResolver.class).forEach(subclass -> {
                try {
                    registerInlineContentItemsResolver(subclass.getConstructor().newInstance());
                } catch (NoSuchMethodException |
                        IllegalAccessException |
                        InvocationTargetException |
                        InstantiationException e) {
                    // No default constructor, no InlineContentItemsResolver.
                }
            });
        }
    }

    Object convert(ContentItem item, Map<String, ContentItem> linkedItems, String contentType) {
        String className = contentTypeToClassNameMapping.get(contentType);
        Class<?> mappingClass;
        try {
            mappingClass = Class.forName(className);
        } catch (ClassNotFoundException e) {
            return item;
        }

        if (mappingClass == null) {
            return item;
        }

        return convert(item, linkedItems, mappingClass);
    }

    <T> T convert(ContentItem item, Map<String, ContentItem> linkedItems, Class<T> tClass) {
        if (tClass == Object.class) {
            String className = contentTypeToClassNameMapping.get(item.getSystem().getType());
            if (className == null) {
                return (T) item;
            }
            Class<?> mappingClass = null;
            try {
                mappingClass = Class.forName(className);
            } catch (ClassNotFoundException e) {
                return (T) item;
            }
            if (mappingClass == null) {
                return (T) item;
            }
            return (T) convert(item, linkedItems, mappingClass);
        }
        if (tClass == ContentItem.class) {
            return (T) item;
        }
        T bean = null;
        try {
            //Invoke the default constructor
            bean = tClass.getConstructor().newInstance();

            //Get the bean properties
            Field[] fields = tClass.getDeclaredFields();

            //Inject mappings
            for (Field field : fields) {
                Object value = getValueForField(item, linkedItems, bean, field);
                if (value != null) {
                    Optional<PropertyDescriptor> propertyDescriptor = getPropertyDescriptor(bean, field);
                    if (propertyDescriptor.isPresent()) {
                        propertyDescriptor.get().getWriteMethod().invoke(bean, value);
                    }
                }
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
            handleReflectionException(e);
        }
        //Return bean
        return bean;
    }

    private Object getValueForField(
            ContentItem item, Map<String, ContentItem> linkedItems, Object bean, Field field) {
        //Inject System object
        if (field.getType() == System.class) {
            return item.getSystem();
        }
        //Explicit checks
        //Check to see if this is an explicitly mapped Element
        ElementMapping elementMapping = field.getAnnotation(ElementMapping.class);
        if (elementMapping != null && item.getElements().containsKey(elementMapping.value())) {
            return item.getElements().get(elementMapping.value()).getValue();
        }
        //Check to see if this is an explicitly mapped ContentItem
        ContentItemMapping contentItemMapping = field.getAnnotation(ContentItemMapping.class);

        // Modular content mapping - linked items element
        if (contentItemMapping != null &&
                isListOrMap(field.getType()) &&
                item.getElements().containsKey(contentItemMapping.value()) &&
                item.getElements().get(contentItemMapping.value()) instanceof LinkedItem) {
            LinkedItem linkedItemElement =
                    (LinkedItem) item.getElements().get(contentItemMapping.value());
            Map<String, ContentItem> referencedLinkedItems = new LinkedHashMap<>();
            for (String codename : linkedItemElement.getValue()) {
                referencedLinkedItems.put(codename, linkedItems.get(codename));
            }
            return getCastedLinkedItemsForListOrMap(bean, field, referencedLinkedItems, linkedItems);
        }
        // Single Linked Item mapping
        if (contentItemMapping != null && linkedItems.containsKey(contentItemMapping.value())) {
            return getCastedLinkedItemsForField(field.getType(), contentItemMapping.value(), linkedItems);
        }

        //Implicit checks
        String candidateCodename = fromCamelCase(field.getName());
        //Check to see if this is an implicitly mapped Element
        if (item.getElements().containsKey(candidateCodename)) {
            return item.getElements().get(candidateCodename).getValue();
        }
        //Check to see if this is an implicitly mapped ContentItem
        if (linkedItems.containsKey(candidateCodename)) {
            return getCastedLinkedItemsForField(field.getType(), candidateCodename, linkedItems);
        }

        //Check to see if this is a collection of implicitly mapped ContentItem
        if (isListOrMap(field.getType())) {
            return getCastedLinkedItemsForListOrMap(bean, field, linkedItems, linkedItems);
        }
        return null;
    }

    private Object getCastedLinkedItemsForField(
            Class<?> clazz, String codename, Map<String, ContentItem> linkedItems) {
        ContentItem linkedItemsItem = linkedItems.get(codename);
        if (clazz == ContentItem.class) {
            return linkedItemsItem;
        }
        Map<String, ContentItem> linkedItemsForRecursion =
                copyLinkedItemsWithExclusion(linkedItems, codename);
        return convert(linkedItemsItem, linkedItemsForRecursion, clazz);
    }

    private Object getCastedLinkedItemsForListOrMap(
            Object bean, Field field, Map<String, ContentItem> referencedLinkedItems, Map<String, ContentItem> allLinkedItems) {
        Type type = getType(bean, field);
        if (type == null) {
            // We have failed to get the type, probably due to a missing setter, skip this field
            log.debug("Failed to get type from {} (probably due to a missing setter), {} skipped", bean, field);
            return null;
        }
        if (type == ContentItem.class) {
            return castCollection(field.getType(), referencedLinkedItems);
        }
        Class<?> listClass = (Class<?>) type;
        String contentType = null;
        ContentItemMapping clazzContentItemMapping = listClass.getAnnotation(ContentItemMapping.class);
        if (clazzContentItemMapping != null) {
            contentType = clazzContentItemMapping.value();
        }
        if (contentType == null && classNameToContentTypeMapping.containsKey(listClass.getName())) {
            contentType = classNameToContentTypeMapping.get(listClass.getName());
        }
        if (contentType != null) {
            // This type preserves the order of the insertion, to have the same order as in delivery response
            LinkedHashMap convertedLinkedItems = new LinkedHashMap();
            for (Map.Entry<String, ContentItem> entry : referencedLinkedItems.entrySet()) {
                if (entry.getValue() != null && contentType.equals(entry.getValue().getSystem().getType())) {
                    Map<String, ContentItem> linkedItemsForRecursion =
                            copyLinkedItemsWithExclusion(allLinkedItems, entry.getKey());
                    convertedLinkedItems.put(entry.getKey(),
                            convert(entry.getValue(), linkedItemsForRecursion, listClass));
                }
            }
            return castCollection(field.getType(), convertedLinkedItems);
        }
        return null;
    }

    private static String fromCamelCase(String s) {
        String regex = "([a-z])([A-Z]+)";
        String replacement = "$1_$2";
        return s.replaceAll(regex, replacement).toLowerCase();
    }

    // This function copies the linked item map while excluding an item, useful for a recursive stack
    protected Map<String, ContentItem> copyLinkedItemsWithExclusion(
            Map<String, ContentItem> orig, String excludedContentItem) {
        HashMap<String, ContentItem> target = new HashMap<>();
        for (Map.Entry<String, ContentItem> entry : orig.entrySet()) {
            if (!excludedContentItem.equals(entry.getKey())) {
                target.put(entry.getKey(), entry.getValue());
            }
        }
        return target;
    }

    private static boolean isListOrMap(Class<?> type) {
        return List.class.isAssignableFrom(type) || Map.class.isAssignableFrom(type);
    }

    private static Object castCollection(Class<?> type, Map<String, ?> items) {
        if (List.class.isAssignableFrom(type)) {
            return new ArrayList(items.values());
        }
        if (Map.class.isAssignableFrom(type)) {
            return items;
        }
        return items;
    }

    private static Type getType(Object bean, Field field) {
        //Because of type erasure, we will find the setter method and get the generic types off it's arguments
        Optional<PropertyDescriptor> propertyDescriptor = getPropertyDescriptor(bean, field);
        if (!propertyDescriptor.isPresent()) {
            //Likely no accessors
            log.debug("Property descriptor for object {} with field {} is null", bean, field);
            return null;
        }

        Method writeMethod = propertyDescriptor.get().getWriteMethod();
        if (writeMethod == null) {
            log.debug("No write method for property {}", propertyDescriptor);
            return null;
        }
        Type[] actualTypeArguments = ((ParameterizedType) writeMethod.getGenericParameterTypes()[0])
                .getActualTypeArguments();

        Type type = (Map.class.isAssignableFrom(field.getType())) ? actualTypeArguments[1] : actualTypeArguments[0];

        log.debug("Got type {} from {}",
                type.getTypeName(),
                String.format("%s#%s", bean.getClass().getSimpleName(), field.getName()));

        return type;
    }

    @NotNull
    private static Optional<PropertyDescriptor> getPropertyDescriptor(Object bean, Field field) {
        BeanInfo beanInfo = null;
        try {
            beanInfo = Introspector.getBeanInfo(bean.getClass());
        } catch (IntrospectionException e) {
            log.debug("IntrospectionException from com.madrobot.beans for object {} with field {} is null", bean, field);
            return null;
        }
        PropertyDescriptor[] properties = beanInfo.getPropertyDescriptors();
        Optional<PropertyDescriptor> propertyDescriptor = Arrays.stream(properties).filter(descriptor -> descriptor.getName().equals(field.getName())).findFirst();
        return propertyDescriptor;
    }

    private static void handleReflectionException(Exception ex) {
        log.error("Reflection exception", ex);
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
