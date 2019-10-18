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

import com.fasterxml.jackson.databind.JsonNode;
import kentico.kontent.delivery.template.TemplateEngineConfig;
import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.internal.operators.completable.CompletableFromRunnable;

import java.io.Closeable;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * Executes requests against the Kentico Cloud Delivery API.
 *
 * @see <a href="https://developer.kenticocloud.com/v2/reference#authentication">
 *      KenticoCloud API reference - Authentication</a>
 * @see <a href="https://developer.kenticocloud.com/v2/reference#delivery-api">
 *      KenticoCloud API reference - Delivery API</a>
 */
@lombok.extern.slf4j.Slf4j
public class DeliveryClient implements Closeable {

    private AsyncDeliveryClient asyncDeliveryClient;

    /**
     * Initializes a new instance of the {@link DeliveryClient} class for retrieving content of the specified project.
     *
     * @param deliveryOptions   The settings of the Kentico Cloud project.
     * @throws                  IllegalArgumentException Thrown if the arguments in the {@link DeliveryOptions} are
     *                          invalid.
     * @see                     DeliveryOptions
     */
    public DeliveryClient(DeliveryOptions deliveryOptions) {
        this(deliveryOptions, new TemplateEngineConfig());
    }

    /**
     * Initializes a new instance of the {@link DeliveryClient} class for retrieving content of the specified project.
     *
     * @param deliveryOptions       The settings of the Kentico Cloud project.
     * @param templateEngineConfig  Configuration object used for customization of template render engines for inline
     *                              content
     * @throws                      IllegalArgumentException Thrown if the arguments in the {@link DeliveryOptions} are
     *                              invalid.
     * @see                         DeliveryOptions
     * @see                         TemplateEngineConfig
     */
    public DeliveryClient(DeliveryOptions deliveryOptions, TemplateEngineConfig templateEngineConfig) {
        this.asyncDeliveryClient = new AsyncDeliveryClient(deliveryOptions, templateEngineConfig);
    }

    /**
     * Initializes a new instance of the {@link DeliveryClient} class for retrieving content of the specified project.
     *
     * @param projectId The Project ID associated with your Kentico Cloud account.  Must be in the format of an
     *                  {@link java.util.UUID}.
     * @throws          IllegalArgumentException Thrown if the Project id is invalid.
     */
    public DeliveryClient(String projectId) {
        this.asyncDeliveryClient = new AsyncDeliveryClient(new DeliveryOptions(projectId));
    }

    /**
     * Initializes a new instance of the {@link DeliveryClient} class for retrieving content of the specified project,
     * and configures the preview API.
     * <p>
     * An API key (which comes in the form of a verified <a href="https://jwt.io/">JSON Web Token</a>) provides
     * read-only access to a single project.  You can find the API keys for your project in the API keys section in the
     * <a href="https://app.kenticocloud.com/">Kentico Cloud</a> app.
     *
     * @param projectId     The Project ID associated with your Kentico Cloud account.  Must be in the format of an
     *                      {@link java.util.UUID}.
     * @param previewApiKey The Preview API key configured with your Kentico Cloud account.
     * @throws              IllegalArgumentException Thrown if the Project id is invalid.
     */
    public DeliveryClient(String projectId, String previewApiKey) {
        this.asyncDeliveryClient = new AsyncDeliveryClient(new DeliveryOptions(projectId, previewApiKey));
    }

    /**
     * Returns a {@link ContentItemsListingResponse} for all items in the project.  Beware, this is an incredibly
     * expensive operation on a big project, you were forewarned.
     *
     * @return  A {@link ContentItemsListingResponse} for all items in the project.
     * @throws  KenticoIOException If an {@link IOException} is thrown interacting with KenticoCloud.
     * @see     ContentItem
     * @see     ContentItemsListingResponse
     * @see     <a href="https://developer.kenticocloud.com/v2/reference#list-content-items">
     *          KenticoCloud API reference - List content items</a>
     */
    public ContentItemsListingResponse getItems() {
        return getItems(Collections.emptyList());
    }

