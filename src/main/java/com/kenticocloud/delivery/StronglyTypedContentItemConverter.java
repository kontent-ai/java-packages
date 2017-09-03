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
import java.util.WeakHashMap;

public class StronglyTypedContentItemConverter {

    private static final WeakHashMap<Class, Field[]> CACHE = new WeakHashMap<>();

    private StronglyTypedContentItemConverter() {
        throw new IllegalStateException("Utility class");
    }

    static <T> T convert(ContentItem item, Class<T> tClass) {
        try {
            //Invoke the default constructor
            T bean = ConstructorUtils.invokeConstructor(tClass, null);

            //Get the bean properties
            Field[] fields = CACHE.get(tClass);
            if (fields == null) {
                fields = tClass.getDeclaredFields();
                CACHE.put(tClass, fields);
            }

            //Extract mappings
            Map<String, String> codenameToFieldName = new HashMap<>();
            for (Field field : fields) {
                ElementMapping elementMapping = field.getAnnotation(ElementMapping.class);
                if (elementMapping != null) {
                    codenameToFieldName.put(elementMapping.value(), field.getName());
                } else {
                    codenameToFieldName.put(fromCamelCase(field.getName()), field.getName());
                }
            }

            //Inject properties
            Map<String, Element> elements = item.getElements();
            for(Map.Entry<String, String> entry : codenameToFieldName.entrySet()){
                if (elements.containsKey(entry.getKey())) {
                    BeanUtils.setProperty(bean, entry.getValue(), elements.get(entry.getKey()).getValue());
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
