/*
 * MIT License
 *
 * Copyright (c) 2017
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

package com.kenticocloud.delivery;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Object model for a Taxonomy group element
 */
public class TaxonomyElement extends Element {

    static final String TYPE_VALUE = "taxonomy";

    @JsonProperty("taxonomy_group")
    String taxonomyGroup;

    @JsonProperty("value")
    List<Taxonomy> value;

    TaxonomyElement() {
        setType(TYPE_VALUE);
    }

    /**
     * The name of the taxonomy group
     * @return taxonomy group name
     */
    public String getTaxonomyGroup() {
        return taxonomyGroup;
    }

    void setTaxonomyGroup(String taxonomyGroup) {
        this.taxonomyGroup = taxonomyGroup;
    }

    /**
     * The value of Taxonomy elements is a list of {@link Taxonomy} item objects.
     * @return list of {@link Taxonomy} objects
     */
    @Override
    public List<Taxonomy> getValue() {
        return value;
    }

    void setValue(List<Taxonomy> value) {
        this.value = value;
    }
}
