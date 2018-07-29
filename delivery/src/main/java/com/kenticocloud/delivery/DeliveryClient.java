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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import com.kenticocloud.delivery.template.TemplateEngineConfig;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Executes requests against the Kentico Cloud Delivery API.
 */
public class DeliveryClient {

    private static final Logger logger = LoggerFactory.getLogger(DeliveryClient.class);

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
            logger.info("SDK ID: {}", sdkId);
        } catch (IOException e) {
            logger.info("Jar manifest read error, setting developer build SDK ID");
            sdkId = "localBuild;com.kenticocloud:delivery;0.0.0";
        }
    }

    static final String ITEMS = "items";
    static final String TYPES = "types";
    static final String ELEMENTS = "elements";
    static final String TAXONOMIES = "taxonomies";

    private static final String URL_CONCAT = "%s/%s";

    private ObjectMapper objectMapper = new ObjectMapper();
    private PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();
    private HttpClient httpClient;
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
     * @throws IllegalArgumentException Thrown if the arguments in the {@link DeliveryOptions} are invalid.
     * @param deliveryOptions The settings of the Kentico Cloud project.
     */
    public DeliveryClient(DeliveryOptions deliveryOptions) {
        this(deliveryOptions, new TemplateEngineConfig());
    }

    /**
     * Initializes a new instance of the {@link DeliveryClient} class for retrieving content of the specified project.
     * @throws IllegalArgumentException Thrown if the arguments in the {@link DeliveryOptions} are invalid.
     * @param deliveryOptions The settings of the Kentico Cloud project.
     * @param templateEngineConfig Configuration object used for customization of template render engines
     *                             for inline content
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
     * @throws IllegalArgumentException Thrown if the Project id is invalid.
     * @param projectId The Project ID associated with your Kentico Cloud account.  Must be in the format of an
     * {@link java.util.UUID}.
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
     * @throws IllegalArgumentException Thrown if the Project id is invalid.
     * @param projectId The Project ID associated with your Kentico Cloud account.  Must be in the format of an
     * {@link java.util.UUID}.
     * @param previewApiKey The Preview API key configured with your Kentico Cloud account.
     */
    public DeliveryClient(String projectId, String previewApiKey) {
        this(new DeliveryOptions(projectId, previewApiKey));
    }

    public ContentItemsListingResponse getItems() throws IOException {
        return getItems(new ArrayList<>());
    }

    public ContentItemsListingResponse getItems(List<NameValuePair> params) throws IOException {
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

    public <T> List<T> getItems(Class<T> tClass, List<NameValuePair> params) throws IOException {
        addTypeParameterIfNecessary(tClass, params);
        ContentItemsListingResponse contentItemsListingResponse = getItems(params);
        return contentItemsListingResponse.castTo(tClass);
    }

    public ContentItemResponse getItem(String contentItemCodename) throws IOException {
        return getItem(contentItemCodename, new ArrayList<>());
    }

    public <T> List<T> getItems(Class<T> tClass) throws IOException {
        return getItems(tClass, new ArrayList<>());
    }

    public <T> Page<T> getPageOfItems(Class<T> tClass, List<NameValuePair> params) throws IOException {
        ContentItemsListingResponse response = getItems(params);
        response.setStronglyTypedContentItemConverter(stronglyTypedContentItemConverter);
        return new Page<>(response, tClass, this);
    }

    public <T> Page<T> getNextPage(Page<T> currentPage) throws IOException {
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

    public <T> T getItem(String contentItemCodename, Class<T> tClass) throws IOException {
        return getItem(contentItemCodename, tClass, new ArrayList<>());
    }

    public ContentItemResponse getItem(String contentItemCodename, List<NameValuePair> params) throws IOException {
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

    public <T> T getItem(String contentItemCodename, Class<T> tClass, List<NameValuePair> params) throws IOException {
        addTypeParameterIfNecessary(tClass, params);
        ContentItemResponse contentItemResponse = getItem(contentItemCodename, params);
        return contentItemResponse.castTo(tClass);
    }

    public ContentTypesListingResponse getTypes() throws IOException {
        return getTypes(new ArrayList<>());
    }

    public ContentTypesListingResponse getTypes(List<NameValuePair> params) throws IOException {
        HttpUriRequest request = buildGetRequest(TYPES, params);
        return executeRequest(request, ContentTypesListingResponse.class);
    }

    public ContentType getType(String contentTypeCodeName) throws IOException {
        return getType(contentTypeCodeName, new ArrayList<>());
    }

    public ContentType getType(String contentTypeCodeName, List<NameValuePair> params) throws IOException {
        HttpUriRequest request = buildGetRequest(String.format(URL_CONCAT, TYPES, contentTypeCodeName), params);
        return executeRequest(request, ContentType.class);
    }

    public Element getContentTypeElement(String contentTypeCodeName, String elementCodeName) throws IOException {
        return getContentTypeElement(contentTypeCodeName, elementCodeName, new ArrayList<>());
    }

    public Element getContentTypeElement(String contentTypeCodeName, String elementCodeName, List<NameValuePair> params)
            throws IOException {
        HttpUriRequest request = buildGetRequest(
                String.format("%s/%s/%s/%s", TYPES, contentTypeCodeName, ELEMENTS, elementCodeName), params);
        return executeRequest(request, Element.class);
    }

    public TaxonomyGroupListingResponse getTaxonomyGroups() throws IOException {
        return getTaxonomyGroups(new ArrayList<>());
    }

    public TaxonomyGroupListingResponse getTaxonomyGroups(List<NameValuePair> params) throws IOException {
        HttpUriRequest request = buildGetRequest(TAXONOMIES, params);
        return executeRequest(request, TaxonomyGroupListingResponse.class);
    }

    public TaxonomyGroup getTaxonomyGroup(String taxonomyGroupCodename) throws IOException {
        return getTaxonomyGroup(taxonomyGroupCodename, new ArrayList<>());
    }

    public TaxonomyGroup getTaxonomyGroup(String taxonomyGroupCodename, List<NameValuePair> params) throws IOException {
        HttpUriRequest request = buildGetRequest(
                String.format(URL_CONCAT, TAXONOMIES, taxonomyGroupCodename), params);
        return executeRequest(request, TaxonomyGroup.class);
    }

    public ContentLinkUrlResolver getContentLinkUrlResolver() {
        return contentLinkUrlResolver;
    }

    public void setContentLinkUrlResolver(ContentLinkUrlResolver contentLinkUrlResolver) {
        this.contentLinkUrlResolver = contentLinkUrlResolver;
    }

    public BrokenLinkUrlResolver getBrokenLinkUrlResolver() {
        return brokenLinkUrlResolver;
    }

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

    public void setCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

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

    private <T> T executeRequest(HttpUriRequest request, Class<T> tClass) throws IOException {
        return executeRequest(request, tClass, 0);
    }

    private <T> T executeRequest(HttpUriRequest request, Class<T> tClass, int attemptNumber) throws IOException {
        try {
            String requestUri = request.getURI().toString();
            logger.info("HTTP {} - {} - {}", request.getMethod(), request.getAllHeaders(), requestUri);
            JsonNode jsonNode = cacheManager.resolveRequest(requestUri, () -> {
                HttpResponse response = httpClient.execute(request);
                handleErrorIfNecessary(response);
                InputStream inputStream = response.getEntity().getContent();
                JsonNode node = objectMapper.readValue(inputStream, JsonNode.class);
                logger.info("{} - {}", response.getStatusLine(), requestUri);
                logger.debug("{} - {}:\n{}", request.getMethod(), requestUri, node);
                inputStream.close();
                return node;
            });
            return objectMapper.treeToValue(jsonNode, tClass);
        } catch (KenticoErrorException kenticoError) {
            throw kenticoError;
        } catch (Exception ex) {
            logger.error("Failed request: {}", ex.getMessage());
            if (attemptNumber < deliveryOptions.getRetryAttempts()) {
                int nextAttemptNumber = attemptNumber + 1;
                //Perform a binary exponential backoff
                int wait = (int) (100 * Math.pow(2, nextAttemptNumber));
                logger.info("Reattempting request after {}ms", wait);
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

    private void handleErrorIfNecessary(HttpResponse response) throws IOException {
        final int status = response.getStatusLine().getStatusCode();
        if (status >= 500) {
            logger.error("Kentico API server error, status: {}", status);
            throw new IOException("Unknown error with Kentico API.  Kentico is likely suffering site issues.");
        } else if (status >= 400) {
            logger.error("Kentico API request error, status: {}", status);
            InputStream inputStream = response.getEntity().getContent();
            KenticoError kenticoError = objectMapper.readValue(inputStream, KenticoError.class);
            inputStream.close();
            throw new KenticoErrorException(kenticoError);
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
        objectMapper = new ObjectMapper();

        SimpleModule module = new SimpleModule();
        objectMapper.registerModule(new JSR310Module());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        objectMapper.registerModule(module);
    }
}
