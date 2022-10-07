/*
 * MIT License
 *
 * Copyright (c) 2022 Kontent s.r.o.
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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/**
 * Content item listing response from an invocation of {@link DeliveryClient#getItem(String)}, or
 * {@link DeliveryClient#getItem(String, List)}.
 *
 * @see <a href="https://docs.kontent.ai/reference/delivery-api#section/Content-item-object">
 *      Kontent API reference - Content item object</a>
 * @see <a href="https://docs.kontent.ai/reference/delivery-api#operation/retrieve-a-content-item">
 *      Kontent API reference - View a content item</a>
 * @see ContentItem
 * @see DeliveryClient#getItem(String)
 * @see DeliveryClient#getItem(String, List)
 */
@lombok.Getter
@lombok.ToString(exclude = "stronglyTypedContentItemConverter")
@lombok.EqualsAndHashCode(exclude = "stronglyTypedContentItemConverter")
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@lombok.Builder
public class ContentItemResponse implements LinkedItemProvider {

    /**
     * The {@link ContentItem} returned by this ContentItemResponse.
     *
     * @see     <a href="https://docs.kontent.ai/reference/delivery-api#section/Content-item-object">
     *          Kontent API reference - Content item object</a>
     * @see     <a href="https://docs.kontent.ai/reference/delivery-api#operation/retrieve-a-content-item">
     *          Kontent API reference - View a content item</a>
     * @return  The {@link ContentItem} of this ContentItemResponse.
     */
    @JsonProperty("item")
    ContentItem item;

    /**
     * A map of content items used in linked item and Rich text elements.
     *
     * @see     <a href="https://docs.kontent.ai/reference/delivery-api#section/Linked-items-element">
     *          Kontent API reference - Linked items</a>
     * @see     <a href="https://docs.kontent.ai/reference/delivery-api#section/Content-item-object">
     *          Kontent API reference - Content item object</a>
     * @see     <a href="https://docs.kontent.ai/reference/delivery-api#operation/retrieve-a-content-item">
     *          Kontent API reference - View a content item</a>
     * @return  The linked {@link ContentItem}s referenced in this response.
     */
    @JsonProperty("modular_content")
    Map<String, ContentItem> linkedItems;

    @JsonIgnore
    private StronglyTypedContentItemConverter stronglyTypedContentItemConverter;

    /**
     * Returns a new instance of T by mapping fields to elements in this response's {@link #getItem()}.  Element fields
     * are mapped by automatically CamelCasing and checking for equality, unless otherwise annotated by an
     * {@link ElementMapping} annotation.  T must have a default constructor and have standard setter methods.  When
     * passing in Object.class, the type returned will be an instance of the class registered with the
     * {@link DeliveryClient} that is annotated with {@link ContentItemMapping} that matches the
     * {@link System#type} of this ContentItem (however still returned as type Object).
     * <p>
     * If {@link Object} is passed in, the {@link StronglyTypedContentItemConverter} will cast this ContentItem to a
     * type that is mapped this ContentItem's {@link System#type} from a previous registration via the
     * {@link DeliveryClient#registerType(Class)} or {@link DeliveryClient#registerType(String, Class)} methods.  If no
     * registration has been done, then this same instance of ContentItem will be returned.
     *
     * @param   tClass The class which a new instance should be returned from using this ContentItem.
     * @param   <T> The type of class which will be returned.
     * @return  An instance of T with data mapped from the {@link ContentItem} in this response.
     * @see     DeliveryClient#registerType(Class)
     * @see     DeliveryClient#registerType(String, Class)
     * @see     ContentItemMapping
     * @see     ElementMapping
     * @see     StronglyTypedContentItemConverter
     */
    public <T> T castTo(Class<T> tClass) {
        return stronglyTypedContentItemConverter.convert(item, this.getLinkedItems(), tClass);
    }

    void setItem(ContentItem item) {
        this.item = item;
        item.setLinkedItemProvider(this);
    }

    void setLinkedItems(Map<String, ContentItem> linkedItems) {
        this.linkedItems = linkedItems;
        linkedItems.values().forEach(contentItem -> contentItem.setLinkedItemProvider(this));
    }

    ContentItemResponse setStronglyTypedContentItemConverter(StronglyTypedContentItemConverter stronglyTypedContentItemConverter) {
        this.stronglyTypedContentItemConverter = stronglyTypedContentItemConverter;
        item.setStronglyTypedContentItemConverter(stronglyTypedContentItemConverter);
        if (linkedItems != null) {
            for (ContentItem linkedItem : linkedItems.values()) {
                linkedItem.setStronglyTypedContentItemConverter(stronglyTypedContentItemConverter);
            }
        }
        return this;
    }
}
