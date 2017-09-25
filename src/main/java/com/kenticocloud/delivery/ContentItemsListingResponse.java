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

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Content items listing response
 * <p>
 * When you retrieve a list of content items from your project, the Delivery API returns a
 * {@link ContentItemsListingResponse}
 * @see DeliveryClient#getItems()
 * @see DeliveryClient#getItems(List)
 */
public class ContentItemsListingResponse implements ModularContentProvider {

    @JsonProperty("items")
    List<ContentItem> items;

    @JsonProperty("modular_content")
    Map<String, ContentItem> modularContent;

    @JsonProperty("pagination")
    Pagination pagination;

    private StronglyTypedContentItemConverter stronglyTypedContentItemConverter;

    ContentItemsListingResponse() {
        //Default constructor
    }

    /**
     * A list of content items
     * @return list of {@link ContentItem} objects
     */
    public List<ContentItem> getItems() {
        return items;
    }

    void setItems(List<ContentItem> items) {
        this.items = items;
        items.forEach(contentItem -> contentItem.setModularContentProvider(this));
    }

    /**
     * A map of content items used in Modular content and Rich text elements
     * @return map of {@link ContentItem} objects
     */
    @Override
    public Map<String, ContentItem> getModularContent() {
        return modularContent;
    }

    void setModularContent(Map<String, ContentItem> modularContent) {
        this.modularContent = modularContent;
        modularContent.values().forEach(contentItem -> contentItem.setModularContentProvider(this));
    }

    /**
     * Information about the retrieved page
     * @return the {@link Pagination} object identifying the current page
     */
    public Pagination getPagination() {
        return pagination;
    }

    void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }

    /**
     * Returns a new instance of List&lt;T&gt; by mapping fields to elements in this content item.  Element fields are
     * mapped by automatically CamelCasing and checking for equality, unless otherwise annotated by an
     * {@link ElementMapping} annotation.  T must have a default constructor and have standard setter methods.
     * @param tClass The class which a new instance should be returned from
     * @param <T> The type of class
     * @return An instance of List&lt;T&gt; with data mapped from the {@link ContentItem} list in this response.
     */
    public <T> List<T> castTo(Class<T> tClass) {
        ArrayList<T> tItems = new ArrayList<>();
        for (ContentItem item : getItems()) {
            tItems.add(stronglyTypedContentItemConverter.convert(item, getModularContent(),tClass));
        }
        return tItems;
    }

    void setStronglyTypedContentItemConverter(StronglyTypedContentItemConverter stronglyTypedContentItemConverter) {
        this.stronglyTypedContentItemConverter = stronglyTypedContentItemConverter;
        for (ContentItem item : getItems()) {
            item.setStronglyTypedContentItemConverter(stronglyTypedContentItemConverter);
        }
        if (modularContent != null) {
            for (ContentItem modularContentItem : modularContent.values()) {
                modularContentItem.setStronglyTypedContentItemConverter(stronglyTypedContentItemConverter);
            }
        }
    }
}
