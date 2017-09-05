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

import java.util.List;
import java.util.Map;

/**
 * Content item listing response
 * <p>
 * When retrieving a single content item, the Delivery API returns a {@link ContentItemResponse}
 * @see DeliveryClient#getItem(String)
 * @see DeliveryClient#getItem(String, List)
 */
public class ContentItemResponse implements ModularContentProvider {

    @JsonProperty("item")
    ContentItem item;

    @JsonProperty("modular_content")
    Map<String, ContentItem> modularContent;

    private StronglyTypedContentItemConverter stronglyTypedContentItemConverter;

    ContentItemResponse() {
        //Default constructor
    }

    /**
     * The {@link ContentItem} response
     * @return the {@link ContentItem} requested
     */
    public ContentItem getItem() {
        return item;
    }

    void setItem(ContentItem item) {
        this.item = item;
        item.setModularContentProvider(this);
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
    }

    /**
     * Returns a new instance of T by mapping fields to elements in this content item.  Element fields are mapped
     * by automatically CamelCasing and checking for equality, unless otherwise annotated by an {@link ElementMapping}
     * annotation.  T must have a default constructor and have standard setter methods.
     * @param tClass The class which a new instance should be returned from
     * @param <T> The type of class
     * @return An instance of T with data mapped from the {@link ContentItem} in this response.
     */
    public <T> T castTo(Class<T> tClass) {
        return stronglyTypedContentItemConverter.convert(item, getModularContent(), tClass);
    }

    void setStronglyTypedContentItemConverter(StronglyTypedContentItemConverter stronglyTypedContentItemConverter) {
        this.stronglyTypedContentItemConverter = stronglyTypedContentItemConverter;
        item.setStronglyTypedContentItemConverter(stronglyTypedContentItemConverter);
        if (modularContent != null) {
            for (ContentItem modularContentItem : modularContent.values()) {
                modularContentItem.setStronglyTypedContentItemConverter(stronglyTypedContentItemConverter);
            }
        }
    }
}
