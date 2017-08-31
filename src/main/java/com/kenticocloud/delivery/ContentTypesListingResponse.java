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

/**
 * Content types listing response
 * <p>
 * When you retrieve a list of content items or content types from your project, the Delivery API returns a
 * {@link ContentTypesListingResponse}
 * @see DeliveryClient#getTypes()
 * @see DeliveryClient#getTypes(List)
 */
public class ContentTypesListingResponse {

    @JsonProperty("types")
    List<ContentType> types;

    @JsonProperty("pagination")
    Pagination pagination;

    ContentTypesListingResponse() {
        //Default constructor
    }

    /**
     * A list of content types
     * @return list of {@link ContentType} objects
     */
    public List<ContentType> getTypes() {
        return types;
    }

    void setTypes(List<ContentType> types) {
        this.types = types;
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
}