    /**
     * Returns a {@link ContentItemsListingResponse} using the query provided.  For simplicity, it is recommended to use
     * {@link DeliveryParameterBuilder#params()} followed by {@link DeliveryParameterBuilder#build()} to generate the
     * query parameters.
     *
     * @param params    The query parameters to use for this listing request.
     * @return          A {@link ContentItemsListingResponse} for items matching the query parameters.
     * @throws          KenticoIOException If an {@link IOException} is thrown interacting with KenticoCloud.
     * @see             DeliveryParameterBuilder
     * @see             ContentItem
     * @see             ContentItemsListingResponse
     * @see             <a href="https://developer.kenticocloud.com/v2/reference#listing-response">
     *                  KenticoCloud API reference - Listing response</a>
     * @see             <a href="https://developer.kenticocloud.com/v2/reference#list-content-items">
     *                  KenticoCloud API reference - List content items</a>
     */
    public ContentItemsListingResponse getItems(List<NameValuePair> params) {
        return asyncDeliveryClient.getItems(params).blockingGet();
    }

    /**
     * Returns a new instance of {@code List<T>} by mapping fields to elements from a
     * {@link ContentItemsListingResponse} using the query provided.  For simplicity, it is recommended to use
     * {@link DeliveryParameterBuilder#params()} followed by {@link DeliveryParameterBuilder#build()} to generate the
     * query parameters.  For performance reasons, if there are is a {@link System#type} registered with T, then
     * the 'system.type=YOUR_TYPE' query parameter will be added automatically if 'system.type' is not part of the
     * query.
     * <p>
     * Element fields are mapped by automatically CamelCasing and checking for equality, unless otherwise annotated by
     * an {@link ElementMapping} annotation.  T must have a default constructor and have standard setter methods.
     *
     * @param tClass    The class which a new instance should be returned from.
     * @param <T>       The type of class.
     * @param params    The query parameters to use for this listing request.
     * @return          An instance of {@code List<T>} with data mapped from the {@link ContentItem} list in this
     *                  response.
     * @throws          KenticoIOException If an {@link IOException} is thrown interacting with KenticoCloud.
     * @see             DeliveryParameterBuilder
     * @see             ContentItemsListingResponse#castTo(Class)
     * @see             ContentItemMapping
     * @see             ElementMapping
     * @see             #registerType(Class)
     * @see             #registerType(String, Class)
     */
    public <T> List<T> getItems(Class<T> tClass, List<NameValuePair> params) {
        return asyncDeliveryClient.getItems(tClass, params).blockingGet();
    }

    /**
     * Returns a {@link ContentItemResponse} for the {@link ContentItem} in the project with the given
     * {@link System#codename}.
     *
     * @param contentItemCodename   The {@link System#getCodename()} for the {@link ContentItem} requested.
     * @return                      A {@link ContentItemResponse} for the {@link ContentItem} in the project.
     * @throws                      KenticoIOException If an {@link IOException} is thrown connecting to Kentico.
     * @see                         ContentItem
     * @see                         ContentItemResponse
     * @see                         <a href="https://developer.kenticocloud.com/v2/reference#view-a-content-item">
     *                              KenticoCloud API reference - View a content item</a>
     */
    public ContentItemResponse getItem(String contentItemCodename) {
        return getItem(contentItemCodename, Collections.emptyList());
    }

