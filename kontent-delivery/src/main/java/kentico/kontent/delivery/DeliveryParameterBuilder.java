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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

/**
 * Convenience class to help construct query parameters for any Listing Response.
 * <p>
 * For example given the following criteria:
 * <ul>
 *     <li>Only 'article' types</li>
 *     <li>Only return the 'title', 'summary', 'post_date', and 'teaser_image' elements</li>
 *     <li>Only return items where the 'personas' selected include 'coffee_lover'</li>
 *     <li>Order by 'post_date' desc order</li>
 *     <li>Returning no linked items (0 levels of depth)</li>
 *     <li>Returning only the first 3 items</li>
 * </ul>
 * The DeliveryParameterBuilder will look like this:
 * <pre>{@code
 * DeliveryClient deliveryClient = new DeliveryClient("02a70003-e864-464e-b62c-e0ede97deb8c");
 * List<ArticleItem> articles = deliveryClient.getItems(
 *         ArticleItem.class,
 *         DeliveryParameterBuilder.params()
 *                 .filterEquals("system.type", "article")
 *                 .projection("title", "summary", "post_date", "teaser_image")
 *                 .filterContains("elements.personas", "coffee_lover")
 *                 .orderByDesc("elements.post_date")
 *                 .linkedItemsDepth(0)
 *                 .page(null, 3)
 *                 .build()
 * );
 * }</pre>
 *
 * Note: When using {@link DeliveryClient#getItems(Class, List)}, if the type is registered with the client via
 * {@link DeliveryClient#registerType(Class)}, {@link DeliveryClient#registerType(String, Class)}, or
 * {@link DeliveryClient#scanClasspathForMappings(String)}, the operator
 * {@code .filterEquals("system.type", your_mapped_type} will automatically be added prior to the request if
 * 'system_type' is not part of any other parameter in the request already.
 * <p>
 * <a href="https://docs.kontent.ai/reference/delivery-api#tag/Filtering-content">Filtering Operators</a>
 * <p>
 * When retrieving a list of content items from your project with {@link DeliveryClient#getItems(List)} and/or
 * {@link DeliveryClient#getItems(Class, List)}, you can filter large sets of content items by building query operators
 * from content elements and system attributes. Note that the query operators do not apply to linked items
 * represented by the {@link ContentItemsListingResponse#getLinkedItems()} field in the response.
 * <p>
 * If you want to limit the listing response only to certain elements, see {@link #projection(String...)}.
 * <p>
 * <a href="https://docs.kontent.ai/reference/delivery-api#tag/Filtering-content/filtering-by-system-values">Filtering by system
 * values</a>
 * <p>
 * To filter by system attribute values, you need to use a query attribute in the {@code 'system.<attribute_name>'}
 * format. The system attributes include 'id', 'name', 'codename', 'type', 'sitemap_locations', and 'last_modified'. For
 * example, to retrieve only content items based on the Article content type, you can use
 * {@code .filterEquals("system.type", "article"} as a query operator.
 * <p>
 * <a href="https://docs.kontent.ai/reference/delivery-api#tag/Filtering-content/filtering-by-element-values">Filtering by element
 * values</a>
 * <p>
 * To filter by content element values, you need to use a query attribute in the
 * {@code 'elements.<element_codename>'} format. For example, to retrieve only content items whose
 * {@link NumberElement} named 'Price' has a value of 16, you can use {@code .filterEquals("elements.price", "13"} as
 * a query operator.
 * <p>
 * <a href="https://docs.kontent.ai/reference/delivery-api#tag/Filtering-content/filtering-operators">Filtering operators</a>
 * <p>
 * You can use the following filtering operators both with the system attributes and element values:
 * <ul>
 *     <li>{@link #filterEquals(String, String)}</li>
 *     <li>{@link #filterLessThan(String, String)}</li>
 *     <li>{@link #filterLessThanEquals(String, String)}</li>
 *     <li>{@link #filterGreaterThan(String, String)}</li>
 *     <li>{@link #filterGreaterThanEquals(String, String)}</li>
 *     <li>{@link #filterRange(String, String, String)}</li>
 *     <li>{@link #filterIn(String, String...)}</li>
 *     <li>{@link #filterIn(String, Collection)}</li>
 *     <li>{@link #filterContains(String, String)}</li>
 *     <li>{@link #filterAny(String, String...)}</li>
 *     <li>{@link #filterAny(String, Collection)}</li>
 *     <li>{@link #filterAll(String, String...)}</li>
 *     <li>{@link #filterAll(String, Collection)}</li>
 * </ul>
 * <p>
 * <a href="https://docs.kontent.ai/reference/delivery-api#tag/Filtering-content/arrays-vs-simple-types">Arrays vs. simple types</a>
 * <p>
 * You can use the {@link #filterContains(String, String)}, {@link #filterAny(String, String...)},
 * {@link #filterAny(String, Collection)}, {@link #filterAny(String, String...)}, and
 * {@link #filterAny(String, Collection)} filtering operators only with arrays. Array attributes in Kentico Kontent
 * include the sitemap locations system object ({@link System#getSitemapLocations()}), and the {@link AssetsElement},
 * {@link LinkedItem}, {@link MultipleChoiceElement}, and {@link TaxonomyElement} content elements. All the
 * other system attributes and content type elements are simple types, such as strings or numbers.
 * <p>
 * <a href="https://docs.kontent.ai/reference/delivery-api#tag/Filtering-content/comparing-values">Comparing values</a>
 * <p>
 * The filtering operators {@link #filterLessThan(String, String)}, {@link #filterLessThanEquals(String, String)},
 * {@link #filterGreaterThan(String, String)}, {@link #filterGreaterThanEquals(String, String)}, and
 * {@link #filterRange(String, String, String)} work best with numbers. For example, you can retrieve products with
 * price larger or equal to 15 by using {@code .filterGreaterThanEquals("elements.price", "15")}. Attributes that store
 * dates (such as the last_modified system attribute or {@link DateTimeElement}s) are represented as strings.
 * <p>
 * If you use the filters on attributes with string values, the Delivery API tries to perform a string comparison. For
 * example, to retrieve content items modified during February and March you'd need to use a query such as
 * {@code .filterRange("system.last_modified", "2018-02-01", "2018-03-31")}, specifying both the start and end dates.
 * <p>
 * Paging
 * You can get only a small subset of a large collection of content items with the {@link #page(Integer, Integer)}
 * operator.  The first argument is the number of pages to skip, with the second being the page size.  Using these
 * argument, you can display a specific page of results and iterate over a list of content items or types.
 * <p>
 * For example, when you have a {@link ContentItemsListingResponse#getPagination()} with a {@link Pagination#getCount()}
 * of 10 items, you can use {@code .page(10, 10)} to retrieve the second page of results.
 * <p>
 * For details about the pagination data in each listing response, see the {@link Pagination} object.
 * <p>
 * <a href="https://docs.kontent.ai/reference/delivery-api#tag/Projection">Projection</a>
 * <p>
 * Choose which parts of content to retrieve with the {@link #projection(String...)} operator.
 * <p>
 * For example, to retrieve only the elements with codenames 'title', 'summary', and 'related_articles':
 * {@code .projection("title", "summary", "related_articles")}
 * <p>
 * <a href="https://docs.kontent.ai/reference/delivery-api#section/Linked-items-element">Linked items</a>
 * <p>
 * Content items might reference linked items using the {@link LinkedItem}. Recursively, these
 * linked items can reference another {@link LinkedItem} element. By default, only one level of
 * linked items are returned.
 * <p>
 * If you want to include more than one level of linked items in a response, use the
 * {@link #linkedItemsDepth(Integer)} operator.
 * <p>
 * If you want to exclude all linked items, use the {@link #excludeLinkedItems()} operator.
 * <p>
 * Note: When retrieving content items, linked items cannot be filtered.
 *
 * @see <a href="https://docs.kontent.ai/reference/delivery-api#operation/list-content-items">
 *      KenticoKontent API reference - Listing response</a>
 */
