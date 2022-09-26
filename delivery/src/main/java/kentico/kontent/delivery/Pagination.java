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

package kentico.kontent.delivery;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Pagination object
 */
@lombok.Getter
@lombok.Setter
@lombok.ToString
@lombok.EqualsAndHashCode
@lombok.NoArgsConstructor
public class Pagination {

    /**
     * Number of content items skipped from the response
     *
     * @param skip  Sets skip on this.
     * @return      Reflects the value set by the {@link DeliveryParameterBuilder#page(Integer, Integer)} query
     *              parameter.
     */
    @JsonProperty("skip")
    Integer skip;

    /**
     * Number of content items returned from the response
     *
     * @param limit Sets limit on this.
     * @return      Reflects the value set by the {@link DeliveryParameterBuilder#page(Integer, Integer)} query
     *              parameter.
     */
    @JsonProperty("limit")
    Integer limit;

    /**
     * Number of retrieved content items
     * <p>
     * If the limit and skip query parameters ({@link DeliveryParameterBuilder#page(Integer, Integer)}) aren't set, the
     * count attribute will contain the total number of content items matching the specified filtering parameters.
     *
     * @param count Sets count on this.
     * @return      Number of retrieved content items.
     */
    @JsonProperty("count")
    Integer count;

    /**
     * Number of total items matching the search criteria
     * <p>
     * Is set only when {@link DeliveryParameterBuilder#includeTotalCount()} used for loading {@link ContentItemsListingResponse}.
     *
     * @param totalCount    Sets total_count on this.
     * @return              Number of total content items matching the search criteria or null.
     */
    @JsonProperty("total_count")
    Integer totalCount;

    /**
     * URL to the next page of results.  Generally speaking, this value will not be needed.
     * Consider using {@link DeliveryClient#getNextPage(Page)}.
     *
     * @param nextPage  Sets nextPage on this.
     * @return          URL to the next page of results.
     */
    @JsonProperty("next_page")
    String nextPage;

}
