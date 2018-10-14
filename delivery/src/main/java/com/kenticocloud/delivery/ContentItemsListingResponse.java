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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Content item listing response from an invocation of {@link DeliveryClient#getItems()}, or
 * {@link DeliveryClient#getItems(List)}.
 *
 * @see <a href="https://developer.kenticocloud.com/v2/reference#listing-response">
 *      KenticoCloud API reference - Listing response</a>
 * @see <a href="https://developer.kenticocloud.com/v2/reference#content-item-object">
 *      KenticoCloud API reference - Content item object</a>
 * @see <a href="https://developer.kenticocloud.com/v2/reference#list-content-items">
 *      KenticoCloud API reference - List content items</a>
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
public class ContentItemsListingResponse implements LinkedItemProvider {

    /**
     * The {@link ContentItem}s returned by this ContentItemsListingResponse.
     *
     * @see     <a href="https://developer.kenticocloud.com/v2/reference#listing-response">
     *          KenticoCloud API reference - Listing response</a>
     * @see     <a href="https://developer.kenticocloud.com/v2/reference#content-item-object">
     *          KenticoCloud API reference - Content item object</a>
     * @see     <a href="https://developer.kenticocloud.com/v2/reference#list-content-items">
     *          KenticoCloud API reference - List content items</a>
     * @return  The {@link ContentItem}s of this ContentItemsListingResponse.
     */
    @JsonProperty("items")
    List<ContentItem> items;

    /**
     * A map of content items used in linked item and Rich text elements.
     *
     * @see     <a href="https://developer.kenticocloud.com/v2/reference#listing-response">
     *          KenticoCloud API reference - Listing response</a>
     * @see     <a href="https://developer.kenticocloud.com/v2/reference#linked-items">
     *          KenticoCloud API reference - Linked items</a>
     * @see     <a href="https://developer.kenticocloud.com/v2/reference#content-item-object">
     *          KenticoCloud API reference - Content item object</a>
     * @see     <a href="https://developer.kenticocloud.com/v2/reference#list-content-items">
     *          KenticoCloud API reference - List content items</a>
     * @return  The linked {@link ContentItem}s referenced in this response.
     */
    @JsonProperty("modular_content")
    Map<String, ContentItem> linkedItems;

    /**
     * Information about the retrieved page.  Used for iterating a large result set if using limit query parameters.
     *
     * @see     <a href="https://developer.kenticocloud.com/v2/reference#listing-response-paging">
     *          KenticoCloud API reference - Listing response paging</a>
     * @see     <a href="https://developer.kenticocloud.com/v2/reference#section-pagination-object">
     *          KenticoCloud API reference - Pagination object</a>
     * @return  The {@link Pagination} for this ContentItemsListingResponse identifying the current page.
     */
    @JsonProperty("pagination")
    Pagination pagination;

    @JsonIgnore
    private StronglyTypedContentItemConverter stronglyTypedContentItemConverter;

    /**
     * Returns a new instance of {@code List<T>} by mapping fields to elements in this content item.  Element fields are
     * mapped by automatically CamelCasing and checking for equality, unless otherwise annotated by an
     * {@link ElementMapping} annotation.  T must have a default constructor and have standard setter methods.
     *
     * @param tClass    The class which a new instance should be returned from
     * @param <T>       The type of class
     * @return          An instance of {@code List<T>} with data mapped from the {@link ContentItem} list in this
     *                  response.
     */
    public <T> List<T> castTo(Class<T> tClass) {
        ArrayList<T> tItems = new ArrayList<>();
        for (ContentItem item : getItems()) {
            tItems.add(stronglyTypedContentItemConverter.convert(item, this.getLinkedItems(), tClass));
        }
        return tItems;
    }

    void setItems(List<ContentItem> items) {
        this.items = items;
        items.forEach(contentItem -> contentItem.setLinkedItemProvider(this));
    }

    void setLinkedItems(Map<String, ContentItem> linkedItems) {
        this.linkedItems = linkedItems;
        linkedItems.values().forEach(contentItem -> contentItem.setLinkedItemProvider(this));
    }

    void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }

    ContentItemsListingResponse setStronglyTypedContentItemConverter(StronglyTypedContentItemConverter stronglyTypedContentItemConverter) {
        this.stronglyTypedContentItemConverter = stronglyTypedContentItemConverter;
        for (ContentItem item : getItems()) {
            item.setStronglyTypedContentItemConverter(stronglyTypedContentItemConverter);
        }
        if (linkedItems != null) {
            for (ContentItem linkedItem : linkedItems.values()) {
                linkedItem.setStronglyTypedContentItemConverter(stronglyTypedContentItemConverter);
            }
        }
        return this;
    }
}