public class DeliveryParameterBuilder {

    static final String LANGUAGE = "language";
    static final String ELEMENTS = "elements";
    static final String ORDER = "order";
    static final String DEPTH = "depth";
    static final String SKIP = "skip";
    static final String LIMIT = "limit";
    static final String INCLUDE_TOTAL_COUNT = "includeTotalCount";

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

    /**
     * Constructs a new DeliveryParameterBuilder.
     *
     * @return A newly constructed DeliveryParameterBuilder.
     */
    public static DeliveryParameterBuilder params() {
        return new DeliveryParameterBuilder();
    }

    /**
     * Attribute value is the same as the specified value.
     *
     * @param attribute The attribute.
     * @param value     The value.
     * @return          This DeliveryParameterBuilder with the added operator.
     * @see             <a href="https://docs.kontent.ai/reference/delivery-api#tag/Filtering-content/filtering-operators">
     *                  More in Filtering operators.</a>
     */
    public DeliveryParameterBuilder filterEquals(String attribute, String value) {
        if (attribute != null) {
            nameValuePairs.add(new NameValuePair(attribute, value));
        }
        return this;
    }

    /**
     * Attribute value is less than the specified value.
     *
     * @param attribute The attribute.
     * @param value     The value.
     * @return          This DeliveryParameterBuilder with the added operator.
     * @see             <a href="https://docs.kontent.ai/reference/delivery-api#tag/Filtering-content/filtering-operators">
     *                  More in Filtering operators.</a>
     * @see             <a href="https://docs.kontent.ai/reference/delivery-api#tag/Filtering-content/comparing-values">
     *                  More in Comparing values.</a>
     */
    public DeliveryParameterBuilder filterLessThan(String attribute, String value) {
        if (attribute != null) {
            nameValuePairs.add(new NameValuePair(String.format("%s%s", attribute, LESS_THAN), value));
        }
        return this;
    }

