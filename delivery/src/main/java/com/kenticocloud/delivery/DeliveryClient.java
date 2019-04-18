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

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import com.kenticocloud.delivery.template.TemplateEngineConfig;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicLineFormatter;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Executes requests against the Kentico Cloud Delivery API.
 *
 * @see <a href="https://developer.kenticocloud.com/v1/reference#authentication">
 *      KenticoCloud API reference - Authentication</a>
 * @see <a href="https://developer.kenticocloud.com/v1/reference#delivery-api">
 *      KenticoCloud API reference - Delivery API</a>
 */
@lombok.extern.slf4j.Slf4j
public class DeliveryClient {

    static String sdkId;

    static {
        try {
            Properties buildProps = new Properties();
            buildProps.load(DeliveryClient.class.getResourceAsStream("build.properties"));
            String repositoryHost = buildProps.getProperty("Repository-Host");
            String version = buildProps.getProperty("Implementation-Version");
            String packageId = buildProps.getProperty("Package-Id");
            repositoryHost = repositoryHost == null ? "localBuild" : repositoryHost;
            version = version == null ? "0.0.0" : version;
            packageId = packageId == null ? "com.kenticocloud:delivery" : packageId;
            sdkId = String.format(
                    "%s;%s;%s",
                    repositoryHost,
                    packageId,
                    version);
            log.info("SDK ID: {}", sdkId);
        } catch (IOException e) {
            log.info("Jar manifest read error, setting developer build SDK ID");
            sdkId = "localBuild;com.kenticocloud:delivery;0.0.0";
        }
    }

    static final String ITEMS = "items";
    static final String TYPES = "types";
    static final String ELEMENTS = "elements";
    static final String TAXONOMIES = "taxonomies";

    private static final String URL_CONCAT = "%s/%s";

    private ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    private PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();
    private CloseableHttpClient httpClient;
    private DeliveryOptions deliveryOptions;

    private ContentLinkUrlResolver contentLinkUrlResolver;
    private BrokenLinkUrlResolver brokenLinkUrlResolver;
    private RichTextElementResolver richTextElementResolver = new DelegatingRichTextElementResolver();
    private StronglyTypedContentItemConverter stronglyTypedContentItemConverter =
            new StronglyTypedContentItemConverter();
    private TemplateEngineConfig templateEngineConfig;

