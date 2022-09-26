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

/**
 * RichTextElementResolvers added to the {@link DeliveryClient} will be invoked when resolving any
 * {@link RichTextElement}.
 */
public interface RichTextElementResolver {

    /**
     * The current state of the rich text element's value is provided.  This may have been changed from the original by
     * the {@link ContentLinkUrlResolver}, {@link BrokenLinkUrlResolver}, {@link InlineContentItemsResolver}s, and/or
     * previously ordered {@link RichTextElementResolver}s.
     * <p>
     * It is expected to return the state of the rich text element, even if no manipulation is performed.
     *
     * @param content   The current state of the rich text element's value.
     * @return          The new state of the rich text element's value.
     */
    String resolve(String content);
}