    /**
     * Attribute value is less than or equals the specified value.
     *
     * @param attribute The attribute.
     * @param value     The value.
     * @return          This DeliveryParameterBuilder with the added operator.
     * @see             <a href="https://docs.kontent.ai/reference/delivery-api#tag/Filtering-content/filtering-operators">
     *                  More in Filtering operators.</a>
     * @see             <a href="https://docs.kontent.ai/reference/delivery-api#tag/Filtering-content/comparing-values">
     *                  More in Comparing values.</a>
     */
    public DeliveryParameterBuilder filterLessThanEquals(String attribute, String value) {
        if (attribute != null) {
            nameValuePairs.add(new NameValuePair(String.format("%s%s", attribute, LESS_THAN_OR_EQUALS), value));
        }
        return this;
    }

    /**
     * Attribute value is greater than the specified value.
     *
     * @param attribute The attribute.
     * @param value     The value.
     * @return          This DeliveryParameterBuilder with the added operator.
     * @see             <a href="https://docs.kontent.ai/reference/delivery-api#tag/Filtering-content/filtering-operators">
     *                  More in Filtering operators.</a>
     * @see             <a href="https://docs.kontent.ai/reference/delivery-api#tag/Filtering-content/comparing-values">
     *                  More in Comparing values.</a>
     */
    public DeliveryParameterBuilder filterGreaterThan(String attribute, String value) {
        if (attribute != null) {
            nameValuePairs.add(new NameValuePair(String.format("%s%s", attribute, GREATER_THAN), value));
        }
        return this;
    }

    /**
     * Attribute value is greater than or equals the specified value.
     *
     * @param attribute The attribute.
     * @param value     The value.
     * @return          This DeliveryParameterBuilder with the added operator.
     * @see             <a href="https://docs.kontent.ai/reference/delivery-api#tag/Filtering-content/filtering-operators">
     *                  More in Filtering operators.</a>
     * @see             <a href="https://docs.kontent.ai/reference/delivery-api#tag/Filtering-content/comparing-values">
     *                  More in Comparing values.</a>
     */
    public DeliveryParameterBuilder filterGreaterThanEquals(String attribute, String value) {
        if (attribute != null) {
            nameValuePairs.add(new NameValuePair(String.format("%s%s", attribute, GREATER_THAN_OR_EQUALS), value));
        }
        return this;
    }

