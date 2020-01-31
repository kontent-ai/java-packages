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

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import kentico.kontent.delivery.template.TemplateEngineConfig;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.MaybeSource;
import lombok.extern.slf4j.Slf4j;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.Request;
import org.asynchttpclient.RequestBuilder;
import org.asynchttpclient.Response;
import org.asynchttpclient.extras.rxjava2.DefaultRxHttpClient;
import org.asynchttpclient.extras.rxjava2.RxHttpClient;
import org.asynchttpclient.proxy.ProxyServer;
import org.reactivestreams.Publisher;

import java.io.Closeable;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.asynchttpclient.Dsl.*;

// TODO: add JavaDoc

// TODO: documentation about closing the client

@Slf4j
public class AsyncDeliveryClient implements Closeable {

    private static final String HEADER_X_KC_WAIT_FOR_LOADING_NEW_CONTENT = "X-KC-Wait-For-Loading-New-Content";
    private static String sdkId;

    static {
        try {
            Properties buildProps = new Properties();
            buildProps.load(AsyncDeliveryClient.class.getResourceAsStream("build.properties"));
            String repositoryHost = buildProps.getProperty("Repository-Host");
            String version = buildProps.getProperty("Implementation-Version");
            String packageId = buildProps.getProperty("Package-Id");
            repositoryHost = repositoryHost == null ? "localBuild" : repositoryHost;
            version = version == null ? "0.0.0" : version;
            packageId = packageId == null ? "kentico.kontent:delivery" : packageId;
            sdkId = String.format(
                    "%s;%s;%s",
                    repositoryHost,
                    packageId,
                    version);
            log.info("SDK ID: {}", sdkId);
        } catch (IOException e) {
            log.info("Jar manifest read error, setting developer build SDK ID");
            sdkId = "localBuild;kentico.kontent:delivery;0.0.0";
        }
    }

    private static final String ITEMS = "items";
    private static final String TYPES = "types";
    private static final String ELEMENTS = "elements";
    private static final String TAXONOMIES = "taxonomies";

    private static final String URL_CONCAT = "%s/%s";

    private ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    private DeliveryOptions deliveryOptions;

    private ContentLinkUrlResolver contentLinkUrlResolver;
    private BrokenLinkUrlResolver brokenLinkUrlResolver;
    private RichTextElementResolver richTextElementResolver = new DelegatingRichTextElementResolver();
    private StronglyTypedContentItemConverter stronglyTypedContentItemConverter =
            new StronglyTypedContentItemConverter();
    private TemplateEngineConfig templateEngineConfig;

    private ProxyServer proxyServer;
    private AsyncHttpClient asyncHttpClient;
    private RxHttpClient rxHttpClient;

    private AsyncCacheManager cacheManager = new AsyncCacheManager() {
        @Override
        public Maybe<JsonNode> get(String url) {
            return Maybe.empty();
        }

        @Override
        public Completable put(String url, JsonNode jsonNode, List<ContentItem> containedContentItems) {
            return Completable.complete();
        }
    };

    @SuppressWarnings("WeakerAccess")
    public AsyncDeliveryClient(DeliveryOptions deliveryOptions) {
        this(deliveryOptions, new TemplateEngineConfig());
    }