    private CacheManager cacheManager = (requestUri, executor) -> executor.execute();

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
        if (deliveryOptions == null) {
            throw new IllegalArgumentException("The Delivery options object is not specified.");
        }
        if (deliveryOptions.getProjectId() == null || deliveryOptions.getProjectId().isEmpty()) {
            throw new IllegalArgumentException("Kentico Cloud project identifier is not specified.");
        }
        try {
            UUID.fromString(deliveryOptions.getProjectId());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    String.format(
                            "Provided string is not a valid project identifier (%s).  Have you accidentally passed " +
                                    "the Preview API key instead of the project identifier?",
                            deliveryOptions.getProjectId()),
                    e);
        }
        if (deliveryOptions.isUsePreviewApi() &&
                (deliveryOptions.getPreviewApiKey() == null || deliveryOptions.getPreviewApiKey().isEmpty())) {
            throw new IllegalArgumentException("The Preview API key is not specified.");
        }
        if (deliveryOptions.isUsePreviewApi() && deliveryOptions.getProductionApiKey() != null){
            throw new IllegalArgumentException("Cannot provide both a preview API key and a production API key.");
        }
        if (deliveryOptions.getRetryAttempts() < 0) {
            throw new IllegalArgumentException("Cannot retry connections less than 0 times.");
        }
        this.deliveryOptions = deliveryOptions;
        connManager.setMaxTotal(20);
        connManager.setDefaultMaxPerRoute(20);
        httpClient = HttpClients.custom().setConnectionManager(connManager).build();
        if (templateEngineConfig != null) {
            templateEngineConfig.init();
            this.templateEngineConfig = templateEngineConfig;
        }
        reconfigureDeserializer();
    }

    /**
     * Initializes a new instance of the {@link DeliveryClient} class for retrieving content of the specified project.
     *
     * @param projectId The Project ID associated with your Kentico Cloud account.  Must be in the format of an
     *                  {@link java.util.UUID}.
     * @throws          IllegalArgumentException Thrown if the Project id is invalid.
     */
    public DeliveryClient(String projectId) {
        this(new DeliveryOptions(projectId));
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
        this(new DeliveryOptions(projectId, previewApiKey));
    }

    /**
     * Returns a {@link ContentItemsListingResponse} for all items in the project.  Beware, this is an incredibly
     * expensive operation on a big project, you were forewarned.
     *
     * @return  A {@link ContentItemsListingResponse} for all items in the project.
     * @throws  KenticoIOException If an {@link IOException} is thrown interacting with KenticoCloud.
     * @see     ContentItem
     * @see     ContentItemsListingResponse
     * @see     <a href="https://developer.kenticocloud.com/v1/reference#list-content-items">
     *          KenticoCloud API reference - List content items</a>
     */
    public ContentItemsListingResponse getItems() {
        return getItems(new ArrayList<>());
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
     * @see             <a href="https://developer.kenticocloud.com/v1/reference#listing-response">
     *                  KenticoCloud API reference - Listing response</a>
     * @see             <a href="https://developer.kenticocloud.com/v1/reference#list-content-items">
     *                  KenticoCloud API reference - List content items</a>
     */
    public ContentItemsListingResponse getItems(List<NameValuePair> params) {
        HttpUriRequest request = buildGetRequest(ITEMS, params);
        ContentItemsListingResponse contentItemsListingResponse =
                executeRequest(request, ContentItemsListingResponse.class);
        contentItemsListingResponse.setStronglyTypedContentItemConverter(stronglyTypedContentItemConverter);
        RichTextElementConverter converter = new RichTextElementConverter(
                getContentLinkUrlResolver(),
                getBrokenLinkUrlResolver(),
                getRichTextElementResolver(),
                templateEngineConfig,
                stronglyTypedContentItemConverter
        );
        converter.process(contentItemsListingResponse.getItems());
        return contentItemsListingResponse;
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
        addTypeParameterIfNecessary(tClass, params);
        ContentItemsListingResponse contentItemsListingResponse = getItems(params);
        return contentItemsListingResponse.castTo(tClass);
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
     * @see                         <a href="https://developer.kenticocloud.com/v1/reference#view-a-content-item">
     *                              KenticoCloud API reference - View a content item</a>
     */
    public ContentItemResponse getItem(String contentItemCodename) {
        return getItem(contentItemCodename, new ArrayList<>());
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
        return getItems(tClass, new ArrayList<>());
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
     * @see             <a href="https://developer.kenticocloud.com/v1/reference#listing-response-paging">
     *                  KenticoCloud API reference - Listing response paging</a>
     */
    public <T> Page<T> getPageOfItems(Class<T> tClass, List<NameValuePair> params) {
        ContentItemsListingResponse response = getItems(params);
        response.setStronglyTypedContentItemConverter(stronglyTypedContentItemConverter);
        return new Page<>(response, tClass, this);
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
     * @see                 <a href="https://developer.kenticocloud.com/v1/reference#listing-response-paging">
     *                      KenticoCloud API reference - Listing response paging</a>
     */
    public <T> Page<T> getNextPage(Page<T> currentPage) {
        Pagination pagination = currentPage.getPagination();
        if (pagination.getNextPage() == null || pagination.getNextPage().isEmpty()) {
            return null;
        }
        RequestBuilder requestBuilder = RequestBuilder.get(pagination.getNextPage());
        requestBuilder = addHeaders(requestBuilder);
        HttpUriRequest httpUriRequest = requestBuilder.build();
        ContentItemsListingResponse response = executeRequest(httpUriRequest, ContentItemsListingResponse.class);
        response.setStronglyTypedContentItemConverter(stronglyTypedContentItemConverter);
        RichTextElementConverter converter = new RichTextElementConverter(
                getContentLinkUrlResolver(),
                getBrokenLinkUrlResolver(),
                getRichTextElementResolver(),
                templateEngineConfig,
                stronglyTypedContentItemConverter
        );
        converter.process(response.getItems());
        return new Page<>(response, currentPage.getType(), this);
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
     * @see                         <a href="https://developer.kenticocloud.com/v1/reference#view-a-content-item">
     *                              KenticoCloud API reference - View a content item</a>
     */
    public <T> T getItem(String contentItemCodename, Class<T> tClass) {
        return getItem(contentItemCodename, tClass, new ArrayList<>());
    }

    /**
     * Returns a {@link ContentItemResponse} for the {@link ContentItem} in the project with the given
     * {@link System#codename}.  Query parameters can be provided, which can be used to change modular content
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
     * @see                         <a href="https://developer.kenticocloud.com/v1/reference#view-a-content-item">
     *                              KenticoCloud API reference - View a content item</a>
     * @see                         <a href="https://developer.kenticocloud.com/v1/reference#listing-response">
     *                              KenticoCloud API reference - Listing response</a>
     */
    public ContentItemResponse getItem(String contentItemCodename, List<NameValuePair> params) {
        HttpUriRequest request = buildGetRequest(String.format(URL_CONCAT, ITEMS, contentItemCodename), params);
        ContentItemResponse contentItemResponse = executeRequest(request, ContentItemResponse.class);
        contentItemResponse.setStronglyTypedContentItemConverter(stronglyTypedContentItemConverter);
        RichTextElementConverter converter = new RichTextElementConverter(
                getContentLinkUrlResolver(),
                getBrokenLinkUrlResolver(),
                getRichTextElementResolver(),
                templateEngineConfig,
                stronglyTypedContentItemConverter
        );
        converter.process(contentItemResponse.getItem());
        return contentItemResponse;
    }

    /**
     * Returns new instance of T by mapping fields to elements from a {@link ContentItem} in the project with the given
     * {@link System#codename}.  Query parameters can be provided, which can be used to change modular content
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
     * @see                         <a href="https://developer.kenticocloud.com/v1/reference#view-a-content-item">
     *                              KenticoCloud API reference - View a content item</a>
     */
    public <T> T getItem(String contentItemCodename, Class<T> tClass, List<NameValuePair> params) {
        addTypeParameterIfNecessary(tClass, params);
        ContentItemResponse contentItemResponse = getItem(contentItemCodename, params);
        return contentItemResponse.castTo(tClass);
    }

    /**
     * Returns a {@link ContentTypesListingResponse} detailing all {@link ContentType}s in the project.
     *
     * @return  A ContentTypesListingResponse containing all {@link ContentType}s in the project.
     * @throws  KenticoIOException If an {@link IOException} is thrown interacting with KenticoCloud.
     * @see     ContentType
     * @see     DeliveryClient#getTypes()
     * @see     DeliveryClient#getTypes(List)
     * @see     <a href="https://developer.kenticocloud.com/v1/reference#list-content-types">
     *          KenticoCloud API reference - List content types</a>
     * @see     <a href="https://developer.kenticocloud.com/v1/reference#content-type-object">
     *          KenticoCloud API reference - Content type object</a>
     */
    public ContentTypesListingResponse getTypes() {
        return getTypes(new ArrayList<>());
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
     * @see             <a href="https://developer.kenticocloud.com/v1/reference#list-content-types">
     *                  KenticoCloud API reference - List content types</a>
     * @see             <a href="https://developer.kenticocloud.com/v1/reference#content-type-object">
     *                  KenticoCloud API reference - Content type object</a>
     */
    public ContentTypesListingResponse getTypes(List<NameValuePair> params) {
        HttpUriRequest request = buildGetRequest(TYPES, params);
        return executeRequest(request, ContentTypesListingResponse.class);
    }

    /**
     * Returns the {@link ContentType} with a {@link System#codename}.
     *
     * @param contentTypeCodeName   The {@link System#codename} for the ContentType request.
     * @return                      The ContentType matching {@link System#codename}.
     * @throws                      KenticoIOException If an {@link IOException} is thrown interacting with
     *                              KenticoCloud.
     * @see                         ContentType
     * @see                         <a href="https://developer.kenticocloud.com/v1/reference#view-a-content-type">
     *                              KenticoCloud API reference - View a content type</a>
     * @see                         <a href="https://developer.kenticocloud.com/v1/reference#content-type-object">
     *                              KenticoCloud API reference - Content type object</a>
     */
    public ContentType getType(String contentTypeCodeName) {
        HttpUriRequest request =
                buildGetRequest(String.format(URL_CONCAT, TYPES, contentTypeCodeName), new ArrayList<>());
        return executeRequest(request, ContentType.class);
    }

    /**
     * Retrieve a specific {@link ContentType} {@link Element} by specifying its codename and its parent content type.
     *
     * @param contentTypeCodeName   The {@link System#codename} for the ContentType request.
     * @param elementCodeName       The codename for the Element.
     * @return                      The Element for the ContentType requested.
     * @see                         <a
     *                              href="https://developer.kenticocloud.com/v1/reference#view-a-content-type-element">
     *                              KenticoCloud API reference - View a content type element</a>
     * @see                         <a
     *                              href="https://developer.kenticocloud.com/v1/reference#content-type-element-object">
     *                              KenticoCloud API reference - Content element model</a>
     */
    public Element getContentTypeElement(String contentTypeCodeName, String elementCodeName) {
        return getContentTypeElement(contentTypeCodeName, elementCodeName, new ArrayList<>());
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
     *                              href="https://developer.kenticocloud.com/v1/reference#view-a-content-type-element">
     *                              KenticoCloud API reference - View a content type element</a>
     * @see                         <a
     *                              href="https://developer.kenticocloud.com/v1/reference#content-type-element-object">
     *                              KenticoCloud API reference - Content element model</a>
     */
    public Element getContentTypeElement(
            String contentTypeCodeName, String elementCodeName, List<NameValuePair> params) {
        HttpUriRequest request = buildGetRequest(
                String.format("%s/%s/%s/%s", TYPES, contentTypeCodeName, ELEMENTS, elementCodeName), params);
        return executeRequest(request, Element.class);
    }

    /**
     * Retrieve all {@link TaxonomyGroup} in your project.  Returns them ordered alphabetically by codename.
     *
     * @return  A response object containing all {@link TaxonomyGroup} in the project.
     * @see     TaxonomyGroup
     * @see     Taxonomy
     * @see     <a href="https://developer.kenticocloud.com/v1/reference#list-taxonomy-groups">
     *          KenticoCloud API reference - List taxonomy groups</a>
     * @see     <a href="https://developer.kenticocloud.com/v1/reference#taxonomy-group-object">
     *          KenticoCloud API reference - Taxonomy group model</a>
     */
    public TaxonomyGroupListingResponse getTaxonomyGroups() {
        return getTaxonomyGroups(new ArrayList<>());
    }

    /**
     * Retrieve a page of {@link TaxonomyGroup} in your project.
     * Use {@link DeliveryParameterBuilder#page(Integer, Integer)}.
     *
     * @param params    Built from {@link DeliveryParameterBuilder#page(Integer, Integer)}
     * @return          A response object containing all {@link TaxonomyGroup} in the project.
     * @see             TaxonomyGroup
     * @see             Taxonomy
     * @see             <a href="https://developer.kenticocloud.com/v1/reference#list-taxonomy-groups">
     *                  KenticoCloud API reference - List taxonomy groups</a>
     * @see             <a href="https://developer.kenticocloud.com/v1/reference#taxonomy-group-object">
     *                  KenticoCloud API reference - Taxonomy group model</a>
     */
    public TaxonomyGroupListingResponse getTaxonomyGroups(List<NameValuePair> params) {
        HttpUriRequest request = buildGetRequest(TAXONOMIES, params);
        return executeRequest(request, TaxonomyGroupListingResponse.class);
    }

    /**
     * Retrieve a specific {@link TaxonomyGroup} from your project by specifying its codename.
     *
     * @param taxonomyGroupCodename The codename of a specfic taxonomy group.
     * @return                      The {@link TaxonomyGroup}.
     * @see                         TaxonomyGroup
     * @see                         Taxonomy
     * @see                         <a href="https://developer.kenticocloud.com/v1/reference#view-a-taxonomy-group">
     *                              KenticoCloud API reference - View a taxonomy group</a>
     * @see                         <a href="https://developer.kenticocloud.com/v1/reference#taxonomy-group-object">
     *                              KenticoCloud API reference - Taxonomy group model</a>
     */
    public TaxonomyGroup getTaxonomyGroup(String taxonomyGroupCodename) {
        return getTaxonomyGroup(taxonomyGroupCodename, new ArrayList<>());
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
     * @see                         <a href="https://developer.kenticocloud.com/v1/reference#view-a-taxonomy-group">
     *                              KenticoCloud API reference - View a taxonomy group</a>
     * @see                         <a href="https://developer.kenticocloud.com/v1/reference#taxonomy-group-object">
     *                              KenticoCloud API reference - Taxonomy group model</a>
     */
    public TaxonomyGroup getTaxonomyGroup(String taxonomyGroupCodename, List<NameValuePair> params) {
        HttpUriRequest request = buildGetRequest(
                String.format(URL_CONCAT, TAXONOMIES, taxonomyGroupCodename), params);
        return executeRequest(request, TaxonomyGroup.class);
    }

    /**
     * Retrieve the currently configured {@link ContentLinkUrlResolver} for this client.
     *
     * @return  This client's {@link ContentLinkUrlResolver}
     * @see     ContentLinkUrlResolver
     */
    public ContentLinkUrlResolver getContentLinkUrlResolver() {
        return contentLinkUrlResolver;
    }

    /**
     * Sets the {@link ContentLinkUrlResolver} for this client.
     *
     * @param contentLinkUrlResolver    Sets the ContentLinkResolver of this client.
     * @see                             ContentLinkUrlResolver
     */
    public void setContentLinkUrlResolver(ContentLinkUrlResolver contentLinkUrlResolver) {
        this.contentLinkUrlResolver = contentLinkUrlResolver;
    }

    /**
     * Retrieve the currently configured {@link BrokenLinkUrlResolver} for this client.
     *
     * @return  This client's {@link BrokenLinkUrlResolver}
     * @see     BrokenLinkUrlResolver
     */
    public BrokenLinkUrlResolver getBrokenLinkUrlResolver() {
        return brokenLinkUrlResolver;
    }

    /**
     * Sets the {@link BrokenLinkUrlResolver} for this client.
     *
     * @param brokenLinkUrlResolver Sets the BrokenLinkUrlResolver of this client.
     * @see                         BrokenLinkUrlResolver
     */
    public void setBrokenLinkUrlResolver(BrokenLinkUrlResolver brokenLinkUrlResolver) {
        this.brokenLinkUrlResolver = brokenLinkUrlResolver;
    }

    public RichTextElementResolver getRichTextElementResolver() {
        return richTextElementResolver;
    }

    public void setRichTextElementResolver(RichTextElementResolver richTextElementResolver) {
        this.richTextElementResolver = richTextElementResolver;
    }

    public void addRichTextElementResolver(RichTextElementResolver richTextElementResolver) {
        if (this.richTextElementResolver instanceof DelegatingRichTextElementResolver) {
            ((DelegatingRichTextElementResolver) this.richTextElementResolver).addResolver(richTextElementResolver);
        } else if (this.richTextElementResolver == null) {
            setRichTextElementResolver(richTextElementResolver);
        } else {
            DelegatingRichTextElementResolver delegatingResolver = new DelegatingRichTextElementResolver();
            delegatingResolver.addResolver(this.richTextElementResolver);
            delegatingResolver.addResolver(richTextElementResolver);
            setRichTextElementResolver(delegatingResolver);
        }
    }

    public void registerType(String contentType, Class<?> clazz) {
        stronglyTypedContentItemConverter.registerType(contentType, clazz);
    }

    public void registerType(Class<?> clazz) {
        stronglyTypedContentItemConverter.registerType(clazz);
    }

    public void registerInlineContentItemsResolver(InlineContentItemsResolver resolver) {
        stronglyTypedContentItemConverter.registerInlineContentItemsResolver(resolver);
    }

    public void scanClasspathForMappings(String basePackage) {
        stronglyTypedContentItemConverter.scanClasspathForMappings(basePackage);
    }

    /**
     * Sets the {@link CacheManager} for this client.
     *
     * @param cacheManager  A {@link CacheManager} implementation for this client to use.
     * @see                 CacheManager
     */
    public void setCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    /**
     * Sets the maximum number of connections in the client pool to the KenticoCloud API.  Defaults to 20.
     *
     * @param maxConnections The number of connections to the KenticoCloud API the underlying HTTP connection pool uses
     *                       in this client.
     */
    public void setMaxConnections(int maxConnections) {
        connManager.setDefaultMaxPerRoute(maxConnections);
    }

    protected HttpUriRequest buildGetRequest(String apiCall, List<NameValuePair> nameValuePairs) {
        RequestBuilder requestBuilder = RequestBuilder.get(String.format(URL_CONCAT, getBaseUrl(), apiCall));
        requestBuilder = addHeaders(requestBuilder);
        for (NameValuePair nameValuePair : nameValuePairs) {
            requestBuilder.addParameter(nameValuePair);
        }
        return requestBuilder.build();
    }

    protected RequestBuilder addHeaders(RequestBuilder requestBuilder) {
        if (deliveryOptions.getProductionApiKey() != null) {
            requestBuilder.setHeader(
                    HttpHeaders.AUTHORIZATION, String.format("Bearer %s", deliveryOptions.getProductionApiKey())
            );
        } else if (deliveryOptions.isUsePreviewApi()) {
            requestBuilder.setHeader(
                    HttpHeaders.AUTHORIZATION, String.format("Bearer %s", deliveryOptions.getPreviewApiKey())
            );
        }
        if (deliveryOptions.isWaitForLoadingNewContent()) {
            requestBuilder.setHeader(
                    "X-KC-Wait-For-Loading-New-Content", "true"
            );
        }
        requestBuilder.setHeader(HttpHeaders.ACCEPT, "application/json");
        requestBuilder.setHeader("X-KC-SDKID", sdkId);
        return requestBuilder;
    }

    private String getBaseUrl() {
        if (deliveryOptions.isUsePreviewApi()) {
            return String.format(deliveryOptions.getPreviewEndpoint(), deliveryOptions.getProjectId());
        } else {
            return String.format(deliveryOptions.getProductionEndpoint(), deliveryOptions.getProjectId());
        }
    }

    private <T> T executeRequest(HttpUriRequest request, Class<T> tClass) {
        try {
            return executeRequest(request, tClass, 0);
        } catch (IOException e) {
            log.error("IOException connecting to Kentico: {}", e.toString());
            throw new KenticoIOException(e);
        }
    }

    private <T> T executeRequest(HttpUriRequest request, Class<T> tClass, int attemptNumber) throws IOException {
        try {
            String requestUri = request.getURI().toString();
            log.debug("HTTP {} - {}", request.getMethod(), requestUri);
            JsonNode jsonNode = cacheManager.resolveRequest(requestUri, () -> {
                try (CloseableHttpResponse response = httpClient.execute(request)) {
                    handleErrorIfNecessary(response);
                    InputStream inputStream = response.getEntity().getContent();
                    JsonNode node = objectMapper.readValue(inputStream, JsonNode.class);
                    log.info("{} - {}",
                            BasicLineFormatter.INSTANCE.formatStatusLine(null, response.getStatusLine())
                                    .toString(),
                            requestUri);
                    log.debug("{}:\n{}",
                            String.format("%s - %s", request.getMethod(), requestUri),
                            node.toString());
                    inputStream.close();
                    return node;
                }
            });
            return objectMapper.treeToValue(jsonNode, tClass);
        } catch (KenticoErrorException kenticoError) {
            throw kenticoError;
        } catch (Exception ex) {
            log.error("Failed request: {}", ex.getMessage());
            if (attemptNumber < deliveryOptions.getRetryAttempts()) {
                int nextAttemptNumber = attemptNumber + 1;
                //Perform a binary exponential backoff
                int wait = (int) (100 * Math.pow(2, nextAttemptNumber));
                log.info("Reattempting request after {}ms", wait);
                try {
                    Thread.sleep(wait);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                return executeRequest(request, tClass, nextAttemptNumber);
            } else {
                throw ex;
            }
        }
    }

    private void handleErrorIfNecessary(HttpResponse response) {
        StatusLine statusLine = response.getStatusLine();
        final int status = statusLine.getStatusCode();
        if (status >= 500) {
            String statusString = BasicLineFormatter.INSTANCE.formatStatusLine(null, statusLine).toString();
            log.error("Kentico API server error, status: {}", statusString);
            String message =
                    String.format(
                            "Unknown error with Kentico API.  Kentico is likely suffering site issues.  Status: %s",
                            statusString);
            throw new KenticoIOException(message);
        } else if (status >= 400) {
            String statusString = BasicLineFormatter.INSTANCE.formatStatusLine(null, statusLine).toString();
            log.error("Kentico API server error, status: {}", statusString);
            try (InputStream inputStream = response.getEntity().getContent()) {
                KenticoError kenticoError = objectMapper.readValue(inputStream, KenticoError.class);
                throw new KenticoErrorException(kenticoError);
            } catch (IOException e) {
                log.error("IOException connecting to Kentico: {}", e.toString());
                throw new KenticoIOException(e);
            }
        }
    }

    private void addTypeParameterIfNecessary(Class tClass, List<NameValuePair> params) {
        Optional<NameValuePair> any = params.stream()
                .filter(nameValuePair -> nameValuePair.getName().equals("system.type"))
                .findAny();
        if (!any.isPresent()) {
            String contentType = stronglyTypedContentItemConverter.getContentType(tClass);
            if (contentType != null) {
                params.add(new BasicNameValuePair("system.type", contentType));
            }
        }
    }

    private void reconfigureDeserializer() {
        objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        SimpleModule module = new SimpleModule();
        objectMapper.registerModule(new JSR310Module());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        objectMapper.registerModule(module);
    }
}
