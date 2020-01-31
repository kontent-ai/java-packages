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

import java.util.List;

/**
 * Object model for Assets elements.
 *
 * @see Asset
 * @see <a href="https://docs.kontent.ai/reference/delivery-api#section/Asset-element">KenticoKontent API reference - Asset</a>
 * @see <a href="https://docs.kontent.ai/reference/delivery-api#section/Content-item-object">
 *      KenticoKontent API reference - Content item object</a>
 */
@lombok.Getter
@lombok.Setter
@lombok.ToString(callSuper = true)
@lombok.EqualsAndHashCode(callSuper = true)
public class AssetsElement extends Element<List<Asset>> {

    static final String TYPE_VALUE = "asset";

    /**
     * When used in content items, Asset elements can contain multiple assets. The value in the JSON response for an
     * Asset element consists of a list of {@link Asset} objects.
     *
     * @param value New value for this element's List of Assets.
     * @return      This element's list of assets.
     */
    @JsonProperty("value")
    List<Asset> value;

    public AssetsElement() {
        setType(TYPE_VALUE);
    }
}
