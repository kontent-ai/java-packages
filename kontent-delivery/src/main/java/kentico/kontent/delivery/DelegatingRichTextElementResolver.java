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

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of {@link RichTextElementResolver} that iterates over a list of resolvers sequentially.
 *
 * @see DeliveryClient#addRichTextElementResolver(RichTextElementResolver)
 * @see DeliveryClient#setRichTextElementResolver(RichTextElementResolver)
 * @see RichTextElementConverter
 */
public class DelegatingRichTextElementResolver implements RichTextElementResolver {

    List<RichTextElementResolver> resolvers = new ArrayList<>();

    /**
     * {@inheritDoc}
     */
    @Override
    public String resolve(String content) {
        if (resolvers.isEmpty()) {
            return content;
        }
        String resolvedContent = content;
        for (RichTextElementResolver resolver : resolvers) {
            resolvedContent = resolver.resolve(resolvedContent);
        }
        return resolvedContent;
    }

    /**
     * Adds a {@link RichTextElementResolver} to the list of resolvers this resolver will delegate to.
     *
     * @param resolver The {@link RichTextElementResolver} to add to the delegate list.
     */
    public void addResolver(RichTextElementResolver resolver) {
        resolvers.add(resolver);
    }
}
