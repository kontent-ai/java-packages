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

package kontent.ai.delivery.template;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ViewResolverConfiguration {

    List<String> prefixes = new ArrayList<>();
    String suffix = ".html";

    public ViewResolverConfiguration() {
        this.prefixes.addAll(Arrays.asList("kontent/ai/templates/", "META-INF/kontent/ai/templates/"));
    }

    public ViewResolverConfiguration addPrefixes(String... prefixes) {
        this.prefixes.addAll(Arrays.asList(prefixes));
        return this;
    }

    public ViewResolverConfiguration setSuffix(String suffix) {
        this.suffix = suffix;
        return this;
    }

    public List<String> getPrefixes() {
        return prefixes;
    }

    public void setPrefixes(List<String> prefixes) {
        this.prefixes = prefixes;
    }

    public String getSuffix() {
        return suffix;
    }
}
