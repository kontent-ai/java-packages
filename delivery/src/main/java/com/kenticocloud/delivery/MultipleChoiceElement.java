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
@lombok.Getter
@lombok.Setter
@lombok.ToString(callSuper = true)
@lombok.EqualsAndHashCode(callSuper = true)
public class MultipleChoiceElement extends Element<List<Option>> {

    static final String TYPE_VALUE = "multiple_choice";

    /**
     * The value of the selected elements.
     * <p>
     * The value of a Multiple choice element is a list of option objects. Each option object has a name and codename.
     * <p>
     * Note, this is not returned when querying for the element by type.
     *
     * @param value Sets the value of this
     * @return      A list of selected elements.
     * @see         Option
     */
    @JsonProperty("value")
    List<Option> value;

    /**
     * The available option elements on this content type element.
     * <p>
     * The value of a Multiple choice element is a list of option objects. Each option object has a name and codename.
     * <p>
     * Note, this is only returned when querying for the element by type.
     *
     * @param options   Sets the options of this.
     * @return          A list of option elements.
     * @see             Option
     */
    @JsonProperty("options")
    List<Option> options;

    /**
     * Gets the name from the value property
     *
     * @param Since the value is a list, you must send the option index
     * @see             Option
     */
    public String nameFromOptionValue(int index) {
        return value.get(index).name;
    }

    /**
     * Gets the codename from the value property
     *
     * @param Since the value is a list, you must send the option index
     * @see             Option
     */
    public String codenameFromOptionValue(int index) {
        return value.get(index).codename;
    }

    /**
     * Gets the name from the options property
     *
     * @param Since the options is a list, you must send the option index
     * @see             Option
     */
    public String nameFromOptions(int index) {
        return value.get(index).name;
    }

    /**
     * Gets the codename from the options property
     *
     * @param Since the options is a list, you must send the option index
     * @see             Option
     */
    public String codenameFromOptions(int index) {
        return value.get(index).codename;
    }

    public MultipleChoiceElement() {
        setType(TYPE_VALUE);
    }
}