    /**
     * Returns a new instance of {@code List<T>} by mapping fields to elements from a
     * {@link ContentItemsListingResponse}.  Beware, this is an incredibly expensive operation on a big project, you
     * were forewarned.  It is recommended to use {@link #getItems(Class, List)} using a
     * {@link DeliveryParameterBuilder} instead.  For performance reasons, if there are is a {@link System#type}
     * registered with T, then the 'system.type=YOUR_TYPE' query parameter will be added automatically.
     * <p>
     * Element fields are mapped by automatically CamelCasing and checking for equality, unless otherwise annotated by
     * an {@link ElementMapping} annotation.  T must have a default constructor and have standard setter methods.
     *
     * @param tClass    The class which a new instance should be returned from.
     * @param <T>       The type of class.
     * @return          An instance of {@code List<T>} with data mapped from the {@link ContentItem} list in this
     *                  response.
     * @throws          KenticoIOException If an {@link IOException} is thrown interacting with KenticoCloud.
     * @see             DeliveryParameterBuilder
     * @see             ContentItemsListingResponse#castTo(Class)
     * @see             ContentItemMapping
     * @see             ElementMapping
     * @see             #registerType(Class)
     * @see             #registerType(String, Class)
     */
    public <T> List<T> getItems(Class<T> tClass) {
        return getItems(tClass, Collections.emptyList());
    }

    /**
     * Returns {@link Page} containing a new instance of {@code List<T>} by mapping fields to elements from a
     * {@link ContentItemsListingResponse} using the query provided.  For simplicity, it is recommended to use
     * {@link DeliveryParameterBuilder#params()} followed by {@link DeliveryParameterBuilder#build()} to generate the
     * query parameters.
     * <p>
     * Element fields are mapped by automatically CamelCasing and checking for equality, unless otherwise annotated by
     * an {@link ElementMapping} annotation.  T must have a default constructor and have standard setter methods.
     *
     * @param tClass    The class which a new instance should be returned from.
     * @param <T>       The type of class.
     * @param params    The query parameters to use for this listing request.
     * @return          An instance of {@code Page<T>} with data mapped from the {@link ContentItem} list in this
     *                  response.
     * @throws          KenticoIOException If an {@link IOException} is thrown interacting with KenticoCloud.
     * @see             Page
     * @see             Pagination
     * @see             DeliveryParameterBuilder
     * @see             ContentItemsListingResponse#castTo(Class)
     * @see             ContentItemMapping
     * @see             ElementMapping
     * @see             #registerType(Class)
     * @see             #registerType(String, Class)
     * @see             <a href="https://developer.kenticocloud.com/v2/reference#listing-response-paging">
     *                  KenticoCloud API reference - Listing response paging</a>
     */
    public <T> Page<T> getPageOfItems(Class<T> tClass, List<NameValuePair> params) {
        return asyncDeliveryClient.getPageOfItems(tClass, params).blockingGet();
    }

    /**
     * Returns the next {@link Page} of results.
     *
     * @param currentPage   The instance of the {@link Page} preceding the {@link Page} being requested.
     * @param <T>           The type of class.
     * @return              An instance of {@code Page<T>} with data mapped from the {@link ContentItem} list in this
     *                      response.
     * @throws              KenticoIOException If an {@link IOException} is thrown interacting with KenticoCloud.
     * @see                 Page
     * @see                 Pagination
     * @see                 DeliveryParameterBuilder
     * @see                 ContentItemsListingResponse#castTo(Class)
     * @see                 ContentItemMapping
     * @see                 ElementMapping
     * @see                 #registerType(Class)
     * @see                 #registerType(String, Class)
     * @see                 <a href="https://developer.kenticocloud.com/v2/reference#listing-response-paging">
     *                      KenticoCloud API reference - Listing response paging</a>
     */
    public <T> Page<T> getNextPage(Page<T> currentPage) {
        return asyncDeliveryClient.getNextPage(currentPage).blockingGet();
    }

    /**
     * Returns new instance of T by mapping fields to elements from a {@link ContentItem} in the project with the given
     * {@link System#codename}.
     * <p>
     * Element fields are mapped by automatically CamelCasing and checking for equality, unless otherwise annotated by
     * an {@link ElementMapping} annotation.  T must have a default constructor and have standard setter methods.
     *
     * @param tClass                The class which a new instance should be returned from.
     * @param <T>                   The type of class.
     * @param contentItemCodename   The {@link System#getCodename()} for the {@link ContentItem} requested.
     * @return                      An instance of {@code Page<T>} with data mapped from the {@link ContentItem} list in
     *                              this response.
     * @throws                      KenticoIOException If an {@link IOException} is thrown interacting with
     *                              KenticoCloud.
     * @see                         ContentItem
     * @see                         ContentItemResponse#castTo(Class)
     * @see                         ContentItemMapping
     * @see                         ElementMapping
     * @see                         #registerType(Class)
     * @see                         #registerType(String, Class)
     * @see                         <a href="https://developer.kenticocloud.com/v2/reference#view-a-content-item">
     *                              KenticoCloud API reference - View a content item</a>
     */
    public <T> T getItem(String contentItemCodename, Class<T> tClass) {
        return getItem(contentItemCodename, tClass, Collections.emptyList());
    }

