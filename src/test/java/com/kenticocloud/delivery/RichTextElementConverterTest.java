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

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;

public class RichTextElementConverterTest {

    @Test
    public void testLinkReplacement() {
        RichTextElementConverter converter = new RichTextElementConverter(
                link -> link.getUrlSlug(),
                () -> "/404",
                new StronglyTypedContentItemConverter(),
                null);
        RichTextElement original = new RichTextElement();
        original.setValue("<p>Each AeroPress comes with a <a href=\"\" data-item-id=\"65832c4e-8e9c-445f-a001-b9528d13dac8\">pack of filters</a> included in the <a href=\"\" data-item-id=\"not-found\">box</a>.</p>");
        Link link = new Link();
        link.setUrlSlug("/test me/\"<>&\u0080");
        HashMap<String, Link> links = new HashMap<>();
        links.put("65832c4e-8e9c-445f-a001-b9528d13dac8", link);
        original.setLinks(links);
        RichTextElement converted = converter.convert(original);
        Assert.assertEquals(
                "<p>Each AeroPress comes with a <a href=\"/test me/&#34;&#60;&#62;&#38;&#128;\" data-item-id=\"65832c4e-8e9c-445f-a001-b9528d13dac8\">pack of filters</a> included in the <a href=\"/404\" data-item-id=\"not-found\">box</a>.</p>",
                converted.getValue());
    }

    @Test
    public void testModularContentReplacement() {
        StronglyTypedContentItemConverter stronglyTypedContentItemConverter = new StronglyTypedContentItemConverter();
        stronglyTypedContentItemConverter.registerType(CustomItem.class);
        stronglyTypedContentItemConverter.registerInlineContentItemsResolver(new InlineContentItemsResolver<CustomItem>() {
            @Override
            String resolve(CustomItem data) {
                return data.getMessageText();
            }
        });
        RichTextElementConverter converter = new RichTextElementConverter(
                null,
                null,
                stronglyTypedContentItemConverter,
                null);
        RichTextElement original = new RichTextElement();
        original.setModularContent(Arrays.asList("donate_with_us"));
        ContentItem contentItem = new ContentItem();
        contentItem.setModularContentProvider(() -> {
            ContentItem donateWithUs = new ContentItem();
            System system = new System();
            system.setType("item");
            donateWithUs.setSystem(system);
            TextElement textElement = new TextElement();
            textElement.setValue("Please donate with us.");
            HashMap<String, Element> elements = new HashMap<>();
            elements.put("message_text", textElement);
            donateWithUs.setElements(elements);
            donateWithUs.setModularContentProvider(HashMap::new);
            donateWithUs.setStronglyTypedContentItemConverter(stronglyTypedContentItemConverter);
            HashMap<String, ContentItem> modularContent = new HashMap<>();
            modularContent.put("donate_with_us", donateWithUs);
            return modularContent;
        });
        contentItem.setStronglyTypedContentItemConverter(stronglyTypedContentItemConverter);
        original.parent = contentItem;
        original.setValue(
                "<p><object type=\"application/kenticocloud\" data-type=\"item\" data-codename=\"donate_with_us\"></object></p>");
        RichTextElement converted = converter.convert(original);
        Assert.assertEquals(
                "<p>Please donate with us.</p>",
                converted.getValue());
    }
}