    @SuppressWarnings({"ResultOfMethodCallIgnored", "WeakerAccess"})
    public AsyncDeliveryClient(DeliveryOptions deliveryOptions, TemplateEngineConfig templateEngineConfig) {
        if (deliveryOptions == null) {
            throw new IllegalArgumentException("The Delivery options object is not specified.");
        }
        if (deliveryOptions.getProjectId() == null || deliveryOptions.getProjectId().isEmpty()) {
            throw new IllegalArgumentException("Kentico Kontent project identifier is not specified.");
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
        if (deliveryOptions.isUsePreviewApi() && deliveryOptions.getProductionApiKey() != null) {
            throw new IllegalArgumentException("Cannot provide both a preview API key and a production API key.");
        }
        if (deliveryOptions.getRetryAttempts() < 0) {
            throw new IllegalArgumentException("Cannot retry connections less than 0 times.");
        }
        this.deliveryOptions = deliveryOptions;

        if (templateEngineConfig != null) {
            templateEngineConfig.init();
            this.templateEngineConfig = templateEngineConfig;
        }
        reconfigureDeserializer();

        if (deliveryOptions.getProxyServer() != null) {
            this.proxyServer = deliveryOptions.getProxyServer();
        }

        // TODO: max connections (20)
        asyncHttpClient = asyncHttpClient(config());
        rxHttpClient = new DefaultRxHttpClient(asyncHttpClient);
    }

    @SuppressWarnings("unused")
    public AsyncDeliveryClient(String projectId) {
        this(new DeliveryOptions(projectId));
    }

    @SuppressWarnings("unused")
    public AsyncDeliveryClient(String projectId, String previewApiKey) {
        this(new DeliveryOptions(projectId, previewApiKey));
    }

    public Maybe<ContentItemsListingResponse> getItems() {
        return getItems(Collections.emptyList());
    }

    @SuppressWarnings("WeakerAccess")
    public Maybe<ContentItemsListingResponse> getItems(List<NameValuePair> params) {
        return executeRequest(ITEMS, params, ContentItemsListingResponse.class)
                .map(contentItemsListingResponse ->
                        contentItemsListingResponse
                                .setStronglyTypedContentItemConverter(stronglyTypedContentItemConverter))
                .map(response -> {
                    createRichTextElementConverter().process(response.items);
                    return response;
                });
    }

    @SuppressWarnings("WeakerAccess")
    public <T> Maybe<List<T>> getItems(Class<T> tClass, List<NameValuePair> params) {
        return getItems(addTypeParameterIfNecessary(tClass, params))
                .map(contentItemsListingResponse -> contentItemsListingResponse.castTo(tClass));
    }

    @SuppressWarnings("unused")
    public Maybe<ContentItemResponse> getItem(String contentItemCodename) {
        return getItem(contentItemCodename, Collections.emptyList());
    }

    @SuppressWarnings("unused")
    public <T> Maybe<List<T>> getItems(Class<T> tClass) {
        return getItems(tClass, Collections.emptyList());
    }

    @SuppressWarnings("WeakerAccess")
    public <T> Maybe<Page<T>> getPageOfItems(Class<T> tClass, List<NameValuePair> params) {
        return getItems(params)
                .map(response ->
                        response.setStronglyTypedContentItemConverter(stronglyTypedContentItemConverter))
                .map(response -> new Page<>(response, tClass));
    }

    @SuppressWarnings("WeakerAccess")
    public <T> Maybe<Page<T>> getNextPage(Page<T> currentPage) {
        final Pagination pagination = currentPage.getPagination();
        if (pagination.getNextPage() == null || pagination.getNextPage().isEmpty()) {
            return Maybe.empty();
        }

        return executeRequest(pagination.getNextPage(), ContentItemsListingResponse.class)
                .map(response -> response.setStronglyTypedContentItemConverter(stronglyTypedContentItemConverter))
                .map(response -> {
                    createRichTextElementConverter().process(response.items);
                    return response;
                })
                .map(response -> new Page<>(response, currentPage.getType()));
    }

    @SuppressWarnings("unused")
    public <T> Maybe<T> getItem(String contentItemCodename, Class<T> tClass) {
        return getItem(contentItemCodename, tClass, Collections.emptyList());
    }

    @SuppressWarnings("WeakerAccess")
    public Maybe<ContentItemResponse> getItem(String contentItemCodename, List<NameValuePair> params) {
        final String apiCall = String.format(URL_CONCAT, ITEMS, contentItemCodename);
        return executeRequest(apiCall, params, ContentItemResponse.class)
                .map(response ->
                        response.setStronglyTypedContentItemConverter(stronglyTypedContentItemConverter))
                .map(response -> {
                    createRichTextElementConverter().process(response.item);
                    return response;
                });
    }

    @SuppressWarnings("WeakerAccess")
    public <T> Maybe<T> getItem(String contentItemCodename, Class<T> tClass, List<NameValuePair> params) {
        return getItem(contentItemCodename, addTypeParameterIfNecessary(tClass, params))
                .map(contentItemResponse -> contentItemResponse.castTo(tClass));
    }

    public Maybe<ContentTypesListingResponse> getTypes() {
        return getTypes(Collections.emptyList());
    }

    @SuppressWarnings("WeakerAccess")
    public Maybe<ContentTypesListingResponse> getTypes(List<NameValuePair> params) {
        return executeRequest(TYPES, params, ContentTypesListingResponse.class);
    }

    public Maybe<ContentType> getType(String contentTypeCodeName) {
        final String apiCall = String.format(URL_CONCAT, TYPES, contentTypeCodeName);
        return executeRequest(apiCall, Collections.emptyList(), ContentType.class);
    }

    @SuppressWarnings("unused")
    public Maybe<Element> getContentTypeElement(String contentTypeCodeName, String elementCodeName) {
        return getContentTypeElement(contentTypeCodeName, elementCodeName, Collections.emptyList());
    }

    @SuppressWarnings("WeakerAccess")
    public Maybe<Element> getContentTypeElement(
            String contentTypeCodeName, String elementCodeName, List<NameValuePair> params) {
        final String apiCall = String.format("%s/%s/%s/%s", TYPES, contentTypeCodeName, ELEMENTS, elementCodeName);
        return executeRequest(apiCall, params, Element.class);
    }

    @SuppressWarnings("unused")
    public Maybe<TaxonomyGroupListingResponse> getTaxonomyGroups() {
        return getTaxonomyGroups(Collections.emptyList());
    }

    @SuppressWarnings("WeakerAccess")
    public Maybe<TaxonomyGroupListingResponse> getTaxonomyGroups(List<NameValuePair> params) {
        return executeRequest(TAXONOMIES, params, TaxonomyGroupListingResponse.class);
    }

    @SuppressWarnings("unused")
    public Maybe<TaxonomyGroup> getTaxonomyGroup(String taxonomyGroupCodename) {
        return getTaxonomyGroup(taxonomyGroupCodename, Collections.emptyList());
    }

    @SuppressWarnings("WeakerAccess")
    public Maybe<TaxonomyGroup> getTaxonomyGroup(String taxonomyGroupCodename, List<NameValuePair> params) {
        final String apiCall = String.format(URL_CONCAT, TAXONOMIES, taxonomyGroupCodename);
        return executeRequest(apiCall, params, TaxonomyGroup.class);
    }

    @SuppressWarnings("WeakerAccess")
    public ContentLinkUrlResolver getContentLinkUrlResolver() {
        return contentLinkUrlResolver;
    }

    @SuppressWarnings("WeakerAccess")
    public void setContentLinkUrlResolver(ContentLinkUrlResolver contentLinkUrlResolver) {
        this.contentLinkUrlResolver = contentLinkUrlResolver;
    }

    @SuppressWarnings("WeakerAccess")
    public BrokenLinkUrlResolver getBrokenLinkUrlResolver() {
        return brokenLinkUrlResolver;
    }

    @SuppressWarnings("WeakerAccess")
    public void setBrokenLinkUrlResolver(BrokenLinkUrlResolver brokenLinkUrlResolver) {
        this.brokenLinkUrlResolver = brokenLinkUrlResolver;
    }

    @SuppressWarnings("WeakerAccess")
    public RichTextElementResolver getRichTextElementResolver() {
        return richTextElementResolver;
    }

    @SuppressWarnings("WeakerAccess")
    public void setRichTextElementResolver(RichTextElementResolver richTextElementResolver) {
        this.richTextElementResolver = richTextElementResolver;
    }

    @SuppressWarnings("WeakerAccess")
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

    @SuppressWarnings("WeakerAccess")
    public void registerType(String contentType, Class<?> clazz) {
        stronglyTypedContentItemConverter.registerType(contentType, clazz);
    }

    @SuppressWarnings("WeakerAccess")
    public void registerType(Class<?> clazz) {
        stronglyTypedContentItemConverter.registerType(clazz);
    }

    @SuppressWarnings("WeakerAccess")
    public void registerInlineContentItemsResolver(InlineContentItemsResolver resolver) {
        stronglyTypedContentItemConverter.registerInlineContentItemsResolver(resolver);
    }

    @SuppressWarnings("WeakerAccess")
    public void scanClasspathForMappings(String basePackage) {
        stronglyTypedContentItemConverter.scanClasspathForMappings(basePackage);
    }

    @SuppressWarnings("WeakerAccess")
    public void setCacheManager(AsyncCacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    private <T> Maybe<T> executeRequest(final String apiCall, final List<NameValuePair> queryParams, Class<T> tClass) {
        return executeRequest(createUrl(apiCall, queryParams), tClass);
    }

    private <T> Maybe<T> executeRequest(final String url, Class<T> tClass) {
        final Request request = buildNewRequest(url);

        final boolean skipCache = Optional.ofNullable(request.getHeaders().get(HEADER_X_KC_WAIT_FOR_LOADING_NEW_CONTENT))
                .map(Boolean::valueOf)
                .orElse(false);

        final Maybe<T> retrieveFromCache =
                cacheManager.get(url).map(jsonNode -> objectMapper.treeToValue(jsonNode, tClass));

        final Maybe<T> retrieveFromKentico = rxHttpClient.prepare(request)
                .map(this::logResponseInfo)
                .map(this::handleErrorIfNecessary)
                .map(Response::getResponseBody)
                .map(this::bodyToJsonNode)
                .flatMap(jsonNode -> putInCache(url, tClass, jsonNode))
                .retryWhen(this::configureErrorAndRetryFlow);

        if (skipCache) {
            return retrieveFromKentico;
        } else {
            return retrieveFromCache.switchIfEmpty(retrieveFromKentico);
        }
    }

    private Request buildNewRequest(String url) {
        RequestBuilder requestBuilder = get(url);
        if (this.proxyServer != null) {
            requestBuilder.setProxyServer(this.proxyServer);
        }
        final Request request = requestBuilder.build();

        request.getHeaders()
                .add("Accept", "application/json")
                .add("X-KC-SDKID", sdkId);

        if (deliveryOptions.getProductionApiKey() != null) {
            request.getHeaders()
                    .add("Authorization", String.format("Bearer %s", deliveryOptions.getProductionApiKey()));
        } else if (deliveryOptions.isUsePreviewApi()) {
            request.getHeaders()
                    .add("Authorization", String.format("Bearer %s", deliveryOptions.getPreviewApiKey()));
        }
        if (deliveryOptions.isWaitForLoadingNewContent()) {
            request.getHeaders()
                    .add(HEADER_X_KC_WAIT_FOR_LOADING_NEW_CONTENT, "true");
        }
        return request;
    }

    private String createUrl(final String apiCall, final List<NameValuePair> queryParams) {

        final String queryStr = Optional.ofNullable(queryParams)
                .filter(params -> !params.isEmpty())
                .map(params -> params.stream()
                        .map(pair -> String.format("%s=%s", pair.getName(), pair.getValue()))
                        .collect(Collectors.joining("&")))
                .map("?"::concat)
                .orElse("");

        final String endpoint = deliveryOptions.isUsePreviewApi() ?
                deliveryOptions.getPreviewEndpoint() : deliveryOptions.getProductionEndpoint();

        return String.format("%s/%s/%s%s", endpoint, deliveryOptions.getProjectId(), apiCall, queryStr);
    }

    private Response logResponseInfo(Response response) {
        log.info("{} - {}", response.getStatusText(), response.getUri());
        log.debug("{} - {}:\n{}", response.getStatusCode(), response.getUri(), response.getResponseBody());
        return response;
    }

    private Response handleErrorIfNecessary(Response response) {
        final int status = response.getStatusCode();
        if (status >= 500) {
            log.error("Kentico API server error, status: {} {}", status, response.getStatusText());
            String message =
                    String.format(
                            "Unknown error with Kentico API.  Kentico is likely suffering site issues.  Status: %s %s",
                            status, response.getStatusText());
            throw new KenticoIOException(message);
        } else if (status >= 400) {
            log.error("Kentico API server error, status: {}", status);
            try {
                KenticoError kenticoError = objectMapper.readValue(response.getResponseBodyAsBytes(), KenticoError.class);
                throw new KenticoErrorException(kenticoError);
            } catch (IOException e) {
                log.error("IOException connecting to Kentico: {}", e.toString());
                throw new KenticoIOException(e);
            }
        }

        return response;
    }

    private JsonNode bodyToJsonNode(final String body) throws IOException {
        return objectMapper.readValue(body, JsonNode.class);
    }

    private <T> MaybeSource<T> putInCache(String url, Class<T> tClass, JsonNode jsonNode) throws com.fasterxml.jackson.core.JsonProcessingException {
        final T t = objectMapper.treeToValue(jsonNode, tClass);
        final List<ContentItem> containedContentItems;
        if (t instanceof ContentItemResponse) {
            containedContentItems = Collections.singletonList(((ContentItemResponse) t).getItem());
        } else if (t instanceof ContentItemsListingResponse) {
            containedContentItems = new ArrayList<>(((ContentItemsListingResponse) t).getItems());
        } else {
            containedContentItems = Collections.emptyList();
        }
        return cacheManager.put(url, jsonNode, containedContentItems)
                .andThen(Maybe.just(t));
    }

    private Publisher<?> configureErrorAndRetryFlow(Flowable<Throwable> errors) {
        final AtomicInteger counter = new AtomicInteger(0);
        return errors
                .flatMap(throwable -> {
                    // Don't attempt a retry for Kentico specific errors
                    if (throwable instanceof KenticoErrorException || throwable instanceof KenticoIOException) {
                        return Flowable.error(throwable);
                    }
                    // Do attempt a retry for any other error while we haven't reached the max attempts yet
                    else if (counter.incrementAndGet() <= deliveryOptions.getRetryAttempts()) {
                        return Flowable.just(throwable);
                    }
                    // After the max attempts wrap IOExceptions in a KenticoIOException and propagate it
                    else if (throwable instanceof IOException) {
                        return Flowable.error(new KenticoIOException((IOException) throwable));
                    }
                    // Propagate any other exception as is
                    else {
                        return Flowable.error(throwable);
                    }
                })
                .flatMap(e -> {
                    //Perform a binary exponential backoff
                    int wait = (int) (100 * Math.pow(2, counter.get()));
                    log.info("Reattempting request after {}ms (re-attempt {} out of max {})",
                            wait, counter.get(), deliveryOptions.getRetryAttempts());
                    return Flowable.timer(wait, TimeUnit.MILLISECONDS);
                });
    }

    private List<NameValuePair> addTypeParameterIfNecessary(Class tClass, List<NameValuePair> params) {
        Optional<NameValuePair> any = params.stream()
                .filter(nameValuePair -> nameValuePair.getName().equals("system.type"))
                .findAny();
        if (!any.isPresent()) {
            String contentType = stronglyTypedContentItemConverter.getContentType(tClass);
            if (contentType != null) {
                List<NameValuePair> updatedParams = new ArrayList<>(params);
                updatedParams.add(new NameValuePair("system.type", contentType));
                return updatedParams;
            }
        }
        return params;
    }

    private void reconfigureDeserializer() {
        objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        SimpleModule module = new SimpleModule();
        objectMapper.registerModule(new JSR310Module());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        objectMapper.registerModule(module);
    }

    private RichTextElementConverter createRichTextElementConverter() {
        return new RichTextElementConverter(
                getContentLinkUrlResolver(),
                getBrokenLinkUrlResolver(),
                getRichTextElementResolver(),
                templateEngineConfig,
                stronglyTypedContentItemConverter);
    }

    DeliveryOptions getDeliveryOptions() {
        return deliveryOptions;
    }

    @Override
    public void close() {
        if (asyncHttpClient != null) {
            try {
                asyncHttpClient.close();
            } catch (IOException e) {
                log.warn("Unexpected error while closing the http client", e);
            }
        }
    }
}
