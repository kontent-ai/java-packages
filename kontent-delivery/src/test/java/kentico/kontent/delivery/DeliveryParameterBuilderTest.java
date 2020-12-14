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

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DeliveryParameterBuilderTest {

    @Test
    public void testEquals() {
        List<NameValuePair> params = DeliveryParameterBuilder.params().filterEquals("foo", "bar").build();
        Assert.assertEquals(1, params.size());
        Assert.assertEquals("foo", params.get(0).getName());
        Assert.assertEquals("bar", params.get(0).getValue());
    }

    @Test
    public void testEqualsNullAttr() {
        List<NameValuePair> params = DeliveryParameterBuilder.params().filterEquals(null, null).build();
        Assert.assertEquals(0, params.size());
    }

    @Test
    public void testNotEqualsNullAttr() {
        List<NameValuePair> params = DeliveryParameterBuilder.params().filterNotEquals(null, null).build();
        Assert.assertEquals(0, params.size());
    }

    @Test
    public void testNotEquals() {
        List<NameValuePair> params = DeliveryParameterBuilder.params().filterNotEquals("foo", "bar").build();
        Assert.assertEquals(1, params.size());
        Assert.assertEquals("foo[neq]", params.get(0).getName());
        Assert.assertEquals("bar", params.get(0).getValue());
    }

    @Test
    public void testEmptyNullAttr() {
        List<NameValuePair> params = DeliveryParameterBuilder.params().filterEmpty(null).build();
        Assert.assertEquals(0, params.size());
    }

    @Test
    public void testEmpty() {
        List<NameValuePair> params = DeliveryParameterBuilder.params().filterEmpty("foo").build();
        Assert.assertEquals(1, params.size());
        Assert.assertEquals("foo[empty]", params.get(0).getName());
        Assert.assertEquals(null, params.get(0).getValue());
    }

    @Test
    public void testNotEmptyNullAttr() {
        List<NameValuePair> params = DeliveryParameterBuilder.params().filterNotEmpty(null).build();
        Assert.assertEquals(0, params.size());
    }

    @Test
    public void testNotEmpty() {
        List<NameValuePair> params = DeliveryParameterBuilder.params().filterNotEmpty("foo").build();
        Assert.assertEquals(1, params.size());
        Assert.assertEquals("foo[nempty]", params.get(0).getName());
        Assert.assertEquals(null, params.get(0).getValue());
    }


    @Test
    public void testLessThan() {
        List<NameValuePair> params = DeliveryParameterBuilder.params().filterLessThan("foo", "bar").build();
        Assert.assertEquals(1, params.size());
        Assert.assertEquals("foo[lt]", params.get(0).getName());
        Assert.assertEquals("bar", params.get(0).getValue());
    }

    @Test
    public void testLessThanNullAttr() {
        List<NameValuePair> params = DeliveryParameterBuilder.params().filterLessThan(null, null).build();
        Assert.assertEquals(0, params.size());
    }

    @Test
    public void testLessThanEquals() {
        List<NameValuePair> params = DeliveryParameterBuilder.params().filterLessThanEquals("foo", "bar").build();
        Assert.assertEquals(1, params.size());
        Assert.assertEquals("foo[lte]", params.get(0).getName());
        Assert.assertEquals("bar", params.get(0).getValue());
    }

    @Test
    public void testLessThanEqualsNullAttr() {
        List<NameValuePair> params = DeliveryParameterBuilder.params().filterLessThanEquals(null, null).build();
        Assert.assertEquals(0, params.size());
    }

    @Test
    public void testGreaterThan() {
        List<NameValuePair> params = DeliveryParameterBuilder.params().filterGreaterThan("foo", "bar").build();
        Assert.assertEquals(1, params.size());
        Assert.assertEquals("foo[gt]", params.get(0).getName());
        Assert.assertEquals("bar", params.get(0).getValue());
    }

    @Test
    public void testGreaterThanNullAttr() {
        List<NameValuePair> params = DeliveryParameterBuilder.params().filterGreaterThan(null, null).build();
        Assert.assertEquals(0, params.size());
    }

    @Test
    public void testGreaterThanEquals() {
        List<NameValuePair> params = DeliveryParameterBuilder.params().filterGreaterThanEquals("foo", "bar").build();
        Assert.assertEquals(1, params.size());
        Assert.assertEquals("foo[gte]", params.get(0).getName());
        Assert.assertEquals("bar", params.get(0).getValue());
    }

    @Test
    public void testGreaterEqualsThanNullAttr() {
        List<NameValuePair> params = DeliveryParameterBuilder.params().filterGreaterThanEquals(null, null).build();
        Assert.assertEquals(0, params.size());
    }

    @Test
    public void testRange() {
        List<NameValuePair> params = DeliveryParameterBuilder.params().filterRange("foo", "bar", "foobar").build();
        Assert.assertEquals(1, params.size());
        Assert.assertEquals("foo[range]", params.get(0).getName());
        Assert.assertEquals("bar,foobar", params.get(0).getValue());
    }

    @Test
    public void testRangeNullAttr() {
        List<NameValuePair> params = DeliveryParameterBuilder.params().filterRange(null, null, null).build();
        Assert.assertEquals(0, params.size());
    }

    @Test
    public void testIn() {
        List<NameValuePair> params = DeliveryParameterBuilder.params().filterIn("foo", "bar", "foobar").build();
        Assert.assertEquals(1, params.size());
        Assert.assertEquals("foo[in]", params.get(0).getName());
        Assert.assertEquals("bar,foobar", params.get(0).getValue());
    }

    @Test
    public void testInCollection() {
        List<String> values = new ArrayList<>();
        values.add("bar");
        values.add("foobar");
        List<NameValuePair> params = DeliveryParameterBuilder.params().filterIn("foo", values).build();
        Assert.assertEquals(1, params.size());
        Assert.assertEquals("foo[in]", params.get(0).getName());
        Assert.assertEquals("bar,foobar", params.get(0).getValue());
    }

    @Test
    public void testNotIn() {
        List<NameValuePair> params = DeliveryParameterBuilder.params().filterNotIn("foo", "bar", "foobar").build();
        Assert.assertEquals(1, params.size());
        Assert.assertEquals("foo[nin]", params.get(0).getName());
        Assert.assertEquals("bar,foobar", params.get(0).getValue());
    }

    @Test
    public void testNotInCollection() {
        List<String> values = new ArrayList<>();
        values.add("bar");
        values.add("foobar");
        List<NameValuePair> params = DeliveryParameterBuilder.params().filterNotIn("foo", values).build();
        Assert.assertEquals(1, params.size());
        Assert.assertEquals("foo[nin]", params.get(0).getName());
        Assert.assertEquals("bar,foobar", params.get(0).getValue());
    }

    @Test
    public void testInNullAttr() {
        List<NameValuePair> params = DeliveryParameterBuilder.params().filterIn(null, null, null).build();
        Assert.assertEquals(0, params.size());
    }

    @Test
    public void testContains() {
        List<NameValuePair> params = DeliveryParameterBuilder.params().filterContains("foo", "bar").build();
        Assert.assertEquals(1, params.size());
        Assert.assertEquals("foo[contains]", params.get(0).getName());
        Assert.assertEquals("bar", params.get(0).getValue());
    }

    @Test
    public void testContainsNullAttr() {
        List<NameValuePair> params = DeliveryParameterBuilder.params().filterContains(null, null).build();
        Assert.assertEquals(0, params.size());
    }

    @Test
    public void testAny() {
        List<NameValuePair> params = DeliveryParameterBuilder.params().filterAny("foo", "bar", "foobar").build();
        Assert.assertEquals(1, params.size());
        Assert.assertEquals("foo[any]", params.get(0).getName());
        Assert.assertEquals("bar,foobar", params.get(0).getValue());
    }

    @Test
    public void testAnyCollection() {
        List<String> values = new ArrayList<>();
        values.add("bar");
        values.add("foobar");
        List<NameValuePair> params = DeliveryParameterBuilder.params().filterAny("foo", values).build();
        Assert.assertEquals(1, params.size());
        Assert.assertEquals("foo[any]", params.get(0).getName());
        Assert.assertEquals("bar,foobar", params.get(0).getValue());
    }

    @Test
    public void testAnyNullAttr() {
        List<NameValuePair> params = DeliveryParameterBuilder.params().filterAny(null, null, null).build();
        Assert.assertEquals(0, params.size());
    }

    @Test
    public void testAll() {
        List<NameValuePair> params = DeliveryParameterBuilder.params().filterAll("foo", "bar", "foobar").build();
        Assert.assertEquals(1, params.size());
        Assert.assertEquals("foo[all]", params.get(0).getName());
        Assert.assertEquals("bar,foobar", params.get(0).getValue());
    }

    @Test
    public void testAllCollection() {
        List<String> values = new ArrayList<>();
        values.add("bar");
        values.add("foobar");
        List<NameValuePair> params = DeliveryParameterBuilder.params().filterAll("foo", values).build();
        Assert.assertEquals(1, params.size());
        Assert.assertEquals("foo[all]", params.get(0).getName());
        Assert.assertEquals("bar,foobar", params.get(0).getValue());
    }

    @Test
    public void testAllNullAttr() {
        List<NameValuePair> params = DeliveryParameterBuilder.params().filterAll(null, null, null).build();
        Assert.assertEquals(0, params.size());
    }

    @Test
    public void testOrderAsc() {
        List<NameValuePair> params = DeliveryParameterBuilder.params().orderByAsc("foobar").build();
        Assert.assertEquals(1, params.size());
        Assert.assertEquals("order", params.get(0).getName());
        Assert.assertEquals("foobar[asc]", params.get(0).getValue());
    }

    @Test
    public void testOrderAscNullAttr() {
        List<NameValuePair> params = DeliveryParameterBuilder.params().orderByAsc(null).build();
        Assert.assertEquals(0, params.size());
    }

    @Test
    public void testOrderDesc() {
        List<NameValuePair> params = DeliveryParameterBuilder.params().orderByDesc("foobar").build();
        Assert.assertEquals(1, params.size());
        Assert.assertEquals("order", params.get(0).getName());
        Assert.assertEquals("foobar[desc]", params.get(0).getValue());
    }

    @Test
    public void testOrderDescNullAttr() {
        List<NameValuePair> params = DeliveryParameterBuilder.params().orderByDesc(null).build();
        Assert.assertEquals(0, params.size());
    }

    @Test
    public void testPageSkip() {
        List<NameValuePair> params = DeliveryParameterBuilder.params().page(1, null).build();
        Assert.assertEquals(1, params.size());
        Assert.assertEquals("skip", params.get(0).getName());
        Assert.assertEquals("1", params.get(0).getValue());
    }

    @Test
    public void testPageLimit() {
        List<NameValuePair> params = DeliveryParameterBuilder.params().page(null, 1).build();
        Assert.assertEquals(1, params.size());
        Assert.assertEquals("limit", params.get(0).getName());
        Assert.assertEquals("1", params.get(0).getValue());
    }

    @Test
    public void testPageSkipLimit() {
        List<NameValuePair> params = DeliveryParameterBuilder.params().page(1, 1).build();
        Assert.assertEquals(2, params.size());
        Assert.assertEquals("skip", params.get(0).getName());
        Assert.assertEquals("1", params.get(0).getValue());
        Assert.assertEquals("limit", params.get(1).getName());
        Assert.assertEquals("1", params.get(1).getValue());
    }

    @Test
    public void testProjection() {
        List<NameValuePair> params = DeliveryParameterBuilder.params().projection("foo", "bar").build();
        Assert.assertEquals(1, params.size());
        Assert.assertEquals("elements", params.get(0).getName());
        Assert.assertEquals("foo,bar", params.get(0).getValue());
    }

    @Test
    @SuppressWarnings("all")
    public void testProjectionNull() {
        List<NameValuePair> params = DeliveryParameterBuilder.params().projection(null).build();
        Assert.assertEquals(0, params.size());
    }

    @Test
    public void testLinkedItemsDepth() {
        List<NameValuePair> params = DeliveryParameterBuilder.params().linkedItemsDepth(3).build();
        Assert.assertEquals(1, params.size());
        Assert.assertEquals("depth", params.get(0).getName());
        Assert.assertEquals("3", params.get(0).getValue());
    }

    @Test
    public void testLinkedItemsDepthNull() {
        List<NameValuePair> params = DeliveryParameterBuilder.params().linkedItemsDepth(null).build();
        Assert.assertEquals(0, params.size());
    }

    @Test
    public void testExcludeLinkedItems() {
        List<NameValuePair> params = DeliveryParameterBuilder.params().excludeLinkedItems().build();
        Assert.assertEquals(1, params.size());
        Assert.assertEquals("depth", params.get(0).getName());
        Assert.assertEquals("0", params.get(0).getValue());
    }

    @Test
    public void testLanguage() {
        List<NameValuePair> params = DeliveryParameterBuilder.params().language(Locale.US.toLanguageTag()).build();
        Assert.assertEquals(1, params.size());
        Assert.assertEquals("language", params.get(0).getName());
        Assert.assertEquals("en-US", params.get(0).getValue());
    }

    @Test
    public void testLocaleLanguage() {
        List<NameValuePair> params = DeliveryParameterBuilder.params().language(new Locale("pt", "br")).build();
        Assert.assertEquals(1, params.size());
        Assert.assertEquals("language", params.get(0).getName());
        Assert.assertEquals("pt-BR", params.get(0).getValue());
    }

    @Test
    public void testLocaleWithNoVariantLanguage() {
        List<NameValuePair> params = DeliveryParameterBuilder.params().language(new Locale("en")).build();
        Assert.assertEquals(1, params.size());
        Assert.assertEquals("language", params.get(0).getName());
        Assert.assertEquals("en", params.get(0).getValue());
    }

    @Test
    public void testIncludeTotalCount() {
        List<NameValuePair> params = DeliveryParameterBuilder.params().includeTotalCount().build();
        Assert.assertEquals(1, params.size());
        Assert.assertEquals("includeTotalCount", params.get(0).getName());
        Assert.assertEquals("true", params.get(0).getValue());
    }

    @Test
    @SuppressWarnings("all")
    public void testLanguageNull() {
        String nullValue = null;
        List<NameValuePair> params = DeliveryParameterBuilder.params().language(nullValue).build();
        Assert.assertEquals(0, params.size());
    }
}
