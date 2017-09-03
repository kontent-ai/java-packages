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
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.List;

/**
 * Parent object model of individual elements
 * <p>
 * When retrieving content items or content types, you get an elements collection as a part of the retrieved item or
 * type.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = TextElement.class, name = TextElement.TYPE_VALUE),
    @JsonSubTypes.Type(value = RichTextElement.class, name = RichTextElement.TYPE_VALUE),
    @JsonSubTypes.Type(value = MultipleChoiceElement.class, name = MultipleChoiceElement.TYPE_VALUE),
    @JsonSubTypes.Type(value = NumberElement.class, name = NumberElement.TYPE_VALUE),
    @JsonSubTypes.Type(value = DateTimeElement.class, name = DateTimeElement.TYPE_VALUE),
    @JsonSubTypes.Type(value = AssetsElement.class, name = AssetsElement.TYPE_VALUE),
    @JsonSubTypes.Type(value = ModularContentElement.class, name = ModularContentElement.TYPE_VALUE),
    @JsonSubTypes.Type(value = TaxonomyElement.class, name = TaxonomyElement.TYPE_VALUE),
    @JsonSubTypes.Type(value = UrlSlugElement.class, name = UrlSlugElement.TYPE_VALUE)
})
public abstract class Element {

    @JsonProperty("type")
    String type;

    @JsonProperty("name")
    String name;

    @JsonProperty("codename")
    String codeName;

    ContentItem parent;

    Element() {
        //Default constructor
    }

    /**
     * Type of the element
     * <p>
     * Valid values: text, rich_text, number, multiple_choice, date_time, asset, modular_content, taxonomy, url_slug.
     * @return the codename for this element type
     */
    public String getType() {
        return type;
    }

    void setType(String type) {
        this.type = type;
    }

    /**
     * Display name of the element
     * @return the display name of this element
     */
    public String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    /**
     * The codename for this element
     * <p>
     * Note: This is only populated when querying an individual content type element.
     * @return codename for this content type element as defined in a content type
     * @see DeliveryClient#getContentTypeElement(String, String)
     * @see DeliveryClient#getContentTypeElement(String, String, List)
     */
    public String getCodeName() {
        return codeName;
    }

    void setCodeName(String codeName) {
        this.codeName = codeName;
    }

    void setParent(ContentItem parent) {
        this.parent = parent;
    }

    abstract <T> T getValue();
}
