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

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

public class DeliveryParameterBuilder {

    static final String LANGUAGE = "language";
    static final String ELEMENTS = "elements";
    static final String ORDER = "order";
    static final String DEPTH = "depth";
    static final String SKIP = "skip";
    static final String LIMIT = "limit";

    static final String LESS_THAN = "[lt]";
    static final String LESS_THAN_OR_EQUALS = "[lte]";
    static final String GREATER_THAN = "[gt]";
    static final String GREATER_THAN_OR_EQUALS = "[gte]";
    static final String RANGE = "[range]";
    static final String IN = "[in]";
    static final String CONTAINS = "[contains]";
    static final String ANY = "[any]";
    static final String ALL = "[all]";

    static final String ASC = "[asc]";
    static final String DESC = "[desc]";

    List<NameValuePair> nameValuePairs = new ArrayList<>();

    public static DeliveryParameterBuilder params() {
        return new DeliveryParameterBuilder();
    }

    public DeliveryParameterBuilder filterEquals(String attribute, String value) {
        if (attribute != null) {
            nameValuePairs.add(new BasicNameValuePair(attribute, value));
        }
        return this;
    }

    public DeliveryParameterBuilder filterLessThan(String attribute, String value) {
        if (attribute != null) {
            nameValuePairs.add(new BasicNameValuePair(String.format("%s%s", attribute, LESS_THAN), value));
        }
        return this;
    }

    public DeliveryParameterBuilder filterLessThanEquals(String attribute, String value) {
        if (attribute != null) {
            nameValuePairs.add(new BasicNameValuePair(String.format("%s%s", attribute, LESS_THAN_OR_EQUALS), value));
        }
        return this;
    }

    public DeliveryParameterBuilder filterGreaterThan(String attribute, String value) {
        if (attribute != null) {
            nameValuePairs.add(new BasicNameValuePair(String.format("%s%s", attribute, GREATER_THAN), value));
        }
        return this;
    }

    public DeliveryParameterBuilder filterGreaterThanEquals(String attribute, String value) {
        if (attribute != null) {
            nameValuePairs.add(new BasicNameValuePair(String.format("%s%s", attribute, GREATER_THAN_OR_EQUALS), value));
        }
        return this;
    }

    public DeliveryParameterBuilder filterRange(String attribute, String lower, String upper) {
        if (attribute != null) {
            nameValuePairs.add(new BasicNameValuePair(String.format("%s%s", attribute, RANGE), String.join(",", lower, upper)));
        }
        return this;
    }

    public DeliveryParameterBuilder filterIn(String attribute, String... values) {
        if (attribute != null) {
            nameValuePairs.add(new BasicNameValuePair(String.format("%s%s", attribute, IN), String.join(",", values)));
        }
        return this;
    }

    public DeliveryParameterBuilder filterIn(String attribute, Collection<String> values) {
        return filterIn(attribute, values.toArray(new String[0]));
    }

    public DeliveryParameterBuilder filterContains(String attribute, String value) {
        if (attribute != null) {
            nameValuePairs.add(new BasicNameValuePair(String.format("%s%s", attribute, CONTAINS), value));
        }
        return this;
    }

    public DeliveryParameterBuilder filterAny(String attribute, String... values) {
        if (attribute != null) {
            nameValuePairs.add(new BasicNameValuePair(String.format("%s%s", attribute, ANY), String.join(",", values)));
        }
        return this;
    }

    public DeliveryParameterBuilder filterAny(String attribute, Collection<String> values) {
        return filterAny(attribute, values.toArray(new String[0]));
    }

    public DeliveryParameterBuilder filterAll(String attribute, String... values) {
        if (attribute != null) {
            nameValuePairs.add(new BasicNameValuePair(String.format("%s%s", attribute, ALL), String.join(",", values)));
        }
        return this;
    }

    public DeliveryParameterBuilder filterAll(String attribute, Collection<String> values) {
        return filterAll(attribute, values.toArray(new String[0]));
    }

    public DeliveryParameterBuilder orderByAsc(String attribute) {
        if (attribute != null) {
            nameValuePairs.add(new BasicNameValuePair(ORDER, String.format("%s%s", attribute, ASC)));
        }
        return this;
    }

    public DeliveryParameterBuilder orderByDesc(String attribute) {
        if (attribute != null) {
            nameValuePairs.add(new BasicNameValuePair(ORDER, String.format("%s%s", attribute, DESC)));
        }
        return this;
    }

    public DeliveryParameterBuilder page(Integer skip, Integer limit) {
        if (skip != null) {
            nameValuePairs.add(new BasicNameValuePair(SKIP, skip.toString()));
        }

        if (limit != null) {
            nameValuePairs.add(new BasicNameValuePair(LIMIT, limit.toString()));
        }
        return this;
    }

    public DeliveryParameterBuilder projection(String... elements) {
        if (elements != null) {
            nameValuePairs.add(new BasicNameValuePair(ELEMENTS, String.join(",", elements)));
        }
        return this;
    }

    public DeliveryParameterBuilder modularContentDepth(Integer depth) {
        if (depth != null) {
            nameValuePairs.add(new BasicNameValuePair(DEPTH, depth.toString()));
        }
        return this;
    }

    public DeliveryParameterBuilder excludeModularContent() {
        nameValuePairs.add(new BasicNameValuePair(DEPTH, "0"));
        return this;
    }

    public DeliveryParameterBuilder language(String language) {
        if (language != null) {
            nameValuePairs.add(new BasicNameValuePair(LANGUAGE, language));
        }
        return this;
    }

    public DeliveryParameterBuilder language(Locale language) {
        if (language != null) {
            nameValuePairs.add(new BasicNameValuePair(LANGUAGE, language.toString().replace('_', '-')));
        }
        return this;
    }

    public List<NameValuePair> build() {
        return nameValuePairs;
    }
}
