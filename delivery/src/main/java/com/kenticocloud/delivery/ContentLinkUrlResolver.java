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

/**
 * Interface to provide for rendering content item links in rich text.
 * <p>
 * An implementation of this can be provided to via
 * {@link DeliveryClient#setContentLinkUrlResolver(ContentLinkUrlResolver)} to resolve links to a content item that are
 * published.  This is passed on to the {@link RichTextElementConverter} to update {@code href} attributes in links when
 * a {@code data-item-id} is available on the link.
 * <p>
 * This is a {@link FunctionalInterface} to simplify implementation.
 * <p>
 * For example, the following implementation will render {@code <a href="/LINK_TYPE/LINK_URL_SLUG">Some text</a>}:
 * <pre>{@code
 * DeliveryClient deliveryClient = new DeliveryClient("02a70003-e864-464e-b62c-e0ede97deb8c");
 * deliveryClient.setContentLinkUrlResolver(link -> String.format("/%s/%s", link.getType(), link.getUrlSlug()));
 * }</pre>
 *
 * @see <a href="https://developer.kenticocloud.com/v2/reference#section-link-to-a-content-item">
 *      KenticoCloud API reference - Link to a content item</a>
 * @see <a href="https://developer.kenticocloud.com/v2/reference#section-rich-text-links">
 *      KenticoCloud API reference - Rich text links</a>
 * @see Link
 * @see RichTextElementConverter
 * @see RichTextElement
 */
@FunctionalInterface
public interface ContentLinkUrlResolver {

    /**
     * Returns a String to be placed in the href attribute of any links to content items that are published within
     * {@link RichTextElement}s.
     *
     * @param link  The link that needs to be resolved to a url.
     * @return      The String to place in the href attribute of any links to content items that are published.
     */
    String resolveLinkUrl(Link link);
}
