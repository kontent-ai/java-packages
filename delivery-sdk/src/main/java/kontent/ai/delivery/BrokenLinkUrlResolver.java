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

/**
 * Interface to provide for rendering broken content item links in rich text.
 * <p>
 * An implementation of this can be provided to via
 * {@link DeliveryClient#setBrokenLinkUrlResolver(BrokenLinkUrlResolver)} to resolve links to a content item that are
 * not published.  This is passed on to the {@link RichTextElementConverter} to update {@code href} attributes in links
 * when no {@code data-item-id} is available on the link.
 * <p>
 * This is a {@link FunctionalInterface} to simplify implementation.
 * <p>
 * For example, the following implementation will render {@code <a href="/404">Some text</a>}:
 * <pre>{@code
 * DeliveryClient deliveryClient = new DeliveryClient("02a70003-e864-464e-b62c-e0ede97deb8c");
 * deliveryClient.setBrokenLinkUrlResolver(() -> "/404");
 * }</pre>
 *
 * @see RichTextElementConverter
 * @see RichTextElement
 * @see <a href="https://docs.kontent.ai/reference/delivery-api#section/Linked-items-element">
 *      Kontent API reference - Link to a content item</a>
 * @see <a href="https://docs.kontent.ai/reference/delivery-api#section/Rich-text-element/links-single-object">
 *      Kontent API reference - Rich text links</a>
 */
@FunctionalInterface
public interface BrokenLinkUrlResolver {

    /**
     * Returns a String to be placed in the href attribute of any links to content items that are not published within
     * {@link RichTextElement}s.
     *
     * @return The String to place in the href attribute of any links to content items that are not published.
     */
    String resolveBrokenLinkUrl();
}
