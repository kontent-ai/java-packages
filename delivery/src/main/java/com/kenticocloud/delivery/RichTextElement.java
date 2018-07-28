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
 * Object model for Rich text elements
 */
public class RichTextElement extends TextElement {

    static final String TYPE_VALUE = "rich_text";

    @JsonProperty("images")
    Map<String, Image> images;

    @JsonProperty("links")
    Map<String, Link> links;

    @JsonProperty("modular_content")
    List<String> modularContent;

    public RichTextElement() {
        setType(TYPE_VALUE);
    }

    /**
     * The value of a Rich text element is formatted text.
     * <p>
     * Besides formatted text in the element's value attribute, Rich text elements can contain inline images and links
     * to other content items.
     * @return formatted text
     */
    @Override
    public String getValue() {
        return value;
    }

    /**
     * Images associated with this rich text element.
     * @return a map of {@link Image} objects
     * @see Image
     */
    public Map<String, Image> getImages() {
        return images;
    }

    public void setImages(Map<String, Image> images) {
        this.images = images;
    }

    /**
     * Links associated with this rich text element.
     * <p>
     * Each object in the links collection represents a content item ID in the GUID format,
     * e.g., f4b3fc05-e988-4dae-9ac1-a94aba566474.
     * @return a map of {@link Link} objects
     */
    public Map<String, Link> getLinks() {
        return links;
    }

    public void setLinks(Map<String, Link> links) {
        this.links = links;
    }

    /**
     * A list of {@link ContentItem} objects inserted into rich text as modules.
     * <p>
     * The list is represented as an array of strings, each string being a codename of a content item.
     * @return a list of {@link ContentItem} codenames
     */
    public List<String> getModularContent() {
        return modularContent;
    }

    public void setModularContent(List<String> modularContent) {
        this.modularContent = modularContent;
    }
}
