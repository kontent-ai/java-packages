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

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Concrete implementations of this can resolve strongly typed content types included inline rich text.
 *
 * @param <T>   The strongly typed content type model this resolver supports.
 */
public abstract class InlineContentItemsResolver<T> {

    //Neil Gafter Super Type Token, http://gafter.blogspot.com/2006/12/super-type-tokens.html
    private final Type type;

    public InlineContentItemsResolver() {
        Type superclass = getClass().getGenericSuperclass();
        if (superclass instanceof Class) {
            throw new IllegalArgumentException("Missing type parameter.");
        }
        this.type = ((ParameterizedType) superclass).getActualTypeArguments()[0];
    }

    /**
     * Returns the raw text of what to insert into the rich text with the given inline strongly typed content type of
     * type T.
     *
     * @param data  An instance of a strongly typed content item.
     * @return      Raw text to insert into the rich text.
     */
    public abstract String resolve(T data);

    protected Type getType() {
        return type;
    }
}
