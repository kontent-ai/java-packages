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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import kentico.kontent.delivery.template.TemplateEngineConfig;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


// TODO: add JavaDoc

@Slf4j
public class DeliveryClient {

    public static final String HEADER_X_KC_WAIT_FOR_LOADING_NEW_CONTENT = "X-KC-Wait-For-Loading-New-Content";
    public static final String HEADER_X_KC_SDK_ID = "X-KC-SDKID";
    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String HEADER_ACCEPT = "Accept";
    private static final String[] RESERVED_HEADERS = new String[]{HEADER_ACCEPT, HEADER_X_KC_SDK_ID, HEADER_AUTHORIZATION, HEADER_X_KC_WAIT_FOR_LOADING_NEW_CONTENT};
    private static String sdkId;

    static {
        try {
            Properties buildProps = new Properties();
            buildProps.load(DeliveryClient.class.getResourceAsStream("version.properties"));
            String repositoryHost = buildProps.getProperty("repository-host");
            String version = buildProps.getProperty("version");
            String packageId = buildProps.getProperty("package-id");
            repositoryHost = repositoryHost == null ? "localBuild" : repositoryHost;
            version = version == null ? "0.0.0" : version;
            packageId = packageId == null ? "com.github.kentico:kontent-delivery" : packageId;
            sdkId = String.format(
                    "%s;%s;%s",
                    repositoryHost,
                    packageId,
                    version);
            log.info("SDK ID: {}", sdkId);
        } catch (IOException e) {
            log.info("Jar manifest read error, setting developer build SDK ID");
            sdkId = "localBuild;com.github.kentico:kontent-delivery;0.0.0";
        }
    }

    private static final String ITEMS = "items";
    private static final String TYPES = "types";
    private static final String ELEMENTS = "elements";
    private static final String TAXONOMIES = "taxonomies";

    private static final String URL_CONCAT = "%s/%s";

    private static final List<Integer> RETRY_STATUSES = Collections.unmodifiableList(Arrays.asList(408, 429, 500, 502, 503, 504));

    private ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    private DeliveryOptions deliveryOptions;

    private ContentLinkUrlResolver contentLinkUrlResolver;
    private BrokenLinkUrlResolver brokenLinkUrlResolver;
    private RichTextElementResolver richTextElementResolver = new DelegatingRichTextElementResolver();
    private StronglyTypedContentItemConverter stronglyTypedContentItemConverter =
            new StronglyTypedContentItemConverter();
    private TemplateEngineConfig templateEngineConfig;

    private OkHttpClient httpClient;

    private AsyncCacheManager cacheManager = new AsyncCacheManager() {
        @Override
        public CompletionStage<JsonNode> get(String url) {
            return CompletableFuture.completedFuture(null);
        }

        @Override
        public CompletionStage put(String url, JsonNode jsonNode, List<ContentItem> containedContentItems) {
            return CompletableFuture.completedFuture(null);
        }
    };

    static final ScheduledExecutorService SCHEDULER = new ScheduledThreadPoolExecutor(0);

    /**
     * Please use this constructor when you need to initialize client with default template configuration - so when you are using template engine. For i.e. Android platform use {@link DeliveryClient#DeliveryClient(DeliveryOptions, TemplateEngineConfig)} and set second parameter to null.
     *
     * @param deliveryOptions delivery options {@link DeliveryOptions}
     */
    @SuppressWarnings("WeakerAccess")
    public DeliveryClient(DeliveryOptions deliveryOptions) {
        this(deliveryOptions, new TemplateEngineConfig());
    }

    @SuppressWarnings({"ResultOfMethodCallIgnored", "WeakerAccess"})
    public DeliveryClient(DeliveryOptions deliveryOptions, TemplateEngineConfig templateEngineConfig) {
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

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        if (deliveryOptions.getProxyServer() != null) {
            builder.proxy(deliveryOptions.getProxyServer());
        }

        this.httpClient = builder.build();
    }

    @SuppressWarnings("unused")
    public DeliveryClient(String projectId) {
        this(new DeliveryOptions(projectId));
    }