    /**
     * Returns a {@link ContentItemResponse} for the {@link ContentItem} in the project with the given
     * {@link System#codename}.  Query parameters can be provided, which can be used to change linked item
     * depth or to apply a projection. For simplicity, it is recommended to use
     * {@link DeliveryParameterBuilder#params()} followed by {@link DeliveryParameterBuilder#build()} to generate the
     * query parameters.
     *
     * @param contentItemCodename   The {@link System#codename} for the {@link ContentItem} requested.
     * @param params                The query parameters to use for this ContentItemResponse request.
     * @return                      A {@link ContentItemResponse} for the {@link ContentItem} in the project.
     * @throws                      KenticoIOException If an {@link IOException} is thrown connecting to Kentico.
     * @see                         DeliveryParameterBuilder
     * @see                         ContentItem
     * @see                         ContentItemResponse
     * @see                         <a href="https://developer.kenticocloud.com/v2/reference#view-a-content-item">
     *                              KenticoCloud API reference - View a content item</a>
     * @see                         <a href="https://developer.kenticocloud.com/v2/reference#listing-response">
     *                              KenticoCloud API reference - Listing response</a>
     */
    public ContentItemResponse getItem(String contentItemCodename, List<NameValuePair> params) {
        return asyncDeliveryClient.getItem(contentItemCodename, params).blockingGet();
    }

    /**
     * Returns new instance of T by mapping fields to elements from a {@link ContentItem} in the project with the given
     * {@link System#codename}.  Query parameters can be provided, which can be used to change linked item
     * depth or to apply a projection. For simplicity, it is recommended to use
     * {@link DeliveryParameterBuilder#params()} followed by {@link DeliveryParameterBuilder#build()} to generate the
     * query parameters.
     * <p>
     * Element fields are mapped by automatically CamelCasing and checking for equality, unless otherwise annotated by
     * an {@link ElementMapping} annotation.  T must have a default constructor and have standard setter methods.
     *
     * @param contentItemCodename   The {@link System#getCodename()} for the {@link ContentItem} requested.
     * @param tClass                The class which a new instance should be returned from.
     * @param <T>                   The type of class.
     * @param params                The query parameters to use for this ContentItemResponse request.
     * @return                      An instance of {@code Page<T>} with data mapped from the {@link ContentItem} list in
     *                              this response.
     * @throws                      KenticoIOException If an {@link IOException} is thrown interacting with
     *                              KenticoCloud.
     * @see                         ContentItem
     * @see                         ContentItemResponse#castTo(Class)
     * @see                         ContentItemMapping
     * @see                         ElementMapping
     * @see                         #registerType(Class)
     * @see                         #registerType(String, Class)
     * @see                         <a href="https://developer.kenticocloud.com/v2/reference#view-a-content-item">
     *                              KenticoCloud API reference - View a content item</a>
     */
    public <T> T getItem(String contentItemCodename, Class<T> tClass, List<NameValuePair> params) {
        return asyncDeliveryClient.getItem(contentItemCodename, tClass, params).blockingGet();
    }

