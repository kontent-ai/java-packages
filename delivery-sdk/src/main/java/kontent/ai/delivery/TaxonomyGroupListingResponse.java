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

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Content type listing response from an invocation of {@link DeliveryClient#getTypes()}, or
 * {@link DeliveryClient#getTypes(List)}.
 *
 * @see <a href="https://docs.kontent.ai/reference/delivery-api#operation/list-taxonomy-groups">
 *      Kontent API reference - List taxonomy groups</a>
 * @see <a href="https://docs.kontent.ai/reference/delivery-api#section/Taxonomy-group-object">
 *      Kontent API reference - Taxonomy group model</a>
 * @see ContentType
 * @see DeliveryClient#getTypes()
 * @see DeliveryClient#getTypes(List)
 */
@lombok.Data
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@lombok.Builder
public class TaxonomyGroupListingResponse {

    /**
     * A list of taxonomy groups
     *
     * @param taxonomies    Sets the taxonomies of this.
     * @return              List of {@link TaxonomyGroup}.
     */
    @JsonProperty("taxonomies")
    List<TaxonomyGroup> taxonomies;

    /**
     * Information about the retrieved page.
     *
     * @param pagination    Sets the pagination of this.
     * @return              The {@link Pagination} object identifying the current page.
     */
    @JsonProperty("pagination")
    Pagination pagination;

}
