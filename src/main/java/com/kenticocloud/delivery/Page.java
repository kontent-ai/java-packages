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

import java.io.IOException;
import java.util.List;

public class Page<T> {

    Pagination pagination;
    DeliveryClient deliveryClient;
    List<T> contentItems;
    Class<T> tClass;

    public Page(ContentItemsListingResponse response, Class<T> tClass, DeliveryClient deliveryClient) {
        this.pagination = response.getPagination();
        this.contentItems = response.castTo(tClass);
        this.tClass = tClass;
        this.deliveryClient = deliveryClient;
    }

    /**
     * Returns the size of the page.
     *
     * @return the size of the page.
     */
    public int getSize() {
        return pagination.getCount();
    }

    /**
     * Returns the page content as {@link List}.
     *
     * @return the list of content
     */
    public List<T> getContent() {
        return contentItems;
    }

    /**
     * Returns whether the page has content at all.
     *
     * @return true if the page has content
     */
    public boolean hasContent() {
        return contentItems != null && !contentItems.isEmpty();
    }

    /**
     * Returns whether the current page is the first one.
     *
     * @return true if it is the first
     */
    public boolean isFirst() {
        return pagination.skip == 0;
    }

    /**
     * Returns whether the current page is the last one.
     *
     * @return
     */
    public boolean isLast() {
        return pagination.nextPage.isEmpty();
    }

    /**
     * Returns if there is a next page.
     *
     * @return if there is a next page.
     */
    public boolean hasNext() {
        return !pagination.nextPage.isEmpty();
    }

    /**
     * Returns if there is a previous page.
     *
     * @return if there is a previous page.
     */
    public boolean hasPrevious() {
        return pagination.skip > 0;
    }

    /**
     * Returns the next page. Can be {@literal null} in case the current page is already the last one. Clients
     * should check {@link #hasNext()} before calling this method to make sure they receive a non-{@literal null} value.
     *
     * @return the next page.
     */
    public Page<T> nextPage() throws IOException {
        return deliveryClient.getNextPage(this);
    }

    /**
     * Returns the pagination information from the request
     * @return a pagination object
     */
    public Pagination getPagination() {
        return pagination;
    }

    protected Class<T> getType() {
        return tClass;
    }
}