    @SuppressWarnings("unused")
    public DeliveryClient(String projectId, String previewApiKey) {
        this(new DeliveryOptions(projectId, previewApiKey));
    }

    public CompletionStage<ContentItemsListingResponse> getItems() {
        return getItems(Collections.emptyList());
    }

    @SuppressWarnings("WeakerAccess")
    public CompletionStage<ContentItemsListingResponse> getItems(List<NameValuePair> params) {
        return executeRequest(ITEMS, params, ContentItemsListingResponse.class)
                .thenApply(contentItemsListingResponse ->
                        contentItemsListingResponse
                                .setStronglyTypedContentItemConverter(stronglyTypedContentItemConverter))
                .thenApply(response -> {
                    createRichTextElementConverter().process(response.items);
                    return response;
                });
    }

    @SuppressWarnings("WeakerAccess")
    public <T> CompletionStage<List<T>> getItems(Class<T> tClass, List<NameValuePair> params) {
        return getItems(addTypeParameterIfNecessary(tClass, params))
                .thenApply(contentItemsListingResponse -> contentItemsListingResponse.castTo(tClass));
    }

    @SuppressWarnings("unused")
    public CompletionStage<ContentItemResponse> getItem(String contentItemCodename) {
        return getItem(contentItemCodename, Collections.emptyList());
    }

    @SuppressWarnings("unused")
    public <T> CompletionStage<List<T>> getItems(Class<T> tClass) {
        return getItems(tClass, Collections.emptyList());
    }

    @SuppressWarnings("WeakerAccess")
    public <T> CompletionStage<Page<T>> getPageOfItems(Class<T> tClass, List<NameValuePair> params) {
        return getItems(params)
                .thenApply(response ->
                        response.setStronglyTypedContentItemConverter(stronglyTypedContentItemConverter))
                .thenApply(response -> new Page<>(response, tClass));
    }

    @SuppressWarnings("WeakerAccess")
    public <T> CompletionStage<Page<T>> getNextPage(Page<T> currentPage) {
        final Pagination pagination = currentPage.getPagination();
        if (pagination.getNextPage() == null || pagination.getNextPage().isEmpty()) {
            return CompletableFuture.completedFuture(null);
        }

        return executeRequest(pagination.getNextPage(), ContentItemsListingResponse.class)
                .thenApply(response -> response.setStronglyTypedContentItemConverter(stronglyTypedContentItemConverter))
                .thenApply(response -> {
                    createRichTextElementConverter().process(response.items);
                    return response;
                })
                .thenApply(response -> new Page<>(response, currentPage.getType()));
    }

    @SuppressWarnings("unused")
    public <T> CompletionStage<T> getItem(String contentItemCodename, Class<T> tClass) {
        return getItem(contentItemCodename, tClass, Collections.emptyList());
    }

    @SuppressWarnings("WeakerAccess")
    public CompletionStage<ContentItemResponse> getItem(String contentItemCodename, List<NameValuePair> params) {
        final String apiCall = String.format(URL_CONCAT, ITEMS, contentItemCodename);
        return executeRequest(apiCall, params, ContentItemResponse.class)
                .thenApply(response ->
                        response.setStronglyTypedContentItemConverter(stronglyTypedContentItemConverter))
                .thenApply(response -> {
                    createRichTextElementConverter().process(response.item);
                    return response;
                });
    }

    @SuppressWarnings("WeakerAccess")
    public <T> CompletionStage<T> getItem(String contentItemCodename, Class<T> tClass, List<NameValuePair> params) {
        return getItem(contentItemCodename, addTypeParameterIfNecessary(tClass, params))
                .thenApply(contentItemResponse -> contentItemResponse.castTo(tClass));
    }

    public CompletionStage<ContentTypesListingResponse> getTypes() {
        return getTypes(Collections.emptyList());
    }

    @SuppressWarnings("WeakerAccess")
    public CompletionStage<ContentTypesListingResponse> getTypes(List<NameValuePair> params) {
        return executeRequest(TYPES, params, ContentTypesListingResponse.class);
    }

