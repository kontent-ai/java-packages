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
 * Object model for asset rendition
 */
@lombok.Data
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@lombok.Builder
public class AssetRendition {

    @JsonProperty("rendition_id")
    String renditionId;

    @JsonProperty("preset_id")
    String presetId;

    @JsonProperty("width")
    String width;

    @JsonProperty("height")
    String height;

    @JsonProperty("query")
    String query;

    /**
     * Asset rendition ID
     *
     * @return rendition ID
     */
    public String getRenditionId() {
        return renditionId;
    }

    void setRenditionId(String renditionId) {
        this.renditionId = renditionId;
    }

    /**
     * Asset rendition preset ID
     *
     * @return rendition preset ID
     */
    public String getPresetId() {
        return presetId;
    }

    void setPresetId(String presetId) {
        this.presetId = presetId;
    }

    /**
     * Width of the asset rendition
     *
     * @return Width of the asset rendition
     */

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    /**
     * Height of the asset rendition
     *
     * @return Height of the asset rendition
     */

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    /**
     * Asset rendition query
     *
     * @return rendition query
     */
    public String getQuery() {
        return query;
    }

    void setQuery(String query) {
        this.query = query;
    }
}