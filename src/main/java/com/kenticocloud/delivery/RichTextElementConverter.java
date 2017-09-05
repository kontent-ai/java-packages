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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.ResolvableDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RichTextElementConverter extends StdDeserializer<RichTextElement> implements ResolvableDeserializer {

    transient ContentLinkUrlResolver contentLinkUrlResolver;
    transient BrokenLinkUrlResolver brokenLinkUrlResolver;
    transient StronglyTypedContentItemConverter stronglyTypedContentItemConverter;
    private final transient JsonDeserializer<?> defaultDeserializer;

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
    private Pattern linkPattern = Pattern.compile("<a[^>]+?data-item-id=\\\"(?<id>[^\\\"]+)\\\"[^>]*>");

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
            "<object type=\\\"application\\/kenticocloud\" data-type=\\\"" +
                    "(?<type>[^\\\"]+)\\\" data-codename=\\\"" +
                    "(?<codename>[^\\\"]+)\\\"><\\/object>");

    public RichTextElementConverter(
            ContentLinkUrlResolver contentLinkUrlResolver,
            BrokenLinkUrlResolver brokenLinkUrlResolver,
            StronglyTypedContentItemConverter stronglyTypedContentItemConverter,
            JsonDeserializer<?> defaultDeserializer) {
        super(RichTextElement.class);
        this.defaultDeserializer = defaultDeserializer;
        this.contentLinkUrlResolver = contentLinkUrlResolver;
        this.brokenLinkUrlResolver = brokenLinkUrlResolver;
        this.stronglyTypedContentItemConverter = stronglyTypedContentItemConverter;
    }

    @Override
    public RichTextElement deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        RichTextElement orig = (RichTextElement) defaultDeserializer.deserialize(p, ctxt);
        return convert(orig);
    }

    @Override
    public void resolve(DeserializationContext ctxt) throws JsonMappingException {
        ((ResolvableDeserializer) defaultDeserializer).resolve(ctxt);
    }

    public RichTextElement convert(RichTextElement orig) {
        if (orig.getValue() == null) {
            return orig;
        }
        orig.setValue(resolveLinks(orig));
        orig.setValue(resolveModularContent(orig));
        return orig;
    }

    private String resolveLinks(RichTextElement element) {
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
        if (element.getModularContent() == null || element.getParent() == null) {
            return element.getValue();
        }
        Matcher matcher = modularContentPattern.matcher(element.getValue());
        StringBuffer buffer = new StringBuffer();
        while (matcher.find()) {
            ContentItem modularContent = element.getParent().getModularContent(matcher.group("codename"));
            if (modularContent != null) {
                InlineContentItemsResolver resolverForType =
                        stronglyTypedContentItemConverter.getResolverForType(modularContent);
                if (resolverForType == null) {
                    // The modular content doesn't have a resolver, preserve the <object> tag
                    matcher.appendReplacement(buffer, matcher.group(0));
                } else {
                    Object convertedModularContent = modularContent.castTo((Class) resolverForType.getType());
                    matcher.appendReplacement(buffer, resolverForType.resolve(convertedModularContent));
                }
            } else {
                // The modular content isn't fetched or doesn't exist, preserve the <object> tag
                matcher.appendReplacement(buffer, matcher.group(0));
            }
        }
        matcher.appendTail(buffer);
        return buffer.toString();
    }

    private String resolveMatch(String match, String url) {
        return match.replace("href=\"\"", String.format("href=\"%s\"", StringUtils.escapeHtml(url)));
    }
}