    /**
     * Returns a {@link ContentTypesListingResponse} detailing all {@link ContentType}s in the project.
     *
     * @return  A ContentTypesListingResponse containing all {@link ContentType}s in the project.
     * @throws  KenticoIOException If an {@link IOException} is thrown interacting with KenticoCloud.
     * @see     ContentType
     * @see     DeliveryClient#getTypes()
     * @see     DeliveryClient#getTypes(List)
     * @see     <a href="https://developer.kenticocloud.com/v2/reference#list-content-types">
     *          KenticoCloud API reference - List content types</a>
     * @see     <a href="https://developer.kenticocloud.com/v2/reference#content-type-object">
     *          KenticoCloud API reference - Content type object</a>
     */
    public ContentTypesListingResponse getTypes() {
        return getTypes(Collections.emptyList());
    }

    /**
     * Returns a {@link ContentTypesListingResponse} detailing all {@link ContentType}s in the project.  Query
     * parameters can be provided, which can be used for paging. For simplicity, it is recommended to use
     * {@link DeliveryParameterBuilder#params()} followed by {@link DeliveryParameterBuilder#build()} to generate the
     * query parameters.
     *
     * @param params    The query parameters to use for this ContentItemResponse request.
     * @return          A ContentTypesListingResponse containing a page or all {@link ContentType}s.
     * @throws          KenticoIOException If an {@link IOException} is thrown interacting with KenticoCloud.
     * @see             Pagination
     * @see             ContentType
     * @see             <a href="https://developer.kenticocloud.com/v2/reference#list-content-types">
     *                  KenticoCloud API reference - List content types</a>
     * @see             <a href="https://developer.kenticocloud.com/v2/reference#content-type-object">
     *                  KenticoCloud API reference - Content type object</a>
     */
    public ContentTypesListingResponse getTypes(List<NameValuePair> params) {
        return asyncDeliveryClient.getTypes(params).blockingGet();
    }

    /**
     * Returns the {@link ContentType} with a {@link System#codename}.
     *
     * @param contentTypeCodeName   The {@link System#codename} for the ContentType request.
     * @return                      The ContentType matching {@link System#codename}.
     * @throws                      KenticoIOException If an {@link IOException} is thrown interacting with
     *                              KenticoCloud.
     * @see                         ContentType
     * @see                         <a href="https://developer.kenticocloud.com/v2/reference#view-a-content-type">
     *                              KenticoCloud API reference - View a content type</a>
     * @see                         <a href="https://developer.kenticocloud.com/v2/reference#content-type-object">
     *                              KenticoCloud API reference - Content type object</a>
     */
    public ContentType getType(String contentTypeCodeName) {
        return asyncDeliveryClient.getType(contentTypeCodeName).blockingGet();
    }

    /**
     * Retrieve a specific {@link ContentType} {@link Element} by specifying its codename and its parent content type.
     *
     * @param contentTypeCodeName   The {@link System#codename} for the ContentType request.
     * @param elementCodeName       The codename for the Element.
     * @return                      The Element for the ContentType requested.
     * @see                         <a
     *                              href="https://developer.kenticocloud.com/v2/reference#view-a-content-type-element">
     *                              KenticoCloud API reference - View a content type element</a>
     * @see                         <a
     *                              href="https://developer.kenticocloud.com/v2/reference#content-type-element-object">
     *                              KenticoCloud API reference - Content element model</a>
     */
    public Element getContentTypeElement(String contentTypeCodeName, String elementCodeName) {
        return getContentTypeElement(contentTypeCodeName, elementCodeName, Collections.emptyList());
    }

    /**
     * Retrieve a specific {@link ContentType} {@link Element} by specifying its codename and its parent content type.
     * Generally you will want to use {@link #getContentTypeElement(String, String)} instead, but this allows
     * specification of query parameters.
     *
     * @param contentTypeCodeName   The {@link System#codename} for the ContentType request.
     * @param elementCodeName       The codename for the Element.
     * @param params                Query params to add to the request.
     * @return                      The Element for the ContentType requested.
     * @see                         #getContentTypeElement(String, String)
     * @see                         <a
     *                              href="https://developer.kenticocloud.com/v2/reference#view-a-content-type-element">
     *                              KenticoCloud API reference - View a content type element</a>
     * @see                         <a
     *                              href="https://developer.kenticocloud.com/v2/reference#content-type-element-object">
     *                              KenticoCloud API reference - Content element model</a>
     */
    public Element getContentTypeElement(
            String contentTypeCodeName, String elementCodeName, List<NameValuePair> params) {
        return asyncDeliveryClient.getContentTypeElement(contentTypeCodeName, elementCodeName, params)
                .blockingGet();
    }