    /**
     * Attribute value falls in the specified range of two values, both inclusive.
     *
     * @param attribute The attribute.
     * @param lower     The lower bound value.
     * @param upper     The upper bound value.
     * @return          This DeliveryParameterBuilder with the added operator.
     * @see             <a href="https://docs.kontent.ai/reference/delivery-api#tag/Filtering-content/filtering-operators">
     *                  More in Filtering operators.</a>
     * @see             <a href="https://docs.kontent.ai/reference/delivery-api#tag/Filtering-content/comparing-values">
     *                  More in Comparing values.</a>
     */
    public DeliveryParameterBuilder filterRange(String attribute, String lower, String upper) {
        if (attribute != null) {
            nameValuePairs.add(new NameValuePair(
                    String.format("%s%s", attribute, RANGE),
                    String.join(",", lower, upper)));
        }
        return this;
    }

    /**
     * Attribute value is in the specified list of values.
     *
     * @param attribute The attribute.
     * @param values    The values.
     * @return          This DeliveryParameterBuilder with the added operator.
     * @see             <a href="https://docs.kontent.ai/reference/delivery-api#tag/Filtering-content/filtering-operators">
     *                  More in Filtering operators.</a>
     */
    public DeliveryParameterBuilder filterIn(String attribute, String... values) {
        if (attribute != null) {
            nameValuePairs.add(new NameValuePair(String.format("%s%s", attribute, IN), String.join(",", values)));
        }
        return this;
    }

    /**
     * Attribute value is in the specified list of values.
     *
     * @param attribute The attribute.
     * @param values    The values.
     * @return          This DeliveryParameterBuilder with the added operator.
     * @see             <a href="https://docs.kontent.ai/reference/delivery-api#tag/Filtering-content/filtering-operators">
     *                  More in Filtering operators.</a>
     */
    public DeliveryParameterBuilder filterIn(String attribute, Collection<String> values) {
        return filterIn(attribute, values.toArray(new String[0]));
    }

    /**
     * Attribute with an array of values contains the specified value.
     *
     * @param attribute The attribute.
     * @param value     The value.
     * @return          This DeliveryParameterBuilder with the added operator.
     * @see             <a href="https://docs.kontent.ai/reference/delivery-api#tag/Filtering-content/filtering-operators">
     *                  More in Filtering operators.</a>
     * @see             <a href="https://docs.kontent.ai/reference/delivery-api#tag/Filtering-content/arrays-vs-simple-types">
     *                  More in Arrays vs. simple types.</a>
     */
    public DeliveryParameterBuilder filterContains(String attribute, String value) {
        if (attribute != null) {
            nameValuePairs.add(new NameValuePair(String.format("%s%s", attribute, CONTAINS), value));
        }
        return this;
    }

    /**
     * Attribute with an array of values contains any value from the specified list of values.
     *
     * @param attribute The attribute.
     * @param values    The values.
     * @return          This DeliveryParameterBuilder with the added operator.
     * @see             <a href="https://docs.kontent.ai/reference/delivery-api#tag/Filtering-content/filtering-operators">
     *                  More in Filtering operators.</a>
     * @see             <a href="https://docs.kontent.ai/reference/delivery-api#tag/Filtering-content/arrays-vs-simple-types">
     *                  More in Arrays vs. simple types.</a>
     */
    public DeliveryParameterBuilder filterAny(String attribute, String... values) {
        if (attribute != null) {
            nameValuePairs.add(new NameValuePair(String.format("%s%s", attribute, ANY), String.join(",", values)));
        }
        return this;
    }

    /**
     * Attribute with an array of values contains any value from the specified list of values.
     *
     * @param attribute The attribute.
     * @param values    The values.
     * @return          This DeliveryParameterBuilder with the added operator.
     * @see             <a href="https://docs.kontent.ai/reference/delivery-api#tag/Filtering-content/filtering-operators">
     *                  More in Filtering operators.</a>
     * @see             <a href="https://docs.kontent.ai/reference/delivery-api#tag/Filtering-content/arrays-vs-simple-types">
     *                  More in Arrays vs. simple types.</a>
     */
    public DeliveryParameterBuilder filterAny(String attribute, Collection<String> values) {
        return filterAny(attribute, values.toArray(new String[0]));
    }

