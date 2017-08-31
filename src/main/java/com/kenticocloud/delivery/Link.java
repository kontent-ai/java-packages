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
 * Object model for Link elements
 * <p>
 * Links associated with rich text elements
 * @see RichTextElement
 */
public class Link {

    @JsonProperty("type")
    String type;

    @JsonProperty("codename")
    String codename;

    @JsonProperty("url_slug")
    String urlSlug;

    Link() {
        //Default constructor
    }

    /**
     * Content type of the content item
     * @return the content item type codename
     */
    public String getType() {
        return type;
    }

    void setType(String type) {
        this.type = type;
    }

    /**
     * Display name of the element
     * @return the codename of the link element
     */
    public String getCodename() {
        return codename;
    }

    void setCodename(String codename) {
        this.codename = codename;
    }

    /**
     * URL slug of the content item
     * <p>
     * Empty string if the content item's type does not use a URL slug element
     * @return URL slug of the content item
     */
    public String getUrlSlug() {
        return urlSlug;
    }

    void setUrlSlug(String urlSlug) {
        this.urlSlug = urlSlug;
    }
}
