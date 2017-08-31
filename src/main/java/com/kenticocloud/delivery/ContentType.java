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
 */
public class ContentType {

    @JsonProperty("system")
    System system;

    @JsonProperty("elements")
    Map<String, Element> elements;

    ContentType() {
        //Default constructor
    }

    /**
     * {@link System} attributes of the content item
     * @return {@link System} attributes of the content item
     */
    public System getSystem() {
        return system;
    }

    void setSystem(System system) {
        this.system = system;
    }

    /**
     * Content type elements in the content item
     * @return map of {@link Element} objects
     */
    public Map<String, Element> getElements() {
        return elements;
    }

    void setElements(Map<String, Element> elements) {
        this.elements = elements;
    }

}