    /**
     * Attribute with an array of values contains the specified list of values.
     *
     * @param attribute The attribute.
     * @param values    The values.
     * @return          This DeliveryParameterBuilder with the added operator.
     * @see             <a href="https://docs.kontent.ai/reference/delivery-api#tag/Filtering-content/filtering-operators">
     *                  More in Filtering operators.</a>
     * @see             <a href="https://docs.kontent.ai/reference/delivery-api#tag/Filtering-content/arrays-vs-simple-types">
     *                  More in Arrays vs. simple types.</a>
     */
    public DeliveryParameterBuilder filterAll(String attribute, String... values) {
        if (attribute != null) {
            nameValuePairs.add(new NameValuePair(String.format("%s%s", attribute, ALL), String.join(",", values)));
        }
        return this;
    }

    /**
     * Attribute with an array of values contains the specified list of values.
     *
     * @param attribute The attribute.
     * @param values    The values.
     * @return          This DeliveryParameterBuilder with the added operator.
     * @see             <a href="https://docs.kontent.ai/reference/delivery-api#tag/Filtering-content/filtering-operators">
     *                  More in Filtering operators.</a>
     * @see             <a href="https://docs.kontent.ai/reference/delivery-api#tag/Filtering-content/arrays-vs-simple-types">
     *                  More in Arrays vs. simple types.</a>
     */
    public DeliveryParameterBuilder filterAll(String attribute, Collection<String> values) {
        return filterAll(attribute, values.toArray(new String[0]));
    }

    /**
     * Sort the {@link ContentItemsListingResponse} by the attribute in ascending order.  As with filtering, you can use
     * both the 'system' and 'elements' attributes to sort data.
     *
     * @param attribute The attribute to sort on.
     * @return          This DeliveryParameterBuilder with the added operator.
     * @see             <a href="https://docs.kontent.ai/tutorials/develop-apps/get-content/getting-content?tech=rest#a-ordering-content-items">
     *                  More on Content Ordering</a>
     */
    public DeliveryParameterBuilder orderByAsc(String attribute) {
        if (attribute != null) {
            nameValuePairs.add(new NameValuePair(ORDER, String.format("%s%s", attribute, ASC)));
        }
        return this;
    }

    /**
     * Sort the {@link ContentItemsListingResponse} by the attribute in descending order.  As with filtering, you can
     * use both the 'system' and 'elements' attributes to sort data.
     *
     * @param attribute The attribute to sort on.
     * @return          This DeliveryParameterBuilder with the added operator.
     * @see             <a href="https://docs.kontent.ai/tutorials/develop-apps/get-content/getting-content?tech=rest#a-ordering-content-items">
     *                  More on Content Ordering</a>
     */
    public DeliveryParameterBuilder orderByDesc(String attribute) {
        if (attribute != null) {
            nameValuePairs.add(new NameValuePair(ORDER, String.format("%s%s", attribute, DESC)));
        }
        return this;
    }

    /**
     * Allows a subset of a query.  This can be used with {@link DeliveryClient#getItems(List)} and
     * {@link DeliveryClient#getItems(Class, List)}.  If the {@link Page} convention is desired, use
     * {@link DeliveryClient#getPageOfItems(Class, List)}.
     *
     * @param skip  The number of items to skip.  For the second page of 10, use the value '10' and limit of '10'.
     *              The API treats 'null' as a '0' skip value.
     * @param limit The size of the page.
     * @return      This DeliveryParameterBuilder with the added operator.
     * @see         Pagination
     * @see         Page
     * @see         <a href="https://docs.kontent.ai/reference/delivery-api#operation/list-content-items">More on Paging</a>
     */
    public DeliveryParameterBuilder page(Integer skip, Integer limit) {
        if (skip != null) {
            nameValuePairs.add(new NameValuePair(SKIP, skip.toString()));
        }

        if (limit != null) {
            nameValuePairs.add(new NameValuePair(LIMIT, limit.toString()));
        }
        return this;
    }

