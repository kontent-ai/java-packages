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

/**
 * Pagination object
 */
public class Pagination {

    @JsonProperty("skip")
    Integer skip;

    @JsonProperty("limit")
    Integer limit;

    @JsonProperty("count")
    Integer count;

    @JsonProperty("next_page")
    String nextPage;

    /**
     * Number of content items skipped from the response
     * @return Reflects the value set by the {@link DeliveryParameterBuilder#page(Integer, Integer)} query parameter
     */
    public Integer getSkip() {
        return skip;
    }

    public void setSkip(Integer skip) {
        this.skip = skip;
    }

    /**
     * Number of content items returned from the response
     * @return Reflects the value set by the {@link DeliveryParameterBuilder#page(Integer, Integer)} query parameter
     */
    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    /**
     * Number of retrieved content items
     * <p>
     * If the limit and skip query parameters ({@link DeliveryParameterBuilder#page(Integer, Integer)}) aren't set, the
     * count attribute will contain the total number of content items matching the specified filtering parameters.
     * @return Number of retrieved content items
     */
    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    /**
     * URL to the next page of results.
     * @return URL to the next page of results (note, support for this in the API is still WIP)
     */
    public String getNextPage() {
        return nextPage;
    }

    public void setNextPage(String nextPage) {
        this.nextPage = nextPage;
    }
}
