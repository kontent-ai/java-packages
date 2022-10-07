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

/**
 * Object model for Asset elements
 */
@lombok.Data
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@lombok.Builder
public class Asset {

    @JsonProperty("name")
    String name;

    @JsonProperty("type")
    String type;

    @JsonProperty("size")
    Integer size;

    @JsonProperty("description")
    String description;

    @JsonProperty("url")
    String url;

    @JsonProperty("width")
    String width;

    @JsonProperty("height")
    String height;


    /**
     * File name of the asset
     *
     * @return file name
     */
    public String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    /**
     * MIME type of the asset
     *
     * @return MIME type
     */
    public String getType() {
        return type;
    }

    void setType(String type) {
        this.type = type;
    }

    /**
     * Size of the asset in bytes
     *
     * @return number of bytes
     */
    public Integer getSize() {
        return size;
    }

    void setSize(Integer size) {
        this.size = size;
    }

    /**
     * Description of the asset
     *
     * @return asset description
     */
    public String getDescription() {
        return description;
    }

    void setDescription(String description) {
        this.description = description;
    }

    /**
     * Absolute URL for the asset
     *
     * @return URL for the asset, hosted by Kontent.ai
     */
    public String getUrl() {
        return url;
    }

    void setUrl(String url) {
        this.url = url;
    }

    /**
     * Width of the asset
     *
     * @return Width of the asset
     */

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    /**
     * Height of the asset
     *
     * @return Height of the asset
     */

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }
}