    /**
     * Retrieve all {@link TaxonomyGroup} in your project.  Returns them ordered alphabetically by codename.
     *
     * @return  A response object containing all {@link TaxonomyGroup} in the project.
     * @see     TaxonomyGroup
     * @see     Taxonomy
     * @see     <a href="https://developer.kenticocloud.com/v2/reference#list-taxonomy-groups">
     *          KenticoCloud API reference - List taxonomy groups</a>
     * @see     <a href="https://developer.kenticocloud.com/v2/reference#taxonomy-group-object">
     *          KenticoCloud API reference - Taxonomy group model</a>
     */
    public TaxonomyGroupListingResponse getTaxonomyGroups() {
        return getTaxonomyGroups(Collections.emptyList());
    }

    /**
     * Retrieve a page of {@link TaxonomyGroup} in your project.
     * Use {@link DeliveryParameterBuilder#page(Integer, Integer)}.
     *
     * @param params    Built from {@link DeliveryParameterBuilder#page(Integer, Integer)}
     * @return          A response object containing all {@link TaxonomyGroup} in the project.
     * @see             TaxonomyGroup
     * @see             Taxonomy
     * @see             <a href="https://developer.kenticocloud.com/v2/reference#list-taxonomy-groups">
     *                  KenticoCloud API reference - List taxonomy groups</a>
     * @see             <a href="https://developer.kenticocloud.com/v2/reference#taxonomy-group-object">
     *                  KenticoCloud API reference - Taxonomy group model</a>
     */
    public TaxonomyGroupListingResponse getTaxonomyGroups(List<NameValuePair> params) {
        return asyncDeliveryClient.getTaxonomyGroups(params).blockingGet();
    }

    /**
     * Retrieve a specific {@link TaxonomyGroup} from your project by specifying its codename.
     *
     * @param taxonomyGroupCodename The codename of a specfic taxonomy group.
     * @return                      The {@link TaxonomyGroup}.
     * @see                         TaxonomyGroup
     * @see                         Taxonomy
     * @see                         <a href="https://developer.kenticocloud.com/v2/reference#view-a-taxonomy-group">
     *                              KenticoCloud API reference - View a taxonomy group</a>
     * @see                         <a href="https://developer.kenticocloud.com/v2/reference#taxonomy-group-object">
     *                              KenticoCloud API reference - Taxonomy group model</a>
     */
    public TaxonomyGroup getTaxonomyGroup(String taxonomyGroupCodename) {
        return getTaxonomyGroup(taxonomyGroupCodename, Collections.emptyList());
    }

    /**
     * Retrieve a specific {@link TaxonomyGroup} from your project by specifying its codename.  Generally you will want
     * to use {@link #getTaxonomyGroup(String)} instead, but this allows specification of query parameters.
     *
     * @param taxonomyGroupCodename The codename of a specfic taxonomy group.
     * @param params                Query params to add to the request.
     * @return                      The {@link TaxonomyGroup}.
     * @see                         TaxonomyGroup
     * @see                         Taxonomy
     * @see                         <a href="https://developer.kenticocloud.com/v2/reference#view-a-taxonomy-group">
     *                              KenticoCloud API reference - View a taxonomy group</a>
     * @see                         <a href="https://developer.kenticocloud.com/v2/reference#taxonomy-group-object">
     *                              KenticoCloud API reference - Taxonomy group model</a>
     */
    public TaxonomyGroup getTaxonomyGroup(String taxonomyGroupCodename, List<NameValuePair> params) {
        return asyncDeliveryClient.getTaxonomyGroup(taxonomyGroupCodename, params).blockingGet();
    }

