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

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Object model for Asset elements.
 *
 * @see AssetsElement
 * @see <a href="https://developer.kenticocloud.com/v2/reference#section-asset">KenticoCloud API reference - Asset</a>
 */
@lombok.Data
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@lombok.Builder
public class Asset {

    /**
     * File name of the asset.
     *
     * @param name  New value for the name of this asset.
     * @return      The name of the asset.
     */
    @JsonProperty("name")
    String name;

    /**
     * MIME type of the asset.
     *
     * @param type  New value for the MIME type of this asset.
     * @return      The MIME type of this asset.
     */
    @JsonProperty("type")
    String type;

    /**
     * Size of the asset in bytes.
     *
     * @param size  New value for the size of this asset in bytes.
     * @return      The size of this asset in bytes.
     */
    @JsonProperty("size")
    Integer size;

    /**
     * Description of the asset.
     *
     * @param description   New value for the description of this asset.
     * @return              The description of this asset.
     */
    @JsonProperty("description")
    String description;

    /**
     * Absolute URL for the asset.
     *
     * @param url   New Value for the absolute URL of this asset.
     * @return      The absolute URL of this asset.
     */
    @JsonProperty("url")
    String url;
}
