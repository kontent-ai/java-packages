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

package kontent.ai.delivery;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Object model description of a single content item object.
 *
 * @see <a href="https://docs.kontent.ai/reference/delivery-api#section/Content-item-object">
 *      Kontent API reference - Content item object</a>
 */
@lombok.Data
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@lombok.Builder
@lombok.EqualsAndHashCode(exclude = {"linkedItemProvider", "stronglyTypedContentItemConverter"})
@lombok.ToString(exclude = {"linkedItemProvider", "stronglyTypedContentItemConverter"})
public class ContentItem {

    /**
     * {@link System} attributes of the content item.
     *
     * @param system    New value for System attributes of this content item.
     * @return          The System attributes of this content item.
     */
    @JsonProperty("system")
    System system;

    /**
     * Content type elements in the content item.  These are keyed by the codename of the element.
     * <p>
     * Note: The order of the {@link Element} objects might not match the order in the Kontent UI.
     *
     * @return Map of this ContentItem's {@link Element} objects.
     */
    @JsonProperty("elements")
    Map<String, Element> elements;

    @JsonIgnore
    LinkedItemProvider linkedItemProvider;

    @JsonIgnore
    private StronglyTypedContentItemConverter stronglyTypedContentItemConverter;

    /**
     * Content type elements in the content item.  These are keyed by the codename of the element.
     * <p>
     * Note: The order of the {@link Element} objects might not match the order in the Kontent UI.
     *
     * @param elements New value for this ContentItem's {@link Element} objects.
     */
    public void setElements(Map<String, Element> elements) {
        this.elements = elements;
        elements.forEach((s, element) -> element.setParent(this));
    }

    /**
     * Convenience method to get the value of a Text or Rich text element without traversing the Elements map.
     *
     * @param codename  The element codename to retrieve the String rendering of from this ContentItem.
     * @return          The value of the element.  Returns null if the element does not exist, or if it is not a
     *                  {@link TextElement} or {@link RichTextElement}.
     */
    public String getString(String codename) {
        Element element = elements.get(codename);
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
     *
     * @param codename  The element codename to get the Asset list of from this ContentItem.
     * @return          A list of {@link Asset} objects.  Returns an empty collection if the element does not exist, or
     *                  if it is not an {@link AssetsElement}.
     */
    public List<Asset> getAssets(String codename) {
        Element element = elements.get(codename);
        if (element == null) {
            return Collections.emptyList();
        }
        if (!(element instanceof AssetsElement)) {
            return Collections.emptyList();
        }
        return ((AssetsElement) element).getValue();
    }

    /**
     * Convenience method to retrieve the ContentItem from linked items.
     *
     * @param codename  The {@link ContentItem} codename of the linked item.
     * @return          The {@link ContentItem}.  Returns null if it was not included in the response.
     */
    public ContentItem getLinkedItem(String codename) {
        //This shouldn't happen if this is de-serialized from Jackson, but protecting against the NPE for unexpected
        //usages.
        if (linkedItemProvider == null) {
            return null;
        }
        return linkedItemProvider.getLinkedItems().get(codename);
    }

    /**
     * Returns a new instance of T by mapping fields to elements in this content item.  Element fields are mapped
     * by automatically CamelCasing and checking for equality, unless otherwise annotated by an {@link ElementMapping}
     * annotation.  T must have a default constructor and have standard setter methods.
     * When passing in Object.class, the type returned will be an instance of the class registered with the
     * {@link DeliveryClient} that is annotated with {@link ContentItemMapping} that matches the
     * {@link System#type} of this ContentItem (however still returned as type Object).
     * <p>
     * If {@link Object} is passed in, the {@link StronglyTypedContentItemConverter} will cast this ContentItem to a
     * type that is mapped this ContentItem's {@link System#type} from a previous registration via the registration has
     * been done, then this same instance of ContentItem will be returned.  Invoking {@link #castToDefault()} is the
     * same as invoking this method with {@link Object}.
     *
     * @param tClass    The class which a new instance should be returned from using this ContentItem.
     * @param <T>       The type of class which will be returned.
     * @return          An instance of T with data mapped from this {@link ContentItem}.
     * @see             DeliveryClient#registerType(Class)
     * @see             DeliveryClient#registerType(String, Class)
     * @see             ContentItemMapping
     * @see             ElementMapping
     * @see             StronglyTypedContentItemConverter
     */
    public <T> T castTo(Class<T> tClass) {
        return stronglyTypedContentItemConverter.convert(this, linkedItemProvider.getLinkedItems(), tClass);
    }

    /**
     * Returns a new instance by mapping fields to elements in this content item.  Element fields are mapped
     * by automatically CamelCasing and checking for equality, unless otherwise annotated by an {@link ElementMapping}
     * annotation.  The type returned will be an instance of the class registered with the {@link DeliveryClient} that
     * is annotated with {@link ContentItemMapping} that matches the {@link System#type} of this ContentItem
     * (however still returned as type Object).
     * <p>
     * If no registration has been done, then this same instance of ContentItem will be returned.
     *
     * @return  An instance with data mapped from this {@link ContentItem}.
     * @see     #castTo(Class)
     * @see     DeliveryClient#registerType(Class)
     * @see     DeliveryClient#registerType(String, Class)
     * @see     ContentItemMapping
     * @see     ElementMapping
     * @see     StronglyTypedContentItemConverter
     */
    public Object castToDefault() {
        return this.castTo(Object.class);
    }

    /**
     * Returns a new instance by mapping fields to elements in this content item.  Element fields are mapped
     * by automatically CamelCasing and checking for equality, unless otherwise annotated by an {@link ElementMapping}
     * annotation.  The type returned will be an instance of the class registered with the {@link DeliveryClient} that
     * that matches the {@link System#type} provided.
     * <p>
     * If no registration has been done, then this same instance of ContentItem will be returned.
     *
     * @param contentItemSystemType The contentItemSystemType to match this ContentItem too.
     * @return                      An instance with data mapped from this {@link ContentItem}.
     * @see                         #castTo(Class)
     * @see                         DeliveryClient#registerType(Class)
     * @see                         DeliveryClient#registerType(String, Class)
     * @see                         ContentItemMapping
     * @see                         ElementMapping
     * @see                         StronglyTypedContentItemConverter
     */
    public Object castTo(String contentItemSystemType) {
        return stronglyTypedContentItemConverter.convert(
            this, linkedItemProvider.getLinkedItems(), contentItemSystemType);
    }

    void setLinkedItemProvider(LinkedItemProvider linkedItemProvider) {
        this.linkedItemProvider = linkedItemProvider;
    }

    void setStronglyTypedContentItemConverter(StronglyTypedContentItemConverter stronglyTypedContentItemConverter) {
        this.stronglyTypedContentItemConverter = stronglyTypedContentItemConverter;
    }
}
