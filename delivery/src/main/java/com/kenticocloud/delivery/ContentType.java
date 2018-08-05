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

import java.util.Map;

/**
 * Object model description of a single content type object.
 * <p>
 * Also serves as the response from an invocation of {@link DeliveryClient#getType(String)}.
 *
 * @see <a href="https://developer.kenticocloud.com/v1/reference#content-type-object">
 *      KenticoCloud API reference - Content type object</a>
 * @see <a href="https://developer.kenticocloud.com/v1/reference#view-a-content-type">
 *      KenticoCloud API reference - View a content type</a>
 */
@lombok.Data
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@lombok.Builder
public class ContentType {

    /**
     * {@link System} attributes of the content type.
     *
     * @param system    New value for System attributes of this content type.
     * @return          The System attributes of this content type.
     */
    @JsonProperty("system")
    System system;

    /**
     * Content type elements in the content type.  These are keyed by the codename of the element.
     * <p>
     * Note: The order of the {@link Element} objects might not match the order in the KenticoCloud UI.
     *
     * @param elements  New value of this ContentType's {@link Element} objects.
     * @return          Map of this ContentType's {@link Element} objects.
     */
    @JsonProperty("elements")
    Map<String, Element> elements;
}
