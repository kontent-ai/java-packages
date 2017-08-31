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
public class ContentItemResponse {

    @JsonProperty("item")
    ContentItem item;

    @JsonProperty("modular_content")
    Map<String, ContentItem> modularContent;

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
    }

    /**
     * A map of content items used in Modular content and Rich text elements
     * @return map of {@link ContentItem} objects
     */
    public Map<String, ContentItem> getModularContent() {
        return modularContent;
    }

    void setModularContent(Map<String, ContentItem> modularContent) {
        this.modularContent = modularContent;
    }
}
