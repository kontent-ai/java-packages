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

import com.kenticocloud.delivery.template.RenderingEngineMissingException;
import com.kenticocloud.delivery.template.TemplateEngineConfig;
import com.kenticocloud.delivery.template.ViewResolverConfiguration;
import com.kenticocloud.delivery.template.thymeleaf.ThymeleafInlineContentItemsResolver;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;

public class RichTextElementConverterTest {

    @Test
    public void testLinkReplacement() {
        RichTextElementConverter converter = new RichTextElementConverter(
                Link::getUrlSlug,
                () -> "/404",
                content -> content,
                null,
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
            public String resolve(CustomItem data) {
                return data.getMessageText();
            }
        });
        RichTextElementConverter converter = new RichTextElementConverter(
                null,
                null,
                null,
                null,
                stronglyTypedContentItemConverter);
        RichTextElement original = new RichTextElement();
        original.setModularContent(Collections.singletonList("donate_with_us"));
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

    @Test
    public void testModularContentReplacementWithDollarSign() {
        StronglyTypedContentItemConverter stronglyTypedContentItemConverter = new StronglyTypedContentItemConverter();
        stronglyTypedContentItemConverter.registerType(CustomItem.class);
        stronglyTypedContentItemConverter.registerInlineContentItemsResolver(new InlineContentItemsResolver<CustomItem>() {
            @Override
            public String resolve(CustomItem data) {
                return data.getMessageText();
            }
        });
        RichTextElementConverter converter = new RichTextElementConverter(
                null,
                null,
                null,
                null,
                stronglyTypedContentItemConverter);
        RichTextElement original = new RichTextElement();
        original.setModularContent(Collections.singletonList("customer_winner"));
        ContentItem contentItem = new ContentItem();
        contentItem.setModularContentProvider(() -> {
            ContentItem customerWinner = new ContentItem();
            System system = new System();
            system.setType("item");
            customerWinner.setSystem(system);
            TextElement textElement = new TextElement();
            textElement.setValue("You are our 1 millionth customer. Click here to claim your $1,000,000!");
            HashMap<String, Element> elements = new HashMap<>();
            elements.put("message_text", textElement);
            customerWinner.setElements(elements);
            customerWinner.setModularContentProvider(HashMap::new);
            customerWinner.setStronglyTypedContentItemConverter(stronglyTypedContentItemConverter);
            HashMap<String, ContentItem> modularContent = new HashMap<>();
            modularContent.put("customer_winner", customerWinner);
            return modularContent;
        });
        contentItem.setStronglyTypedContentItemConverter(stronglyTypedContentItemConverter);
        original.parent = contentItem;
        original.setValue(
                "<p><object type=\"application/kenticocloud\" data-type=\"item\" data-codename=\"customer_winner\"></object></p>");
        RichTextElement converted = converter.convert(original);
        Assert.assertEquals(
                "<p>You are our 1 millionth customer. Click here to claim your $1,000,000!</p>",
                converted.getValue());
    }

    @Test
    public void testRecursiveModularContentReplacement() {
        StronglyTypedContentItemConverter stronglyTypedContentItemConverter = new StronglyTypedContentItemConverter();
        stronglyTypedContentItemConverter.registerType(CustomItem.class);
        stronglyTypedContentItemConverter.registerInlineContentItemsResolver(new InlineContentItemsResolver<CustomItem>() {
            @Override
            public String resolve(CustomItem data) {
                return data.getMessageText();
            }
        });
        RichTextElementConverter converter = new RichTextElementConverter(
                null,
                null,
                null,
                null,
                stronglyTypedContentItemConverter);
        RichTextElement original = new RichTextElement();
        original.setModularContent(Collections.singletonList("donate_with_us"));
        ContentItem contentItem = new ContentItem();
        contentItem.setModularContentProvider(() -> {
            ContentItem parentDonateWithUs = new ContentItem();
            System parentSystem = new System();
            parentSystem.setType("item");
            parentDonateWithUs.setSystem(parentSystem);
            RichTextElement parentTextElement = new RichTextElement();
            parentTextElement.setValue("<object type=\"application/kenticocloud\" data-type=\"item\" data-codename=\"donate_with_us\"></object>");
            HashMap<String, Element> parentElements = new HashMap<>();
            parentElements.put("message_text", parentTextElement);
            parentDonateWithUs.setElements(parentElements);
            parentDonateWithUs.setModularContentProvider(HashMap::new);
            parentDonateWithUs.setStronglyTypedContentItemConverter(stronglyTypedContentItemConverter);

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
            modularContent.put("parent_donate_with_us", parentDonateWithUs);
            modularContent.put("donate_with_us", donateWithUs);
            return modularContent;
        });
        contentItem.setStronglyTypedContentItemConverter(stronglyTypedContentItemConverter);
        original.parent = contentItem;
        original.setValue(
                "<p><object type=\"application/kenticocloud\" data-type=\"item\" data-codename=\"parent_donate_with_us\"></object></p>");
        RichTextElement converted = converter.convert(original);
        Assert.assertEquals(
                "<p>Please donate with us.</p>",
                converted.getValue());
    }