    /**
     * Retrieve the currently configured {@link ContentLinkUrlResolver} for this client.
     *
     * @return  This client's {@link ContentLinkUrlResolver}
     * @see     ContentLinkUrlResolver
     */
    public ContentLinkUrlResolver getContentLinkUrlResolver() {
        return asyncDeliveryClient.getContentLinkUrlResolver();
    }

    /**
     * Sets the {@link ContentLinkUrlResolver} for this client.
     *
     * @param contentLinkUrlResolver    Sets the ContentLinkResolver of this client.
     * @see                             ContentLinkUrlResolver
     */
    public void setContentLinkUrlResolver(ContentLinkUrlResolver contentLinkUrlResolver) {
        asyncDeliveryClient.setContentLinkUrlResolver(contentLinkUrlResolver);
    }

    /**
     * Retrieve the currently configured {@link BrokenLinkUrlResolver} for this client.
     *
     * @return  This client's {@link BrokenLinkUrlResolver}
     * @see     BrokenLinkUrlResolver
     */
    public BrokenLinkUrlResolver getBrokenLinkUrlResolver() {
        return asyncDeliveryClient.getBrokenLinkUrlResolver();
    }

    /**
     * Sets the {@link BrokenLinkUrlResolver} for this client.
     *
     * @param brokenLinkUrlResolver Sets the BrokenLinkUrlResolver of this client.
     * @see                         BrokenLinkUrlResolver
     */
    public void setBrokenLinkUrlResolver(BrokenLinkUrlResolver brokenLinkUrlResolver) {
        asyncDeliveryClient.setBrokenLinkUrlResolver(brokenLinkUrlResolver);
    }

    public RichTextElementResolver getRichTextElementResolver() {
        return asyncDeliveryClient.getRichTextElementResolver();
    }

    public void setRichTextElementResolver(RichTextElementResolver richTextElementResolver) {
        asyncDeliveryClient.setRichTextElementResolver(richTextElementResolver);
    }

    public void addRichTextElementResolver(RichTextElementResolver richTextElementResolver) {
        asyncDeliveryClient.addRichTextElementResolver(richTextElementResolver);
    }

    public void registerType(String contentType, Class<?> clazz) {
        asyncDeliveryClient.registerType(contentType, clazz);
    }

    public void registerType(Class<?> clazz) {
        asyncDeliveryClient.registerType(clazz);
    }

    public void registerInlineContentItemsResolver(InlineContentItemsResolver resolver) {
        asyncDeliveryClient.registerInlineContentItemsResolver(resolver);
    }

    public void scanClasspathForMappings(String basePackage) {
        asyncDeliveryClient.scanClasspathForMappings(basePackage);
    }

    /**
     * Sets the {@link CacheManager} for this client.
     *
     * @param cacheManager  A {@link CacheManager} implementation for this client to use.
     * @see                 CacheManager
     */
    public void setCacheManager(final CacheManager cacheManager) {
        final AsyncCacheManager bridgedCacheManager = new AsyncCacheManager() {
            @Override
            public Maybe<JsonNode> get(String url) {
                return Maybe.create(emitter -> {
                    try {
                        final JsonNode jsonNode = cacheManager.get(url);
                        if (jsonNode != null) {
                            emitter.onSuccess(jsonNode);
                        }
                        emitter.onComplete();
                    } catch (Exception e) {
                        emitter.onError(e);
                    }
                });
            }

            @Override
            public Completable put(String url, JsonNode jsonNode, List<ContentItem> containedContentItems) {
                return new CompletableFromRunnable(() -> cacheManager.put(url, jsonNode, containedContentItems));
            }
        };
        asyncDeliveryClient.setCacheManager(bridgedCacheManager);
    }

    DeliveryOptions getDeliveryOptions() {
        return asyncDeliveryClient.getDeliveryOptions();
    }

    /**
     * Cleans up the underlying http client resources
     */
    @Override
    public void close() {
        asyncDeliveryClient.close();
    }
}
