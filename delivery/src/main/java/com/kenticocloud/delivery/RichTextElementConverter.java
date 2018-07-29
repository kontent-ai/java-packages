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

import com.kenticocloud.delivery.template.TemplateEngineConfig;
import com.kenticocloud.delivery.template.TemplateEngineInlineContentItemsResolver;
import com.kenticocloud.delivery.template.TemplateEngineModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RichTextElementConverter {

    ContentLinkUrlResolver contentLinkUrlResolver;
    BrokenLinkUrlResolver brokenLinkUrlResolver;
    RichTextElementResolver richTextElementResolver;
    TemplateEngineConfig templateEngineConfig;
    StronglyTypedContentItemConverter stronglyTypedContentItemConverter;

    /*
    Regex prior to the Java \ escapes:
    <a[^>]+?data-item-id=\"(?<id>[^\"]+)\"[^>]*>

    Regex explanation:
    <a matches the characters <a literally (case sensitive)
        Match a single character not present in the list below [^>]+?
            +? Quantifier — Matches between one and unlimited times, as few times as possible, expanding as needed (lazy)
            > matches the character > literally (case sensitive)
    data-item-id= matches the characters data-item-id= literally (case sensitive)
    \" matches the character " literally (case sensitive)
        Named Capture Group id (?<id>[^\"]+)
            Match a single character not present in the list below [^\"]+
                + Quantifier — Matches between one and unlimited times, as many times as possible, giving back as needed (greedy)
                \" matches the character " literally (case sensitive)
    \" matches the character " literally (case sensitive)
        Match a single character not present in the list below [^>]*
            * Quantifier — Matches between zero and unlimited times, as many times as possible, giving back as needed (greedy)
            > matches the character > literally (case sensitive)
    > matches the character > literally (case sensitive)
        Global pattern flags
            g modifier: global. All matches (don't return after first match)
    */
    private Pattern linkPattern = Pattern.compile("<a[^>]+?data-item-id=\"(?<id>[^\"]+)\"[^>]*>");

    /*
    Regex prior to the Java \ escapes:
    <object type=\"application\/kenticocloud" data-type=\"(?<type>[^\"]+\") data-codename=\"(?<codename>[^\"]+\")><\/object>

    Regex explanation:
    <object type= matches the characters <object type= literally (case sensitive)
    \" matches the character " literally (case sensitive)
    application matches the characters application literally (case sensitive)
    \/ matches the character / literally (case sensitive)
    kenticocloud" data-type= matches the characters kenticocloud" data-type= literally (case sensitive)
    \" matches the character " literally (case sensitive)
        Named Capture Group type (?<type>[^\"]+\")
            Match a single character not present in the list below [^\"]+
                + Quantifier — Matches between one and unlimited times, as many times as possible, giving back as needed (greedy)
                \" matches the character " literally (case sensitive)
    \" matches the character " literally (case sensitive)
     data-codename= matches the characters  data-codename= literally (case sensitive)
    \" matches the character " literally (case sensitive)
        Named Capture Group codename (?<codename>[^\"]+\")
            Match a single character not present in the list below [^\"]+
                + Quantifier — Matches between one and unlimited times, as many times as possible, giving back as needed (greedy)
                \" matches the character " literally (case sensitive)
    \" matches the character " literally (case sensitive)
    >< matches the characters >< literally (case sensitive)\/ matches the character / literally (case sensitive)
    object> matches the characters object> literally (case sensitive)
        Global pattern flags
            g modifier: global. All matches (don't return after first match)
     */
    private Pattern modularContentPattern = Pattern.compile(
            "<object type=\"application/kenticocloud\" data-type=\"" +
                    "(?<type>[^\"]+)\" data-codename=\"" +
                    "(?<codename>[^\"]+)\"></object>");

    public RichTextElementConverter(
            ContentLinkUrlResolver contentLinkUrlResolver,
            BrokenLinkUrlResolver brokenLinkUrlResolver,
            RichTextElementResolver richTextElementResolver,
            TemplateEngineConfig templateEngineConfig,
            StronglyTypedContentItemConverter stronglyTypedContentItemConverter) {
        this.contentLinkUrlResolver = contentLinkUrlResolver;
        this.brokenLinkUrlResolver = brokenLinkUrlResolver;
        this.richTextElementResolver = richTextElementResolver;
        this.templateEngineConfig = templateEngineConfig;
        this.stronglyTypedContentItemConverter = stronglyTypedContentItemConverter;
    }

    public void process(List<ContentItem> orig) {
        for (ContentItem contentItem : orig) {
            process(contentItem);
        }
    }

    public void process(ContentItem orig) {
        for (Map.Entry<String, Element> entry : orig.getElements().entrySet()) {
            Element element = entry.getValue();
            if (element instanceof RichTextElement) {
                RichTextElement richTextElement = (RichTextElement) element;
                richTextElement.setValue(resolveLinks(richTextElement));
                richTextElement.setValue(resolveModularContent(richTextElement));
                if (richTextElementResolver != null) {
                    richTextElement.setValue(richTextElementResolver.resolve(richTextElement.getValue()));
                }
            }
        }
    }

    public RichTextElement convert(RichTextElement orig) {
        if (orig.getValue() == null) {
            return orig;
        }
        orig.setValue(resolveLinks(orig));
        orig.setValue(resolveModularContent(orig));
        if (richTextElementResolver != null) {
            orig.setValue(richTextElementResolver.resolve(orig.getValue()));
        }
        return orig;
    }

    private String resolveLinks(RichTextElement element) {
        if (element.links == null) {
            return element.getValue();
        }
        Matcher matcher = linkPattern.matcher(element.getValue());
        StringBuffer buffer = new StringBuffer();
        while (matcher.find()) {
            Link link = element.links.get(matcher.group("id"));
            String url = "";
            if (link != null  && contentLinkUrlResolver != null) {
                url = contentLinkUrlResolver.resolveLinkUrl(link);
            } else if (brokenLinkUrlResolver != null){
                url = brokenLinkUrlResolver.resolveBrokenLinkUrl();
            }
            matcher.appendReplacement(buffer, resolveMatch(matcher.group(0), url));
        }
        matcher.appendTail(buffer);
        return buffer.toString();
    }

    private String resolveModularContent(RichTextElement element) {
        return resolveModularContentRecursively(element, element.getValue(), new ArrayList<>());
    }

    private String resolveModularContentRecursively(
            RichTextElement element, String content, Collection<String> itemsAlreadyResolved) {
        if (element.getModularContent() == null || element.getParent() == null) {
            return element.getValue();
        }
        Matcher matcher = modularContentPattern.matcher(content);
        StringBuffer buffer = new StringBuffer();

        while (matcher.find()) {
            String codename = matcher.group("codename");
            ContentItem modularContent = element.getParent().getModularContent(codename);
            InternalInlineContentItemResolver resolver = null;
            //Check to see if we encountered a cycle in our tree, if so, halt resolution
            if (!itemsAlreadyResolved.contains(codename)) {
                resolver = resolveMatch(element, modularContent);
            }
            if (resolver == null) {
                // The modular content isn't fetched, doesn't exist, we encountered a cycle, or there is no resolver,
                // preserve the <object> tag
                matcher.appendReplacement(buffer, matcher.group(0));
            } else {
                String resolvedString = resolver.resolve();

                //The resolved String may have an inline modular content object element, so recursively call, but add
                //the resolved modular content to the blacklist below to avoid an infinite cyclic loop
                ArrayList<String> resolvedItems = new ArrayList<>(itemsAlreadyResolved);
                resolvedItems.add(codename);
                resolvedString = resolveModularContentRecursively(element, resolvedString, resolvedItems);

                //Make resolved replacement string a literal string to make sure dollar signs are not interpreted as capturing groups
                // and add resolved string to the buffer
                matcher.appendReplacement(buffer, Matcher.quoteReplacement(resolvedString));
            }
        }
        matcher.appendTail(buffer);
        return buffer.toString();
    }

    private InternalInlineContentItemResolver resolveMatch(RichTextElement element, ContentItem modularContent) {
        if (modularContent != null) {
            for (Element modularContentElement: modularContent.getElements().values()) {
                if (modularContentElement instanceof RichTextElement) {
                    RichTextElement embeddedRichTextElement = (RichTextElement) modularContentElement;
                    embeddedRichTextElement.setValue(resolveLinks(embeddedRichTextElement));
                }
            }
            InlineContentItemsResolver resolverForType =
                    stronglyTypedContentItemConverter.getResolverForType(modularContent);
            if (resolverForType != null) {
                Object convertedModularContent = modularContent.castTo((Class) resolverForType.getType());
                return () -> resolverForType.resolve(convertedModularContent);
            } else if (templateEngineConfig != null) {
                TemplateEngineInlineContentItemsResolver supportedResolver;
                Object modularContentModel = modularContent.castToDefault();
                TemplateEngineModel model = new TemplateEngineModel();
                model.setInlineContentItem(modularContentModel);
                //TODO: Add support for adding Locale from query if it exists
                model.addVariable("parent", element);
                model.addVariables(templateEngineConfig.getDefaultModelVariables());
                supportedResolver = getTemplateResolver(model);
                if (supportedResolver != null) {
                    return () -> supportedResolver.resolve(model);
                }
            }
        }
        return null;
    }

    private String resolveMatch(String match, String url) {
        return match.replace("href=\"\"", String.format("href=\"%s\"", StringUtils.escapeHtml(url)));
    }

    private TemplateEngineInlineContentItemsResolver getTemplateResolver(TemplateEngineModel model) {
        if (templateEngineConfig == null) {
            return null;
        }
        List<TemplateEngineInlineContentItemsResolver> templateResolvers = templateEngineConfig.getResolvers();
        if (templateResolvers != null) {
            for (TemplateEngineInlineContentItemsResolver templateResolver : templateResolvers) {
                if (templateResolver.supports(model)) {
                    return templateResolver;
                }
            }
        }
        return null;
    }

    private interface InternalInlineContentItemResolver {
        String resolve();
    }
}
