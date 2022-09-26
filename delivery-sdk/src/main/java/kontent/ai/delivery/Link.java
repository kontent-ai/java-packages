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

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Object model for Link elements
 * <p>
 * Links associated with rich text elements
 *
 * @see RichTextElement
 */
@lombok.Getter
@lombok.Setter
@lombok.ToString
@lombok.EqualsAndHashCode
@lombok.NoArgsConstructor
public class Link {

    /**
     * Content type of the content item
     *
     * @param type  Sets the type of this.
     * @return      The content item type codename.
     */
    @JsonProperty("type")
    String type;

    /**
     * Display name of the element
     *
     * @param codename  Sets the codename of this.
     * @return          The codename of the link element.
     */
    @JsonProperty("codename")
    String codename;

    /**
     * URL slug of the content item
     * <p>
     * Empty string if the content item's type does not use a URL slug element
     *
     * @param urlSlug   Sets the urlSlug of this.
     * @return          URL slug of the content item.
     */
    @JsonProperty("url_slug")
    String urlSlug;

}
