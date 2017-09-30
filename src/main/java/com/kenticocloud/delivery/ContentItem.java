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

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Object model description of a single content item object.
 */
public class ContentItem {

    @JsonProperty("system")
    System system;

    @JsonProperty("elements")
    Map<String, Element> elements;

    ModularContentProvider modularContentProvider;

    private StronglyTypedContentItemConverter stronglyTypedContentItemConverter;

    ContentItem() {
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
        elements.forEach((s, element) -> element.setParent(this));
    }

    /**
     * Convenience method to get the value of a Text or Rich text element without traversing the Elements map.
     * @param codeName the element codeName to get the value of
     * @return The value of the element.  Returns null if the element does not exist, or if it is not a
     * {@link TextElement} or {@link RichTextElement}
     */
    public String getString(String codeName) {
        Element element = elements.get(codeName);
        if (element == null) {
            return null;
        }
        if (!(element instanceof TextElement)) {
            return null;
        }
        return ((TextElement) element).getValue();
    }

    /**
     * Convenience method to get the value of an Assets element without traversing the Elements map.
     * @param codeName the element codeName to get the Asset list of
     * @return A list of {@link Asset} objects.  Returns an empty collection if the element does not exist, or if it is
     * not an {@link AssetsElement}
     */
    public List<Asset> getAssets(String codeName) {
        Element element = elements.get(codeName);
        if (element == null) {
            return Collections.emptyList();
        }
        if (!(element instanceof AssetsElement)) {
            return Collections.emptyList();
        }
        return ((AssetsElement) element).getValue();
    }

    /**
     * Convenience method to retrieve the ContentItem of modular content.
     * @param codeName the {@link ContentItem} codeName
     * @return The {@link ContentItem}.  Returns null if it was not included in the response.
     */
    public ContentItem getModularContent(String codeName) {
        //This shouldn't happen if this is deserialized from Jackson, but protecting against the NPE for unexpected
        //usages.
        if (modularContentProvider == null) {
            return null;
        }
        return modularContentProvider.getModularContent().get(codeName);
    }

    /**
     * Returns a new instance of T by mapping fields to elements in this content item.  Element fields are mapped
     * by automatically CamelCasing and checking for equality, unless otherwise annotated by an {@link ElementMapping}
     * annotation.  T must have a default constructor and have standard setter methods.
     * When passing in Object.class, the type returned will be an instance of the class registered with the
     * {@link DeliveryClient} that is annotated with {@link ContentItemMapping} that matches the contentType of this
     * ContentItem (however still returned as type Object).
     * @param tClass The class which a new instance should be returned from
     * @param <T> The type of class
     * @return An instance of T with data mapped from the {@link ContentItem} in this response.
     */
    public <T> T castTo(Class<T> tClass) {
        return stronglyTypedContentItemConverter.convert(this, modularContentProvider.getModularContent(), tClass);
    }

    /**
     * Returns a new instance by mapping fields to elements in this content item.  Element fields are mapped
     * by automatically CamelCasing and checking for equality, unless otherwise annotated by an {@link ElementMapping}
     * annotation.  The type returned will be an instance of the class registered with the {@link DeliveryClient} that
     * is annotated with {@link ContentItemMapping} that matches the contentType of this ContentItem (however still
     * returned as type Object).
     * @see #castTo(Class)
     * @return An instance with data mapped from the {@link ContentItem} in this response.
     */
    public Object castToDefault() {
        return this.castTo(Object.class);
    }

    /**
     * Returns a new instance by mapping fields to elements in this content item.  Element fields are mapped
     * by automatically CamelCasing and checking for equality, unless otherwise annotated by an {@link ElementMapping}
     * annotation.  The type returned will be an instance of the class registered with the {@link DeliveryClient} that
     * that matches the contentType provided.
     * @param contentType The contentType to match this ContentItem too.
     * @return An instance with data mapped from the {@link ContentItem} in this response.
     */
    public Object castTo(String contentType) {
        return stronglyTypedContentItemConverter.convert(
                this, modularContentProvider.getModularContent(), contentType);
    }

    void setModularContentProvider(ModularContentProvider modularContentProvider) {
        this.modularContentProvider = modularContentProvider;
    }

    void setStronglyTypedContentItemConverter(StronglyTypedContentItemConverter stronglyTypedContentItemConverter) {
        this.stronglyTypedContentItemConverter = stronglyTypedContentItemConverter;
    }
}
