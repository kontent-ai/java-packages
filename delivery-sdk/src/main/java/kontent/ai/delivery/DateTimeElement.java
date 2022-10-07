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

import java.time.ZonedDateTime;

/**
 * Object model for Date &amp; time elements.
 *
 * @see Asset
 * @see <a href="https://docs.kontent.ai/reference/delivery-api#section/Date-and-time-element">
 *      Kontent API reference - Date time</a>
 * @see <a href="https://docs.kontent.ai/reference/delivery-api#section/Content-item-object">
 *      Kontent API reference - Content item object</a>
 */
@lombok.Getter
@lombok.Setter
@lombok.ToString(callSuper = true)
@lombok.EqualsAndHashCode(callSuper = true)
public class DateTimeElement extends Element<ZonedDateTime> {

    static final String TYPE_VALUE = "date_time";

    /**
     * The value of a Date &amp; time element is a string in the ISO 8601 format. If empty, the value is null.
     *
     * @param value New value of the {@link ZonedDateTime} of this element.
     * @return      A {@link ZonedDateTime} instance representing the original ISO 8601 string.
     */
    @JsonProperty("value")
    ZonedDateTime value;

    public DateTimeElement() {
        setType(TYPE_VALUE);
    }
}