    @Test
    public void testRecursiveModularContentReplacementWithLinks() {
        StronglyTypedContentItemConverter stronglyTypedContentItemConverter = new StronglyTypedContentItemConverter();
        stronglyTypedContentItemConverter.registerType(CustomItem.class);
        stronglyTypedContentItemConverter.registerInlineContentItemsResolver(new InlineContentItemsResolver<CustomItem>() {
            @Override
            public String resolve(CustomItem data) {
                return data.getMessageText();
            }
        });
        RichTextElementConverter converter = new RichTextElementConverter(
                Link::getUrlSlug,
                () -> "/404",
                content -> content,
                null,
                stronglyTypedContentItemConverter);
        RichTextElement original = new RichTextElement();
        original.setModularContent(Collections.singletonList("donate_with_us"));
        ContentItem contentItem = new ContentItem();
        contentItem.setModularContentProvider(() -> {
            ContentItem parentDonateWithUs = new ContentItem();
            System parentSystem = new System();
            parentSystem.setType("item");
            parentDonateWithUs.setSystem(parentSystem);
            RichTextElement parentTextElement = new RichTextElement();
            parentTextElement.setValue("<object type=\"application/kenticocloud\" data-type=\"item\" data-codename=\"donate_with_us\"></object>");
            HashMap<String, Element> parentElements = new HashMap<>();
            parentElements.put("message_text", parentTextElement);
            parentDonateWithUs.setElements(parentElements);
            parentDonateWithUs.setModularContentProvider(HashMap::new);
            parentDonateWithUs.setStronglyTypedContentItemConverter(stronglyTypedContentItemConverter);

            ContentItem donateWithUs = new ContentItem();
            System system = new System();
            system.setType("item");
            donateWithUs.setSystem(system);
            RichTextElement richTextElement = new RichTextElement();
            richTextElement.setValue("<p>Each AeroPress comes with a <a href=\"\" data-item-id=\"65832c4e-8e9c-445f-a001-b9528d13dac8\">pack of filters</a> included in the <a href=\"\" data-item-id=\"not-found\">box</a>.</p>");
            Link link = new Link();
            link.setUrlSlug("/test me/\"<>&\u0080");
            HashMap<String, Link> links = new HashMap<>();
            links.put("65832c4e-8e9c-445f-a001-b9528d13dac8", link);
            richTextElement.setLinks(links);
            HashMap<String, Element> elements = new HashMap<>();
            elements.put("message_text", richTextElement);
            donateWithUs.setElements(elements);
            donateWithUs.setModularContentProvider(HashMap::new);
            donateWithUs.setStronglyTypedContentItemConverter(stronglyTypedContentItemConverter);

            HashMap<String, ContentItem> modularContent = new HashMap<>();
            modularContent.put("parent_donate_with_us", parentDonateWithUs);
            modularContent.put("donate_with_us", donateWithUs);
            return modularContent;
        });
        contentItem.setStronglyTypedContentItemConverter(stronglyTypedContentItemConverter);
        original.parent = contentItem;
        original.setValue(
                "<p><object type=\"application/kenticocloud\" data-type=\"item\" data-codename=\"parent_donate_with_us\"></object></p>");
        RichTextElement converted = converter.convert(original);
        Assert.assertEquals(
                "<p><p>Each AeroPress comes with a <a href=\"/test me/&#34;&#60;&#62;&#38;&#128;\" data-item-id=\"65832c4e-8e9c-445f-a001-b9528d13dac8\">pack of filters</a> included in the <a href=\"/404\" data-item-id=\"not-found\">box</a>.</p></p>",
                converted.getValue());
    }