    public CompletionStage<ContentType> getType(String contentTypeCodeName) {
        final String apiCall = String.format(URL_CONCAT, TYPES, contentTypeCodeName);
        return executeRequest(apiCall, Collections.emptyList(), ContentType.class);
    }

    @SuppressWarnings("unused")
    public CompletionStage<Element> getContentTypeElement(String contentTypeCodeName, String elementCodeName) {
        return getContentTypeElement(contentTypeCodeName, elementCodeName, Collections.emptyList());
    }

    @SuppressWarnings("WeakerAccess")
    public CompletionStage<Element> getContentTypeElement(
            String contentTypeCodeName, String elementCodeName, List<NameValuePair> params) {
        final String apiCall = String.format("%s/%s/%s/%s", TYPES, contentTypeCodeName, ELEMENTS, elementCodeName);
        return executeRequest(apiCall, params, Element.class);
    }

    @SuppressWarnings("unused")
    public CompletionStage<TaxonomyGroupListingResponse> getTaxonomyGroups() {
        return getTaxonomyGroups(Collections.emptyList());
    }

    @SuppressWarnings("WeakerAccess")
    public CompletionStage<TaxonomyGroupListingResponse> getTaxonomyGroups(List<NameValuePair> params) {
        return executeRequest(TAXONOMIES, params, TaxonomyGroupListingResponse.class);
    }

    @SuppressWarnings("unused")
    public CompletionStage<TaxonomyGroup> getTaxonomyGroup(String taxonomyGroupCodename) {
        return getTaxonomyGroup(taxonomyGroupCodename, Collections.emptyList());
    }

