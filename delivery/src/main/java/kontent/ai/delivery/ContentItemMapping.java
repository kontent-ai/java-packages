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

package kontent.ai.delivery;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used by the {@link StronglyTypedContentItemConverter} to convert {@link ContentItem}s into any
 * plain old Java object of your choosing.
 * <p>
 * When registering a custom type with {@link DeliveryClient#registerType(Class)}, this annotation is required to be on
 * the class, and the {@link #value()} is used to register the type with {@link ContentItem}s with the same value set
 * for it's {@link System#type}.  If you do not wish to annotate your class, you can still use
 * {@link DeliveryClient#registerType(String, Class)}.
 * <p>
 * For example, the following will map to {@link ContentItem}s with a {@link System#type} of 'article':
 * <pre>
 * &#64;ContentItemMapping("article")
 * public class ArticleItem {
 *  ...
 * }
 * </pre>
 * <p>
 * When this annotation is placed on a field and the {@link #value()} matches a key in the {@link ContentItem}'s linked
 * items, then the {@link ContentItem} from the linked item will be cast to the field.  If you do not wish to
 * annotate the field, you can name the field (using CamelCase) the same as the codename of the linked item
 * {@link ContentItem}.  If using a custom type, the {@link ContentItem} will be recursively converted it until the
 * depth of the original query is exhausted or loops back.  Note, that because this maps to a specific codename, it will
 * generally be of limited use unless you know that instance of linked item will always be referenced.
 * <p>
 * For example, the following will map a field to a linked item with the codename 'origins_of_arabica_bourbon':
 * <pre>
 * &#64;ContentItemMapping("origins_of_arabica_bourbon")
 * ContentItem arabicaBourbonOrigin;
 * </pre>
 * <p>
 * When this annotation is placed on a field of type List or Map, the {@link #value()} is used to map to the matching
 * key from {@link ContentItem#getElements()} and if the {@link Element} is of type {@link LinkedItem}, the
 * {@link ContentItem}s contained will be cast to the type of the List or type of the Map's value entry.  If using a
 * custom type, any {@link ContentItem}s with a {@link System#type} that is not registered for the type will be
 * excluded from the List or Map.  If you do not wish to annotate the List or Map, you can name the field (using
 * CamelCase) to the name of the {@link Element} codename.  Keys to maps will be the {@link System#codename} of the
 * {@link ContentItem} instances.
 * <p>
 * For example, the following will map to the 'related_articles' element if it's of type {@link LinkedItem}:
 * <pre>
 * &#64;ContentItemMapping("related_articles")
 * List&#60;ContentItem&#62; relatedArticles;
 *
 * &#64;ContentItemMapping("related_articles")
 * Map&#60;String, ContentItem&#62; relatedArticlesMap;
 * </pre>
 * <p>
 * Custom types may also be registered with {@link DeliveryClient#scanClasspathForMappings(String)}.
 *
 * @see ContentItem
 * @see System
 * @see DeliveryClient#registerType(Class)
 * @see DeliveryClient#scanClasspathForMappings(String)
 * @see DeliveryParameterBuilder#linkedItemsDepth(Integer)
 * @see ElementMapping
 * @see LinkedItem
 * @see StronglyTypedContentItemConverter
 * @see <a href="https://docs.kontent.ai/reference/delivery-api#section/Content-item-object">
 *      Kontent API reference - Content item object</a>
 * @see <a href="https://docs.kontent.ai/reference/delivery-api#section/Linked-items-element">
 *      Kontent API reference - Linked items</a>
 */
@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ContentItemMapping {

    /**
     * When placed on a Class, this maps to a {@link ContentItem}'s {@link System#type}.
     * <p>
     * When placed on a field, this maps to the linked {@link ContentItem} with a matching {@link System#codename}
     * if it was included with the {@link ContentItem}.
     * <p>
     * When placed on a List or Map, this maps to linked {@link ContentItem}s that are linked by a {@link LinkedItem}
     * with the same name.
     *
     * @return  The {@link System#type} or codename this annotation is referencing.  See the documentation on this
     *          annotation.
     */
    String value();
}