    /**
     * Choose which parts of content to retrieve.
     *
     * For example, to retrieve only the elements with codenames 'title', 'summary', and 'related_articles':
     * {@code .projection("title", "summary", "related_articles")}
     *
     * @param elements  The elements to retrieve.
     * @return          This DeliveryParameterBuilder with the added operator.
     * @see             <a href="https://docs.kontent.ai/reference/delivery-api#tag/Projection">More on Projection</a>
     */
    public DeliveryParameterBuilder projection(String... elements) {
        if (elements != null) {
            nameValuePairs.add(new NameValuePair(ELEMENTS, String.join(",", elements)));
        }
        return this;
    }

    /**
     * Choose the depth of linked items to return.
     *
     * @param depth The number of levels of depth to return.
     * @return      This DeliveryParameterBuilder with the added operator.
     * @see         <a href="https://docs.kontent.ai/reference/delivery-api#section/Linked-items-element">
     *              More on Linked items</a>
     */
    public DeliveryParameterBuilder linkedItemsDepth(Integer depth) {
        if (depth != null) {
            nameValuePairs.add(new NameValuePair(DEPTH, depth.toString()));
        }
        return this;
    }

    /**
     * Excludes all linked items.  Analogous to {@code .linkedItemsDepth(0)}
     *
     * @return  This DeliveryParameterBuilder with the added operator.
     * @see     <a href="https://docs.kontent.ai/reference/delivery-api#section/Linked-items-element">More on Linked items</a>
     */
    public DeliveryParameterBuilder excludeLinkedItems() {
        nameValuePairs.add(new NameValuePair(DEPTH, "0"));
        return this;
    }

    /**
     * Determines which language variant of content to return. By default, the API returns content in the default
     * project language. If the requested content is not available in the specified language variant, the API follows
     * the language fallbacks as configured in the Localization settings of the project. Example: en-US.
     *
     * @param language  The language variant to return.
     * @return          This DeliveryParameterBuilder with the added operator.
     * @see             <a href="https://docs.kontent.ai/tutorials/set-up-projects/set-up-languages/localization-in-kentico-kontent">More on Localization</a>
     * @see             <a href="https://docs.kontent.ai/tutorials/set-up-projects/set-up-languages/localization-in-kentico-kontent#section-language-fallbacks">
     *                  Language fallbacks</a>
     */
    public DeliveryParameterBuilder language(String language) {
        if (language != null) {
            nameValuePairs.add(new NameValuePair(LANGUAGE, language));
        }
        return this;
    }

    /**
     * Determines which language variant of content to return. By default, the API returns content in the default
     * project language. If the requested content is not available in the specified language variant, the API follows
     * the language fallbacks as configured in the Localization settings of the project. Example: en-US.
     *
     * @param language  The language variant to return.
     * @return          This DeliveryParameterBuilder with the added operator.
     * @see             <a href="https://docs.kontent.ai/tutorials/set-up-projects/set-up-languages/localization-in-kentico-kontent">More on Localization</a>
     * @see             <a href="https://docs.kontent.ai/tutorials/set-up-projects/set-up-languages/localization-in-kentico-kontent#section-language-fallbacks">
     *                  Language fallbacks</a>
     */
    public DeliveryParameterBuilder language(Locale language) {
        if (language != null) {
            nameValuePairs.add(new NameValuePair(LANGUAGE, language.toString().replace('_', '-')));
        }
        return this;
    }

    /**
     * Adds the information about the total number of content items matching your query.
     * The number doesn't include linked items returned as part of the modular_content property.
     * For the total number of items returned within the response, see the X-Request-Charge header.
     *
     * Asking for the total number of content items might increase the response time.
     *
     * When set to true, the pagination object returned in the API response contains an additional total_count property.
     *
     * @return          This DeliveryParameterBuilder with the added operator.
     * @see             <a href="https://docs.kontent.ai/reference/delivery-api#section/Content-item-object">Content items</a>
     */
    public DeliveryParameterBuilder includeTotalCount() {
        nameValuePairs.add(new NameValuePair(INCLUDE_TOTAL_COUNT, "true"));
        return this;
    }

    /**
     * Builds the query parameters to pass into the {@link DeliveryClient} from this DeliveryParametersBuilder.
     *
     * @return A list of NameValuePairs representing API query parameters.
     */
    public List<NameValuePair> build() {
        return nameValuePairs;
    }
}