    @SuppressWarnings("WeakerAccess")
    public CompletionStage<TaxonomyGroup> getTaxonomyGroup(String taxonomyGroupCodename, List<NameValuePair> params) {
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

    /**
     * Not working on Android platform because of JVM and Dalvik differences, please use {@link DeliveryClient#registerType(Class)} instead
     * Register by scanning the classpath for annotated classes by {@link ContentItemMapping} annotation.
     *
     * @param basePackage name of the base package
     */
    @SuppressWarnings("WeakerAccess")
    public void scanClasspathForMappings(String basePackage) {
        stronglyTypedContentItemConverter.scanClasspathForMappings(basePackage);
    }

    @SuppressWarnings("WeakerAccess")
    public void setCacheManager(AsyncCacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    /**
     * Sets the {@link CacheManager} for this client.
     *
     * @param cacheManager A {@link CacheManager} implementation for this client to use.
     * @see CacheManager
     */
    public void setCacheManager(final CacheManager cacheManager) {
        final AsyncCacheManager bridgedCacheManager = new AsyncCacheManager() {
            @Override
            public CompletionStage<JsonNode> get(String url) {

                return CompletableFuture.supplyAsync(() ->
                        cacheManager.get(url)
                );
            }

            @Override
            public CompletionStage put(String url, JsonNode jsonNode, List<ContentItem> containedContentItems) {
                return CompletableFuture.runAsync(() -> cacheManager.put(url, jsonNode, containedContentItems));
            }
        };
        this.setCacheManager(bridgedCacheManager);
    }

    private <T> CompletionStage<T> executeRequest(final String apiCall, final List<NameValuePair> queryParams, Class<T> tClass) {
        return executeRequest(createUrl(apiCall, queryParams), tClass);
    }

    private <T> CompletionStage<T> executeRequest(final String url, Class<T> tClass) {
        final Request request = buildNewRequest(url);
        log.debug("Request to url: {}", url);
        final boolean skipCache = Optional.ofNullable(request.header(HEADER_X_KC_WAIT_FOR_LOADING_NEW_CONTENT))
                .map(Boolean::valueOf)
                .orElse(false);


        if (skipCache) {
            return retrieveFromKentico(request, url, tClass, 0);
        } else {
            return cacheManager.get(url).thenApply(jsonNode -> {
                try {
                    if (jsonNode == null) {
                        return null;
                    }
                    return objectMapper.treeToValue(jsonNode, tClass);
                } catch (JsonProcessingException e) {
                    log.error("JsonProcessingException parsing Kentico object: {}", e.toString());
                }
                return null;
            }).thenCompose(result -> {
                if (result != null) {
                    return CompletableFuture.completedFuture(result);
                } else {
                    return retrieveFromKentico(request, url, tClass, 0);
                }
            });
        }
    }

    private <T> CompletionStage<T> retrieveFromKentico(Request request, final String url, Class<T> tClass, int retryTurn) {
        return send(request)
                .thenApply(this::logResponseInfo)
                .thenApply(this::handleErrorIfNecessary)
                .thenApply(Response::body)
                .thenApply((responseBody) -> {
                    try {
                        return responseBody.string();
                    } catch (IOException e) {
                        log.error("IOException when converting responseBody to body string: {}", e.toString());
                        throw new CompletionException(e);
                    }
                })
                .thenApply((bodyString) -> {
                    try {
                        return objectMapper.readValue(bodyString, JsonNode.class);
                    } catch (IOException e) {
                        log.error("IOException when mapping body string to the JsonNode: {}", e.toString());
                        throw new CompletionException(e);
                    }
                })
                .thenCompose((jsonNode) -> {
                    try {
                        return convertAndPutInCache(url, tClass, jsonNode);
                    } catch (JsonProcessingException e) {
                        log.error("JsonProcessingException when converting JsonNode to typed class: {}", e.toString());
                        throw new CompletionException(e);
                    }
                })
                .exceptionally((error) -> {
                    final AtomicInteger counter = new AtomicInteger(retryTurn);

                    if (error instanceof CompletionException) {
                        Throwable cause = error.getCause();

                        // Don't retry when when not KenticoException or not set to retry
                        boolean retry = cause instanceof KenticoException && ((KenticoException) cause).shouldRetry();

                        if (!retry) {
                            throw (CompletionException) error;
                        }
                    }

                    if (counter.incrementAndGet() > deliveryOptions.getRetryAttempts()) {
                        KenticoRetryException ex = new KenticoRetryException(deliveryOptions.getRetryAttempts());
                        ex.initCause(error.getCause());
                        throw ex;
                    }

                    //Perform a binary exponential backoff
                    int wait = (int) (100 * Math.pow(2, retryTurn));
                    log.info("Reattempting request after {}ms (re-attempt {} out of max {})",
                            wait, counter.get(), deliveryOptions.getRetryAttempts());

                    try {
                        return CompletableFuture.supplyAsync(
                                () -> {
                                    try {
                                        return retrieveFromKentico(request, url, tClass, counter.get())
                                                .toCompletableFuture().get();
                                    } catch (InterruptedException e) {
                                        log.error(String.format("InterruptedException have been raised on retial no. %d", counter.get()));
                                        throw new CompletionException(e);
                                    } catch (ExecutionException e) {
                                        log.error(String.format("ExecutionException have been raised on retrial no. %d", counter.get()));
                                        if (e.getCause() instanceof KenticoRetryException) {
                                            KenticoRetryException exception = new KenticoRetryException(((KenticoRetryException) e.getCause()).getMaxRetryAttempts());
                                            exception.initCause(error.getCause());
                                            throw exception;
                                        }
                                        throw new CompletionException(e);
                                    }
                                },
                                r -> SCHEDULER.schedule(
                                        () -> ForkJoinPool.commonPool().execute(r), wait, TimeUnit.MILLISECONDS)
                        ).toCompletableFuture()
                                .get();
                    } catch (InterruptedException e) {
                        log.error("InterruptedException have been raised for timeout");
                        throw new CompletionException(e);
                    } catch (ExecutionException e) {
                        log.error("ExecutionException have been raised for timeout");
                        if (e.getCause() instanceof KenticoRetryException) {
                            KenticoRetryException exception = new KenticoRetryException(((KenticoRetryException) e.getCause()).getMaxRetryAttempts());
                            exception.initCause(error.getCause());
                            throw exception;
                        }

                        throw new CompletionException(e);
                    }
                });
    }

    private Response handleErrorIfNecessary(Response response) throws KenticoIOException, KenticoErrorException {
        final int status = response.code();
        if (RETRY_STATUSES.contains(status)) {
            log.error("Kentico API retry status returned: {} (one of {})", status, RETRY_STATUSES.toString());
            try {
                KenticoError kenticoError = objectMapper.readValue(response.body().bytes(), KenticoError.class);
                throw new KenticoErrorException(kenticoError, true);
            } catch (IOException e) {
                log.error("IOException when trying to parse the error response body: {}", e.toString());
                throw new KenticoIOException(String.format("Kentico API retry status returned: %d (one of %s)", status, RETRY_STATUSES.toString()), true);
            }
        } else if (status >= 500) {
            log.error("Kentico API server error, status: {}", status);
            log.info("Request URL: ", response.request().url().toString());
            String message =
                    String.format(
                            "Unknown error with Kentico API.  Kentico is likely suffering site issues.  Status: %s",
                            status);
            throw new CompletionException(new KenticoIOException(message, false));
        } else if (status >= 400) {
            log.error("Kentico API server error, status: {}", status);
            try {
                KenticoError kenticoError = objectMapper.readValue(response.body().bytes(), KenticoError.class);
                throw new CompletionException(new KenticoErrorException(kenticoError, false));
            } catch (IOException e) {
                log.error("IOException connecting to Kentico: {}", e.toString());
                throw new CompletionException(new KenticoIOException(e, false));
            }
        }

        return response;
    }

    private CompletionStage<Response> send(Request request) {
        final CompletableFuture<Response> future = new CompletableFuture<>();
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                log.error("Request call failed with IO Exception: {}", e.getMessage());
                future.completeExceptionally(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                log.debug("Request call succeeded with response message:", response.message());
                future.complete(response);
            }
        });
        return future;
    }

