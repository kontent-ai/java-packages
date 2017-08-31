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
 * Object model for Multiple choice elements
 */
public class MultipleChoiceElement extends Element {

    static final String TYPE_VALUE = "multiple_choice";

    @JsonProperty("value")
    List<Option> value;

    @JsonProperty("options")
    List<Option> options;

    MultipleChoiceElement() {
        setType(TYPE_VALUE);
    }

    /**
     * The value of the selected elements.
     * <p>
     * The value of a Multiple choice element is a list of option objects. Each option object has a name and codename.
     * <p>
     * Note, this is not returned when querying for the element by type.
     * @return a list of selected elements
     * @see Option
     */
    public List<Option> getValue() {
        return value;
    }

    void setValue(List<Option> value) {
        this.value = value;
    }

    /**
     * The available option elements on this content type element.
     * <p>
     * The value of a Multiple choice element is a list of option objects. Each option object has a name and codename.
     * <p>
     * Note, this is only returned when querying for the element by type.
     * @return a list of option elements
     * @see Option
     */
    public List<Option> getOptions() {
        return options;
    }

    void setOptions(List<Option> options) {
        this.options = options;
    }
}