    @Test
    public void testModularContentReplacementSkippedIfNotThere() {
        StronglyTypedContentItemConverter stronglyTypedContentItemConverter = new StronglyTypedContentItemConverter();
        stronglyTypedContentItemConverter.registerType(CustomItem.class);
        stronglyTypedContentItemConverter.registerInlineContentItemsResolver(new InlineContentItemsResolver<CustomItem>() {
            @Override
            public String resolve(CustomItem data) {
                return data.getMessageText();
            }
        });
        RichTextElementConverter converter = new RichTextElementConverter(
                null,
                null,
                null,
                null,
                stronglyTypedContentItemConverter);
        RichTextElement original = new RichTextElement();
        original.setModularContent(Collections.singletonList("donate_with_us"));
        ContentItem contentItem = new ContentItem();
        contentItem.setModularContentProvider(HashMap::new);
        contentItem.setStronglyTypedContentItemConverter(stronglyTypedContentItemConverter);
        original.parent = contentItem;
        original.setValue(
                "<p><object type=\"application/kenticocloud\" data-type=\"item\" data-codename=\"donate_with_us\"></object></p>");
        RichTextElement converted = converter.convert(original);
        Assert.assertEquals(
                "<p><object type=\"application/kenticocloud\" data-type=\"item\" data-codename=\"donate_with_us\"></object></p>",
                converted.getValue());
    }

    @Test
    public void testRichTextElementResolver() {
        RichTextElementConverter converter = new RichTextElementConverter(
                Link::getUrlSlug,
                () -> "/404",
                content -> "<p>replaced</p>",
                null,
                null);
        RichTextElement original = new RichTextElement();
        original.setValue("<p>Each AeroPress comes with a <a href=\"\" data-item-id=\"65832c4e-8e9c-445f-a001-b9528d13dac8\">pack of filters</a> included in the <a href=\"\" data-item-id=\"not-found\">box</a>.</p>");
        RichTextElement converted = converter.convert(original);
        Assert.assertEquals("<p>replaced</p>", converted.getValue());
    }