    private Request buildNewRequest(String url) {
        Request.Builder requestBuilder = new Request.Builder().url(url);
        requestBuilder.header(HEADER_ACCEPT, "applications/json");
        requestBuilder.header(HEADER_X_KC_SDK_ID, sdkId);

        if (deliveryOptions.getProductionApiKey() != null) {
            requestBuilder.header(HEADER_AUTHORIZATION, String.format("Bearer %s", deliveryOptions.getProductionApiKey()));
        } else if (deliveryOptions.isUsePreviewApi()) {
            requestBuilder.header(HEADER_AUTHORIZATION, String.format("Bearer %s", deliveryOptions.getPreviewApiKey()));
        }
        if (deliveryOptions.isWaitForLoadingNewContent()) {
            requestBuilder.header(HEADER_X_KC_WAIT_FOR_LOADING_NEW_CONTENT, "true");
        }

        if (deliveryOptions.getCustomHeaders() != null){
            for (Header header : deliveryOptions.getCustomHeaders()) {
                if (Arrays.stream(RESERVED_HEADERS).anyMatch(header.getName()::equals)) {
                    log.info("Custom header with name {} will be ignored", header.getName());
                } else {
                    requestBuilder.header(header.getName(), header.getValue());
                }
            }
        }

        return requestBuilder.build();

    }

    private String createUrl(final String apiCall, final List<NameValuePair> queryParams) {

        final String queryStr = Optional.ofNullable(queryParams)
                .filter(params -> !params.isEmpty())
                .map(params -> params.stream()
                        .map(pair -> pair.getValue() != null
                                ? String.format("%s=%s", pair.getName(), pair.getValue())
                                : pair.getName())
                        .collect(Collectors.joining("&")))
                .map("?"::concat)
                .orElse("");

        final String endpoint = deliveryOptions.isUsePreviewApi() ?
                deliveryOptions.getPreviewEndpoint() : deliveryOptions.getProductionEndpoint();

        return String.format("%s/%s/%s%s", endpoint, deliveryOptions.getProjectId(), apiCall, queryStr);
    }

    private Response logResponseInfo(Response response) {
        log.info("{} - {}", response.message(), response.request().url());
        log.debug("{} - {}:\n{}", response.code(), response.request().url(), response.body());
        return response;
    }

    private <T> CompletionStage<T> convertAndPutInCache(String url, Class<T> tClass, JsonNode jsonNode) throws JsonProcessingException {
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
                .thenApply((result) -> t);
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
        objectMapper.registerModule(new JavaTimeModule());
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
}
