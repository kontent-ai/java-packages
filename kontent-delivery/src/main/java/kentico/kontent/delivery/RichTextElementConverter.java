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

package kentico.kontent.delivery;

import kentico.kontent.delivery.template.TemplateEngineConfig;
import kentico.kontent.delivery.template.TemplateEngineInlineContentItemsResolver;
import kentico.kontent.delivery.template.TemplateEngineModel;

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

    private Pattern linkedItemPattern = Pattern.compile("<object type=\"application/kenticocloud\" " +
            "(?<attrs>[^>]+)></object>");

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
                richTextElement.setValue(resolveLinkedItems(richTextElement));
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
        orig.setValue(resolveLinkedItems(orig));
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

    private String resolveLinkedItems(RichTextElement element) {
        return resolveLinkedItemsRecursively(element, element.getValue(), new ArrayList<>());
    }

    private String resolveLinkedItemsRecursively(
            RichTextElement element, String content, Collection<String> itemsAlreadyResolved) {
        if (element.getLinkedItems() == null || element.getParent() == null) {
            return element.getValue();
        }
        Matcher matcher = linkedItemPattern.matcher(content);
        StringBuffer buffer = new StringBuffer();

        while (matcher.find()) {
            String attrs = matcher.group("attrs");
            InlineModularContentDataAttributes dataAttributes = InlineModularContentDataAttributes.fromAttrs(attrs);
            String codename = dataAttributes.getCodename();
            ContentItem linkedItem = element.getParent().getLinkedItem(codename);
            InternalInlineContentItemResolver resolver = null;
            // Check to see if we encountered a cycle in our tree, if so, halt resolution
            if (!itemsAlreadyResolved.contains(codename)) {
                resolver = resolveMatch(element, linkedItem);
            }
            if (resolver == null) {
                // The linked item isn't fetched, doesn't exist, we encountered a cycle, or there is no resolver,
                // preserve the <object> tag
                matcher.appendReplacement(buffer, matcher.group(0));
            } else {
                String resolvedString = resolver.resolve();

                // The resolved String may have an inline linked item object element, so recursively call, but add
                // the resolved linked item to the blacklist below to avoid an infinite cyclic loop
                ArrayList<String> resolvedItems = new ArrayList<>(itemsAlreadyResolved);
                resolvedItems.add(codename);
                resolvedString = resolveLinkedItemsRecursively(element, resolvedString, resolvedItems);

                // Make resolved replacement string a literal string to make sure dollar signs are not interpreted as
                // capturing groups and add resolved string to the buffer
                matcher.appendReplacement(buffer, Matcher.quoteReplacement(resolvedString));
            }
        }
        matcher.appendTail(buffer);
        return buffer.toString();
    }

    private InternalInlineContentItemResolver resolveMatch(RichTextElement element, ContentItem linkedItem) {
        if (linkedItem != null) {
            for (Element linkedItemElement : linkedItem.getElements().values()) {
                if (linkedItemElement instanceof RichTextElement) {
                    RichTextElement embeddedRichTextElement = (RichTextElement) linkedItemElement;
                    embeddedRichTextElement.setValue(resolveLinks(embeddedRichTextElement));
                }
            }
            InlineContentItemsResolver resolverForType =
                    stronglyTypedContentItemConverter.getResolverForType(linkedItem);
            InlineContentItemsResolver<ContentItem> defaultResolver = stronglyTypedContentItemConverter.getDefaultResolver();
            if (resolverForType != null) {
                Object convertedLinkedItem = linkedItem.castTo((Class) resolverForType.getType());
                return () -> resolverForType.resolve(convertedLinkedItem);
            } else if (templateEngineConfig != null) {
                TemplateEngineInlineContentItemsResolver supportedResolver;
                Object linkedItemModel = linkedItem.castToDefault();
                TemplateEngineModel model = new TemplateEngineModel();
                model.setInlineContentItem(linkedItemModel);
                // TODO: Add support for adding Locale from query if it exists
                model.addVariable("parent", element);
                model.addVariables(templateEngineConfig.getDefaultModelVariables());
                supportedResolver = getTemplateResolver(model);
                if (supportedResolver != null) {
                    return () -> supportedResolver.resolve(model);
                }
            }

            if (defaultResolver != null) {
                return () -> defaultResolver.resolve(linkedItem);
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

    @lombok.Getter
    @lombok.Setter
    @lombok.Builder
    private static class InlineModularContentDataAttributes {
        private static Pattern DATA_ATTRIBUTE_PATTERN = Pattern.compile("(data-\\w+)=\"(\\w+)\"");

        private String type;
        private String rel;
        private String codename;

        private static InlineModularContentDataAttributes fromAttrs(String attrs) {
            Matcher matcher = DATA_ATTRIBUTE_PATTERN.matcher(attrs);
            InlineModularContentDataAttributesBuilder builder = InlineModularContentDataAttributes.builder();
            while (matcher.find()) {
                String key = matcher.group(1);
                String value = matcher.group(2);
                switch (key) {
                    case "data-type":
                        builder.type(value);
                        break;
                    case "data-rel":
                        builder.rel(value);
                        break;
                    case "data-codename":
                        builder.codename(value);
                        break;
                    default:
                        break;
                }
            }
            return builder.build();
        }
    }
}