    @Test
    public void testTemplateResolver() throws RenderingEngineMissingException {
        ThymeleafInlineContentItemsResolver resolver = new ThymeleafInlineContentItemsResolver();
        ViewResolverConfiguration configuration = new ViewResolverConfiguration()
                .addPrefixes("customTemplateLocation/")
                .setSuffix(".template");
        resolver.getTemplateEngine().setViewResolverConfiguration(configuration);
        TemplateEngineConfig templateEngineConfig = new TemplateEngineConfig();
        templateEngineConfig.setResolvers(Collections.singletonList(resolver));

        StronglyTypedContentItemConverter stronglyTypedContentItemConverter = new StronglyTypedContentItemConverter();
        stronglyTypedContentItemConverter.registerType(CustomItem.class);
        RichTextElementConverter converter = new RichTextElementConverter(
                null,
                null,
                null,
                templateEngineConfig,
                stronglyTypedContentItemConverter);
        RichTextElement original = new RichTextElement();
        original.setModularContent(Collections.singletonList("donate_with_us"));
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
                "<p><custom>Please donate with us.</custom></p>",
                converted.getValue());
    }

    @Test
    public void testRecursiveTemplateResolver() throws RenderingEngineMissingException {
        ThymeleafInlineContentItemsResolver resolver = new ThymeleafInlineContentItemsResolver();
        ViewResolverConfiguration configuration = new ViewResolverConfiguration()
                .addPrefixes("customTemplateLocation/")
                .setSuffix(".template");
        resolver.getTemplateEngine().setViewResolverConfiguration(configuration);
        TemplateEngineConfig templateEngineConfig = new TemplateEngineConfig();
        templateEngineConfig.setResolvers(Collections.singletonList(resolver));

        StronglyTypedContentItemConverter stronglyTypedContentItemConverter = new StronglyTypedContentItemConverter();
        stronglyTypedContentItemConverter.registerType(CustomItem.class);
        RichTextElementConverter converter = new RichTextElementConverter(
                null,
                null,
                null,
                templateEngineConfig,
                stronglyTypedContentItemConverter);
        RichTextElement original = new RichTextElement();
        original.setModularContent(Collections.singletonList("donate_with_us"));
        ContentItem contentItem = new ContentItem();
        contentItem.setModularContentProvider(() -> {
            ContentItem parentDonateWithUs = new ContentItem();
            System parentSystem = new System();
            parentSystem.setType("item");
            parentDonateWithUs.setSystem(parentSystem);
            RichTextElement parentTextElement = new RichTextElement();
            parentTextElement.setValue("<object type=\"application/kenticocloud\" data-type=\"item\" data-codename=\"donate_with_us\"></object>");
            HashMap<String, Element> parentElements = new HashMap<>();
            parentElements.put("message_text", parentTextElement);
            parentDonateWithUs.setElements(parentElements);
            parentDonateWithUs.setModularContentProvider(HashMap::new);
            parentDonateWithUs.setStronglyTypedContentItemConverter(stronglyTypedContentItemConverter);

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
            modularContent.put("parent_donate_with_us", parentDonateWithUs);
            modularContent.put("donate_with_us", donateWithUs);
            return modularContent;
        });
        contentItem.setStronglyTypedContentItemConverter(stronglyTypedContentItemConverter);
        original.parent = contentItem;
        original.setValue(
                "<p><object type=\"application/kenticocloud\" data-type=\"item\" data-codename=\"parent_donate_with_us\"></object></p>");
        RichTextElement converted = converter.convert(original);
        Assert.assertEquals(
                "<p><custom><custom>Please donate with us.</custom></custom></p>",
                converted.getValue());
    }

    @Test
    public void testRecursiveResolverHaltsWithCycle() throws RenderingEngineMissingException {
        ThymeleafInlineContentItemsResolver resolver = new ThymeleafInlineContentItemsResolver();
        ViewResolverConfiguration configuration = new ViewResolverConfiguration()
                .addPrefixes("customTemplateLocation/")
                .setSuffix(".template");
        resolver.getTemplateEngine().setViewResolverConfiguration(configuration);
        TemplateEngineConfig templateEngineConfig = new TemplateEngineConfig();
        templateEngineConfig.setResolvers(Collections.singletonList(resolver));

        StronglyTypedContentItemConverter stronglyTypedContentItemConverter = new StronglyTypedContentItemConverter();
        stronglyTypedContentItemConverter.registerType(CustomItem.class);
        RichTextElementConverter converter = new RichTextElementConverter(
                null,
                null,
                null,
                templateEngineConfig,
                stronglyTypedContentItemConverter);
        RichTextElement original = new RichTextElement();
        original.setModularContent(Collections.singletonList("donate_with_us"));
        ContentItem contentItem = new ContentItem();
        contentItem.setModularContentProvider(() -> {
            ContentItem parentDonateWithUs = new ContentItem();
            System parentSystem = new System();
            parentSystem.setType("item");
            parentDonateWithUs.setSystem(parentSystem);
            RichTextElement parentTextElement = new RichTextElement();
            parentTextElement.setValue("<object type=\"application/kenticocloud\" data-type=\"item\" data-codename=\"donate_with_us\"></object>");
            HashMap<String, Element> parentElements = new HashMap<>();
            parentElements.put("message_text", parentTextElement);
            parentDonateWithUs.setElements(parentElements);
            parentDonateWithUs.setModularContentProvider(HashMap::new);
            parentDonateWithUs.setStronglyTypedContentItemConverter(stronglyTypedContentItemConverter);

            ContentItem donateWithUs = new ContentItem();
            System system = new System();
            system.setType("item");
            donateWithUs.setSystem(system);
            TextElement textElement = new TextElement();
            textElement.setValue("<object type=\"application/kenticocloud\" data-type=\"item\" data-codename=\"parent_donate_with_us\"></object>");
            HashMap<String, Element> elements = new HashMap<>();
            elements.put("message_text", textElement);
            donateWithUs.setElements(elements);
            donateWithUs.setModularContentProvider(HashMap::new);
            donateWithUs.setStronglyTypedContentItemConverter(stronglyTypedContentItemConverter);

            HashMap<String, ContentItem> modularContent = new HashMap<>();
            modularContent.put("parent_donate_with_us", parentDonateWithUs);
            modularContent.put("donate_with_us", donateWithUs);
            return modularContent;
        });
        contentItem.setStronglyTypedContentItemConverter(stronglyTypedContentItemConverter);
        original.parent = contentItem;
        original.setValue(
                "<p><object type=\"application/kenticocloud\" data-type=\"item\" data-codename=\"parent_donate_with_us\"></object></p>");
        RichTextElement converted = converter.convert(original);
        Assert.assertEquals(
                "<p><custom><custom><object type=\"application/kenticocloud\" data-type=\"item\" data-codename=\"parent_donate_with_us\"></object></custom></custom></p>",
                converted.getValue());
    }
}
