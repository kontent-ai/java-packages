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
 * Object model for a Taxonomy element
 */
public class Taxonomy {

    @JsonProperty("name")
    String name;

    @JsonProperty("codename")
    String codename;

    @JsonProperty("terms")
    List<Taxonomy> terms;

    public Taxonomy() {
        //Default constructor
    }

    /**
     * The display name of the taxonomy
     * @return the name of the taxonomy
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * The codename of the taxonomy
     * @return the codename of the taxonomy
     */
    public String getCodename() {
        return codename;
    }

    public void setCodename(String codename) {
        this.codename = codename;
    }

    /**
     * A list of taxonomically descendant terms
     * @return a list of taxonomically descendant terms
     */
    public List<Taxonomy> getTerms() {
        return terms;
    }

    public void setTerms(List<Taxonomy> terms) {
        this.terms = terms;
    }
}
