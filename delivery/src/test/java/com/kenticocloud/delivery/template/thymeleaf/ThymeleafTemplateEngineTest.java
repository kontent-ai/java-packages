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

package com.kenticocloud.delivery.template.thymeleaf;

import com.kenticocloud.delivery.ArticleItem;
import com.kenticocloud.delivery.ContentItem;
import com.kenticocloud.delivery.ContentItemMapping;
import com.kenticocloud.delivery.CustomItem;
import com.kenticocloud.delivery.System;
import com.kenticocloud.delivery.template.RenderingEngineMissingException;
import com.kenticocloud.delivery.template.TemplateEngineModel;
import com.kenticocloud.delivery.template.ViewResolverConfiguration;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

public class ThymeleafTemplateEngineTest {

    @Test
    public void testResolution() throws RenderingEngineMissingException {
        ThymeleafInlineContentItemsResolver resolver = new ThymeleafInlineContentItemsResolver();
        resolver.getTemplateEngine().setViewResolverConfiguration(new ViewResolverConfiguration());
        ArticleItem articleItem = new ArticleItem();
        articleItem.setTitle("Test");
        TemplateEngineModel templateEngineModel = new TemplateEngineModel();
        templateEngineModel.setInlineContentItem(articleItem);
        Assert.assertTrue(resolver.supports(templateEngineModel));
        Assert.assertEquals("<p>Test</p>", resolver.resolve(templateEngineModel));
    }

    @Test
    public void testResolutionContentItem() throws RenderingEngineMissingException {
        ThymeleafInlineContentItemsResolver resolver = new ThymeleafInlineContentItemsResolver();
        resolver.getTemplateEngine().setViewResolverConfiguration(new ViewResolverConfiguration());
        ContentItem item = new ContentItem();
        System system = new System();
        system.setType("coffee");
        system.setName("Coffee name");
        item.setSystem(system);
        TemplateEngineModel templateEngineModel = new TemplateEngineModel();
        templateEngineModel.setInlineContentItem(item);
        Assert.assertTrue(resolver.supports(templateEngineModel));
        Assert.assertEquals("<coffee>Coffee name</coffee>", resolver.resolve(templateEngineModel));
    }

    @Test
    public void testResolutionNonAnnotatedStronglyTypedModel() throws RenderingEngineMissingException {
        ThymeleafInlineContentItemsResolver resolver = new ThymeleafInlineContentItemsResolver();
        resolver.getTemplateEngine().setViewResolverConfiguration(new ViewResolverConfiguration());
        Coffee item = new Coffee();
        System system = new System();
        system.setType("coffee");
        system.setName("Coffee name");
        item.setSystem(system);
        TemplateEngineModel templateEngineModel = new TemplateEngineModel();
        templateEngineModel.setInlineContentItem(item);
        Assert.assertTrue(resolver.supports(templateEngineModel));
        Assert.assertEquals("<coffee>Coffee name</coffee>", resolver.resolve(templateEngineModel));
    }

    @Test
    public void testSupportsFailsWhenNoTemplate() throws RenderingEngineMissingException {
        ThymeleafInlineContentItemsResolver resolver = new ThymeleafInlineContentItemsResolver();
        resolver.getTemplateEngine().setViewResolverConfiguration(new ViewResolverConfiguration());
        ClassWithoutTemplate model = new ClassWithoutTemplate();
        TemplateEngineModel templateEngineModel = new TemplateEngineModel();
        templateEngineModel.setInlineContentItem(model);
        Assert.assertFalse(resolver.supports(templateEngineModel));
    }

    @Test
    public void testSupportsFailsWhenNoMapping() throws RenderingEngineMissingException {
        ThymeleafInlineContentItemsResolver resolver = new ThymeleafInlineContentItemsResolver();
        resolver.getTemplateEngine().setViewResolverConfiguration(new ViewResolverConfiguration());
        ClassWithoutAnyMapping model = new ClassWithoutAnyMapping();
        TemplateEngineModel templateEngineModel = new TemplateEngineModel();
        templateEngineModel.setInlineContentItem(model);
        Assert.assertFalse(resolver.supports(templateEngineModel));
    }

    @Test
    public void testAddingCustomTemplateLocation() throws RenderingEngineMissingException {
        ThymeleafInlineContentItemsResolver resolver = new ThymeleafInlineContentItemsResolver();
        ViewResolverConfiguration configuration = new ViewResolverConfiguration()
                .addPrefixes("customTemplateLocation/")
                .setSuffix(".template");
        resolver.getTemplateEngine().setViewResolverConfiguration(configuration);
        CustomItem item = new CustomItem();
        item.setMessageText("Test");
        TemplateEngineModel templateEngineModel = new TemplateEngineModel();
        templateEngineModel.setInlineContentItem(item);
        Assert.assertTrue(resolver.supports(templateEngineModel));
        Assert.assertEquals("<custom>Test</custom>", resolver.resolve(templateEngineModel));
    }

    @Test
    public void testReplacingPrefixes() throws RenderingEngineMissingException {
        ThymeleafInlineContentItemsResolver resolver = new ThymeleafInlineContentItemsResolver();
        ViewResolverConfiguration configuration = new ViewResolverConfiguration();
        List<String> prefixes = configuration.getPrefixes();
        List<String> replacements = Collections.singletonList("nonExistentLocation/");
        configuration.setPrefixes(replacements);
        resolver.getTemplateEngine().setViewResolverConfiguration(configuration);
        ArticleItem articleItem = new ArticleItem();
        articleItem.setTitle("Test");
        TemplateEngineModel templateEngineModel = new TemplateEngineModel();
        templateEngineModel.setInlineContentItem(articleItem);
        Assert.assertFalse(resolver.supports(templateEngineModel));
        configuration.setPrefixes(prefixes);
        resolver.getTemplateEngine().setViewResolverConfiguration(configuration);
        Assert.assertTrue(resolver.supports(templateEngineModel));
        Assert.assertEquals("<p>Test</p>", resolver.resolve(templateEngineModel));
    }

    @Test
    public void testIllegalStateException() throws RenderingEngineMissingException {
        ThymeleafInlineContentItemsResolver resolver = new ThymeleafInlineContentItemsResolver();
        ArticleItem articleItem = new ArticleItem();
        articleItem.setTitle("Test");
        TemplateEngineModel templateEngineModel = new TemplateEngineModel();
        templateEngineModel.setInlineContentItem(articleItem);
        Assert.assertFalse(resolver.supports(templateEngineModel));
        try {
            resolver.resolve(templateEngineModel);
            Assert.fail("Expected IllegalStateException");
        } catch (IllegalStateException e) {
            Assert.assertEquals("Engine not configured.  Did you call setViewResolverConfiguration()?", e.getMessage());
        }
    }

    @ContentItemMapping("non_existing")
    class ClassWithoutTemplate {}

    class ClassWithoutAnyMapping {}

    public class Coffee {
        System system;

        public System getSystem() {
            return system;
        }

        public void setSystem(System system) {
            this.system = system;
        }
    }
}
