/*
 * MIT License
 *
 * Copyright (c) 2022 Kontent s.r.o.
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

import java.io.Serializable;

/**
 * Object model for Image elements
 * <p>
 * Images associated with rich text elements
 *
 * @see RichTextElement
 */
@lombok.Getter
@lombok.Setter
@lombok.ToString
@lombok.EqualsAndHashCode
@lombok.NoArgsConstructor
public class Image {

    /**
     * ID of the image
     *
     * @param imageId   Sets the imageId of this
     * @return          This imageId
     */
    @JsonProperty("image_id")
    String imageId;

    /**
     * Description of the image
     * <p>
     * Used for the alt attribute of an &lt;img&gt; tag.
     *
     * @param description   Sets the description of this
     * @return              The image description of this
     */
    @JsonProperty("description")
    String description;

    /**
     * Absolute URL for the image
     *
     * @param url   Sets the url of this
     * @return      An absolute URL image hosted by Kontent.ai
     */
    @JsonProperty("url")
    String url;

}
