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

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.localserver.LocalServerTestBase;
import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class DeliveryClientTest extends LocalServerTestBase {

    // https://semver.org/#is-there-a-suggested-regular-expression-regex-to-check-a-semver-string
    private static String SEMVER_REGEX = "^(0|[1-9]\\d*)\\.(0|[1-9]\\d*)\\.(0|[1-9]\\d*)(?:-((?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*)(?:\\.(?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*))*))?(?:\\+([0-9a-zA-Z-]+(?:\\.[0-9a-zA-Z-]+)*))?$";

    @Test
    public void testSdkIdHeader() throws Exception {
        String projectId = "02a70003-e864-464e-b62c-e0ede97deb8c";
        String previewApiKey = "preview_api_key";

        this.serverBootstrap.registerHandler(
                String.format("/%s/%s", projectId, "items/on_roasts"),
                (request, response, context) -> {
                    String[] trackingHeaderValueParts = request.getHeaders("X-KC-SDKID")[0].getValue().split(";");
                    Assert.assertEquals(3, trackingHeaderValueParts.length);
                    Assert.assertEquals("jCenter", trackingHeaderValueParts[0]);
                    Assert.assertEquals("com.github.kentico:kontent-delivery", trackingHeaderValueParts[1]);
                    Assert.assertTrue("Tracking header version value does not comply with semver definition.", trackingHeaderValueParts[2].matches(SEMVER_REGEX));

                    response.setEntity(
                            new InputStreamEntity(
                                    this.getClass().getResourceAsStream("SampleContentItem.json")
                            )
                    );
                });

        HttpHost httpHost = this.start();
        DeliveryClient client = new DeliveryClient(projectId, previewApiKey);

        String testServerUri = httpHost.toURI();
        client.getDeliveryOptions().setPreviewEndpoint(testServerUri);

        ContentItemResponse item = client.getItem("on_roasts").toCompletableFuture().get();
        Assert.assertNotNull(item);
    }

    @Test
    public void testCustomHeadersPropagatedWithSdkIdHeader() throws Exception {
        String projectId = "02a70003-e864-464e-b62c-e0ede97deb8c";
        String previewApiKey = "preview_api_key";
        List<Header> headers = Arrays.asList(
                new Header("test-header-name1", "test-header-value1"),
                new Header("test-header-name2", "test-header-value2")
        );

        this.serverBootstrap.registerHandler(
                String.format("/%s/%s", projectId, "items/on_roasts"),
                (request, response, context) -> {
                    String[] trackingHeaderValueParts = request.getHeaders("X-KC-SDKID")[0].getValue().split(";");
                    Assert.assertEquals(3, trackingHeaderValueParts.length);
                    Assert.assertEquals("jCenter", trackingHeaderValueParts[0]);
                    Assert.assertEquals("com.github.kentico:kontent-delivery", trackingHeaderValueParts[1]);
                    Assert.assertTrue("Tracking header version value does not comply with semver definition.", trackingHeaderValueParts[2].matches(SEMVER_REGEX));

                    Assert.assertEquals(headers.get(0).getValue(), request.getHeaders(headers.get(0).getName())[0].getValue());
                    Assert.assertEquals(headers.get(1).getValue(), request.getHeaders(headers.get(1).getName())[0].getValue());

                    response.setEntity(
                            new InputStreamEntity(
                                    this.getClass().getResourceAsStream("SampleContentItem.json")
                            )
                    );
                });

        HttpHost httpHost = this.start();
        DeliveryClient client = new DeliveryClient(
                DeliveryOptions
                        .builder()
                        .projectId(projectId)
                        .previewApiKey(previewApiKey)
                        .usePreviewApi(true)
                        .customHeaders(headers)
                        .build()
        );

        String testServerUri = httpHost.toURI();
        client.getDeliveryOptions().setPreviewEndpoint(testServerUri);

        ContentItemResponse item = client.getItem("on_roasts").toCompletableFuture().get();
        Assert.assertNotNull(item);
    }

    @Test
    public void testCustomHeadersDoNotOverwriteReservedHeaders() throws Exception {
        String projectId = "02a70003-e864-464e-b62c-e0ede97deb8c";
        String previewApiKey = "preview_api_key";
        String customHeaderValue = "CUSTOM_VALUE_TO_BE_IGNORED";
        List<Header> headers = Arrays.asList(
                new Header(DeliveryClient.HEADER_AUTHORIZATION, customHeaderValue),
                new Header(DeliveryClient.HEADER_ACCEPT, customHeaderValue),
                new Header(DeliveryClient.HEADER_X_KC_SDK_ID, customHeaderValue),
                new Header(DeliveryClient.HEADER_X_KC_WAIT_FOR_LOADING_NEW_CONTENT, customHeaderValue)
        );

        this.serverBootstrap.registerHandler(
                String.format("/%s/%s", projectId, "items/on_roasts"),
                (request, response, context) -> {
                    Assert.assertNotEquals(headers.get(0).getValue(), request.getHeaders(headers.get(0).getName())[0].getValue());
                    Assert.assertNotEquals(headers.get(1).getValue(), request.getHeaders(headers.get(1).getName())[0].getValue());
                    Assert.assertNotEquals(headers.get(2).getValue(), request.getHeaders(headers.get(2).getName())[0].getValue());
                    Assert.assertArrayEquals(new Header[]{}, request.getHeaders(headers.get(3).getName()));

                    response.setEntity(
                            new InputStreamEntity(
                                    this.getClass().getResourceAsStream("SampleContentItem.json")
                            )
                    );
                });

        HttpHost httpHost = this.start();
        DeliveryClient client = new DeliveryClient(
                DeliveryOptions
                        .builder()
                        .projectId(projectId)
                        .previewApiKey(previewApiKey)
                        .usePreviewApi(true)
                        .customHeaders(headers)
                        .build()
        );

        String testServerUri = httpHost.toURI();
        client.getDeliveryOptions().setPreviewEndpoint(testServerUri);

        ContentItemResponse item = client.getItem("on_roasts").toCompletableFuture().get();
        Assert.assertNotNull(item);
    }

    @Test
    public void testRetryWorksForRetryStatusCode() throws Exception {
        String projectId = "02a70003-e864-464e-b62c-e0ede97deb8c";
        final AtomicBoolean sentError = new AtomicBoolean(false);

        this.serverBootstrap.registerHandler(
                String.format("/%s/%s", projectId, "items/on_roasts"),
                (request, response, context) -> {
                    if (sentError.get()) {
                        response.setEntity(
                                new InputStreamEntity(
                                        this.getClass().getResourceAsStream("SampleContentItem.json")
                                )
                        );
                    } else {
                        response.setStatusCode(500); // Retry status codes in DeliveryClient
                        response.setEntity(new StringEntity("Response Error!"));
                        sentError.set(true);
                    }
                });
        HttpHost httpHost = this.start();
        String testServerUri = httpHost.toURI();
        DeliveryOptions deliveryOptions = new DeliveryOptions();
        deliveryOptions.setProjectId(projectId);
        deliveryOptions.setProductionEndpoint(testServerUri);
        deliveryOptions.setRetryAttempts(1);

        DeliveryClient client = new DeliveryClient(deliveryOptions);

        ContentItemResponse item = client.getItem("on_roasts").toCompletableFuture().get();
        Assert.assertNotNull(item);
        Assert.assertTrue(sentError.get());
    }

    @Test
    public void testRetryStopsForJsonParseError() throws Exception {
        String projectId = "02a70003-e864-464e-b62c-e0ede97deb8c";
        final int retryAttempts = 2;
        final AtomicInteger sentErrorCount = new AtomicInteger(0);

        this.serverBootstrap.registerHandler(
                String.format("/%s/%s", projectId, "items/on_roasts"),
                (request, response, context) -> {
                    response.setEntity(new StringEntity("Response Error!"));
                    sentErrorCount.getAndIncrement();
                });
        HttpHost httpHost = this.start();
        String testServerUri = httpHost.toURI();
        DeliveryOptions deliveryOptions = DeliveryOptions.builder()
                .projectId(projectId)
                .productionEndpoint(testServerUri)
                .retryAttempts(3) // is ignored
                .build();
        deliveryOptions.setProjectId(projectId);
        deliveryOptions.setProductionEndpoint(testServerUri);
        deliveryOptions.setRetryAttempts(retryAttempts);

        try {
            DeliveryClient client = new DeliveryClient(deliveryOptions);
            client.getItem("on_roasts")
                    .toCompletableFuture()
                    .get();
            Assert.fail("Expected a failure exception");
        } catch (ExecutionException e) {
            Assert.assertTrue(e.getCause() instanceof JsonParseException);
        }
        Assert.assertEquals(1, sentErrorCount.get());
    }

    @Test
    public void testRetryStopsOnNonRetryStatus() throws Exception {
        String projectId = "02a70003-e864-464e-b62c-e0ede97deb8c";
        final int[] sentErrorCount = {0};

        this.serverBootstrap.registerHandler(
                String.format("/%s/%s", projectId, "items/error"),
                (request, response, context) -> {
                    response.setStatusCode(404);
                    response.setEntity(
                            new InputStreamEntity(
                                    this.getClass().getResourceAsStream("SampleKenticoError.json")
                            )
                    );
                    sentErrorCount[0] = sentErrorCount[0] + 1;
                });
        HttpHost httpHost = this.start();
        String testServerUri = httpHost.toURI();
        DeliveryOptions deliveryOptions = new DeliveryOptions();
        deliveryOptions.setProjectId(projectId);
        deliveryOptions.setProductionEndpoint(testServerUri);
        deliveryOptions.setRetryAttempts(1);

        try {
            DeliveryClient client = new DeliveryClient(deliveryOptions);
            client.getItem("error")
                    .toCompletableFuture()
                    .get();
            Assert.fail("Expected KenticoErrorException");
        } catch (ExecutionException e) {
            Assert.assertTrue(e.getCause() instanceof KenticoErrorException);
            KenticoErrorException error = (KenticoErrorException) e.getCause();
            Assert.assertEquals("The requested content item 'error' was not found.", error.getMessage());
            Assert.assertEquals("The requested content item 'error' was not found.", error.getKenticoError().getMessage());
        }
        Assert.assertEquals(1, sentErrorCount[0]);
    }

    @Test
    public void testGetItems() throws Exception {
        String projectId = "02a70003-e864-464e-b62c-e0ede97deb8c";

        this.serverBootstrap.registerHandler(
                String.format("/%s/%s", projectId, "items"),
                (request, response, context) -> {
                    String uri = String.format("http://testserver%s", request.getRequestLine().getUri());

                    List<NameValuePair> nameValuePairs =
                            URLEncodedUtils.parse(URI.create(uri), Charset.defaultCharset());
                    Map<String, String> params = convertNameValuePairsToMap(nameValuePairs);
                    Assert.assertEquals(1, params.size());
                    Assert.assertEquals("/path1/path2/test-article", params.get("elements.url_pattern"));
                    response.setEntity(
                            new InputStreamEntity(
                                    this.getClass().getResourceAsStream("SampleContentItemList.json")
                            )
                    );
                });
        HttpHost httpHost = this.start();
        String testServerUri = httpHost.toURI();
        DeliveryOptions deliveryOptions = new DeliveryOptions();
        deliveryOptions.setProjectId(projectId);
        deliveryOptions.setProductionEndpoint(testServerUri);

        DeliveryClient client = new DeliveryClient(deliveryOptions, null);
        client.setContentLinkUrlResolver(Link::getUrlSlug);
        client.setBrokenLinkUrlResolver(() -> "/404");
        client.addRichTextElementResolver(content -> String.format("%s%s", "<p>test</p>", content));

        List<kentico.kontent.delivery.NameValuePair> urlPattern = DeliveryParameterBuilder.params().filterEquals("elements.url_pattern", "/path1/path2/test-article").build();
        ContentItemsListingResponse items = client.getItems(urlPattern)
                .toCompletableFuture()
                .get();

        Assert.assertNotNull(items);
        Assert.assertTrue(((RichTextElement) items.getItems().get(1).getElements().get("description")).getValue().contains("href=\"/on roasts\""));
        Assert.assertTrue(((RichTextElement) items.getItems().get(1).getElements().get("description")).getValue().contains("<p>test</p>"));
    }

    @Test
    public void testGetAllItems() throws Exception {
        String projectId = "02a70003-e864-464e-b62c-e0ede97deb8c";

        this.serverBootstrap.registerHandler(
                String.format("/%s/%s", projectId, "items"),
                (request, response, context) -> response.setEntity(
                        new InputStreamEntity(
                                this.getClass().getResourceAsStream("SampleContentItemList.json")
                        )
                ));
        HttpHost httpHost = this.start();

        DeliveryOptions deliveryOptions = new DeliveryOptions();
        deliveryOptions.setProductionEndpoint(httpHost.toURI());
        deliveryOptions.setProjectId(projectId);
        DeliveryClient client = new DeliveryClient(deliveryOptions, null);

        ContentItemsListingResponse items = client.getItems()
                .toCompletableFuture()
                .get();
        Assert.assertNotNull(items);

    }


    @Test
    @SuppressWarnings("all")
    public void testGetItemsAsPage() throws Exception {
        String projectId = "02a70003-e864-464e-b62c-e0ede97deb8c";

        this.serverBootstrap.registerHandler(
                String.format("/%s/%s", projectId, "items"),
                (request, response, context) -> {
                    String uri = String.format("http://testserver%s", request.getRequestLine().getUri());

                    List<NameValuePair> nameValuePairs =
                            URLEncodedUtils.parse(URI.create(uri), Charset.defaultCharset());
                    Map<String, String> params = convertNameValuePairsToMap(nameValuePairs);

                    BufferedReader bufferedReader =
                            new BufferedReader(
                                    new InputStreamReader(
                                            this.getClass().getResourceAsStream("SampleContentItemList.json")
                                    )
                            );
                    String line;
                    StringBuilder responseString = new StringBuilder();
                    while ((line = bufferedReader.readLine()) != null) {
                        if (line.contains("next_page")) {
                            responseString.append("\"next_page\": \"");
                            responseString.append((new HttpHost("localhost", this.server.getLocalPort(), this.scheme.name())).toURI());
                            responseString.append("/nextPage\"\r\n");
                        } else {
                            responseString.append(line).append("\r\n");
                        }
                    }
                    bufferedReader.close();
                    response.setEntity(new StringEntity(responseString.toString()));
                });
        this.serverBootstrap.registerHandler("/nextPage",
                (request, response, context) -> response.setEntity(
                        new StringEntity(
                                "{\n" +
                                        "  \"items\": [],\n" +
                                        "  \"modular_content\": {},\n" +
                                        "  \"pagination\": {\n" +
                                        "    \"skip\": 50,\n" +
                                        "    \"limit\": 2,\n" +
                                        "    \"count\": 0,\n" +
                                        "    \"next_page\": \"\"\n" +
                                        "  }\n" +
                                        "}"
                        )
                ));
        HttpHost httpHost = this.start();
        DeliveryOptions deliveryOptions = new DeliveryOptions();
        deliveryOptions.setProductionEndpoint(httpHost.toURI());
        deliveryOptions.setProjectId(projectId);
        DeliveryClient client = new DeliveryClient(deliveryOptions, null);

        Page<ContentItem> pageOfItems =
                client.getPageOfItems(ContentItem.class, DeliveryParameterBuilder.params().page(0, 3).build())
                        .toCompletableFuture()
                        .get();
        Assert.assertEquals(3, pageOfItems.getSize());
        Assert.assertEquals(3, pageOfItems.getContent().size());
        Assert.assertTrue(pageOfItems.hasContent());
        Assert.assertTrue(pageOfItems.isFirst());
        Assert.assertFalse(pageOfItems.isLast());
        Assert.assertTrue(pageOfItems.hasNext());
        Assert.assertFalse(pageOfItems.hasPrevious());

        Page<ContentItem> nextPage = client.getNextPage(pageOfItems)
                .toCompletableFuture()
                .get();
        Assert.assertEquals(0, nextPage.getSize());
        Assert.assertFalse(nextPage.hasContent());
        Assert.assertFalse(nextPage.isFirst());
        Assert.assertTrue(nextPage.isLast());
        Assert.assertFalse(nextPage.hasNext());
        Assert.assertTrue(nextPage.hasPrevious());

        Assert.assertNull(client.getNextPage(nextPage).toCompletableFuture().get());

    }


    @Test
    @SuppressWarnings("Duplicates")
    public void testStronglyTypedGetItemsAsPage() throws Exception {
        String projectId = "02a70003-e864-464e-b62c-e0ede97deb8c";

        this.serverBootstrap.registerHandler(
                String.format("/%s/%s", projectId, "items"),
                (request, response, context) -> {
                    BufferedReader bufferedReader =
                            new BufferedReader(
                                    new InputStreamReader(
                                            this.getClass().getResourceAsStream("SampleContentItemList.json")
                                    )
                            );
                    String line;
                    StringBuilder responseString = new StringBuilder();
                    while ((line = bufferedReader.readLine()) != null) {
                        if (line.contains("next_page")) {
                            responseString.append("\"next_page\": \"");
                            responseString.append((new HttpHost("localhost", this.server.getLocalPort(), this.scheme.name())).toURI());
                            responseString.append("/nextPage\"\r\n");
                        } else {
                            responseString.append(line).append("\r\n");
                        }
                    }
                    bufferedReader.close();
                    response.setEntity(new StringEntity(responseString.toString()));
                });
        this.serverBootstrap.registerHandler("/nextPage",
                (request, response, context) -> response.setEntity(
                        new StringEntity(
                                "{\n" +
                                        "  \"items\": [],\n" +
                                        "  \"modular_content\": {},\n" +
                                        "  \"pagination\": {\n" +
                                        "    \"skip\": 50,\n" +
                                        "    \"limit\": 2,\n" +
                                        "    \"count\": 0,\n" +
                                        "    \"next_page\": \"\"\n" +
                                        "  }\n" +
                                        "}"
                        )
                ));
        HttpHost httpHost = this.start();
        DeliveryOptions deliveryOptions = new DeliveryOptions();
        deliveryOptions.setProductionEndpoint(httpHost.toURI());
        deliveryOptions.setProjectId(projectId);
        DeliveryClient client = new DeliveryClient(deliveryOptions, null);

        Page<ArticleItem> pageOfItems =
                client.getPageOfItems(ArticleItem.class, DeliveryParameterBuilder.params().page(0, 3).build())
                        .toCompletableFuture()
                        .get();
        Assert.assertEquals(3, pageOfItems.getSize());
        Assert.assertEquals(3, pageOfItems.getContent().size());
        Assert.assertTrue(pageOfItems.hasContent());
        Assert.assertTrue(pageOfItems.isFirst());
        Assert.assertFalse(pageOfItems.isLast());
        Assert.assertTrue(pageOfItems.hasNext());
        Assert.assertFalse(pageOfItems.hasPrevious());

        Page<ArticleItem> nextPage = client.getNextPage(pageOfItems)
                .toCompletableFuture()
                .get();
        Assert.assertEquals(0, nextPage.getSize());
        Assert.assertFalse(nextPage.hasContent());
        Assert.assertFalse(nextPage.isFirst());
        Assert.assertTrue(nextPage.isLast());
        Assert.assertFalse(nextPage.hasNext());
        Assert.assertTrue(nextPage.hasPrevious());

        Assert.assertNull(client.getNextPage(nextPage).toCompletableFuture().get());
    }


    @Test
    public void testGetItemWithParams() throws Exception {
        String projectId = "02a70003-e864-464e-b62c-e0ede97deb8c";

        this.serverBootstrap.registerHandler(
                String.format("/%s/%s", projectId, "items/on_roasts"),
                (request, response, context) -> {
                    String uri = String.format("http://testserver%s", request.getRequestLine().getUri());

                    List<NameValuePair> nameValuePairs =
                            URLEncodedUtils.parse(URI.create(uri), Charset.defaultCharset());
                    Map<String, String> params = convertNameValuePairsToMap(nameValuePairs);
                    Assert.assertEquals(1, params.size());
                    Assert.assertEquals("/path1/path2/test-article", params.get("elements.url_pattern"));
                    response.setEntity(
                            new InputStreamEntity(
                                    this.getClass().getResourceAsStream("SampleContentItem.json")
                            )
                    );
                });
        HttpHost httpHost = this.start();
        String testServerUri = httpHost.toURI();
        DeliveryOptions deliveryOptions = new DeliveryOptions();
        deliveryOptions.setProjectId(projectId);
        deliveryOptions.setProductionEndpoint(testServerUri);

        DeliveryClient client = new DeliveryClient(deliveryOptions);
        client.setContentLinkUrlResolver(Link::getUrlSlug);

        List<kentico.kontent.delivery.NameValuePair> urlPattern = DeliveryParameterBuilder.params().filterEquals("elements.url_pattern", "/path1/path2/test-article").build();
        ContentItemResponse item = client.getItem("on_roasts", urlPattern)
                .toCompletableFuture()
                .get();

        Assert.assertNotNull(item);

    }

    @Test
    public void testGetItem() throws Exception {
        String projectId = "02a70003-e864-464e-b62c-e0ede97deb8c";

        this.serverBootstrap.registerHandler(
                String.format("/%s/%s", projectId, "items/on_roasts"),
                (request, response, context) -> response.setEntity(
                        new InputStreamEntity(
                                this.getClass().getResourceAsStream("SampleContentItem.json")
                        )
                ));
        HttpHost httpHost = this.start();
        DeliveryClient client = new DeliveryClient(projectId);

        String testServerUri = httpHost.toURI();
        client.getDeliveryOptions().setProductionEndpoint(testServerUri);
        client.getDeliveryOptions().retryAttempts = -1;

        ContentItemResponse item = client.getItem("on_roasts")
                .toCompletableFuture()
                .get();
        Assert.assertNotNull(item);
    }

    @Test
    public void testGetTaxonomyList() throws Exception {
        String projectId = "02a70003-e864-464e-b62c-e0ede97deb8c";

        this.serverBootstrap.registerHandler(
                String.format("/%s/%s", projectId, "taxonomies"),
                (request, response, context) -> response.setEntity(
                        new InputStreamEntity(
                                this.getClass().getResourceAsStream("SampleTaxonomyGroupListingResponse.json")
                        )
                ));
        HttpHost httpHost = this.start();
        DeliveryClient client = new DeliveryClient(projectId);

        String testServerUri = httpHost.toURI();
        client.getDeliveryOptions().setProductionEndpoint(testServerUri);

        TaxonomyGroupListingResponse response = client.getTaxonomyGroups()
                .toCompletableFuture()
                .get();
        Assert.assertNotNull(response);

    }

    @Test
    @SuppressWarnings("all")
    public void testGetTaxonomyGroup() throws Exception {
        String projectId = "02a70003-e864-464e-b62c-e0ede97deb8c";

        this.serverBootstrap.registerHandler(
                String.format("/%s/%s", projectId, "taxonomies/personas"),
                (request, response, context) -> response.setEntity(
                        new InputStreamEntity(
                                this.getClass().getResourceAsStream("SampleTaxonomyGroup.json")
                        )
                ));
        HttpHost httpHost = this.start();
        DeliveryClient client = new DeliveryClient(projectId);

        String testServerUri = httpHost.toURI();
        client.getDeliveryOptions().setProductionEndpoint(testServerUri);

        TaxonomyGroup taxonomyGroup = client.getTaxonomyGroup("personas")
                .toCompletableFuture()
                .get();
        Assert.assertNotNull(taxonomyGroup);
    }

    @Test
    public void testCache() throws Exception {
        String projectId = "02a70003-e864-464e-b62c-e0ede97deb8c";
        DeliveryClient client = new DeliveryClient(projectId);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JSR310Module());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        final JsonNode jsonNode = objectMapper.readValue(this.getClass().getResourceAsStream("SampleContentItem.json"), JsonNode.class);

        final SimpleInMemoryCacheManager testCache = new SimpleInMemoryCacheManager();
        client.setCacheManager(testCache);

        testCache.put("https://deliver.kontent.ai/02a70003-e864-464e-b62c-e0ede97deb8c/items/on_roasts", jsonNode, null);

        ContentItemResponse item = client.getItem("on_roasts")
                .toCompletableFuture()
                .get();
        Assert.assertNotNull(item);
        Assert.assertEquals(1, testCache.queries.get());
        Assert.assertEquals(1, testCache.hits.get());

    }

    @Test
    public void testPutInCacheAfterNotFoundInCache() throws Exception {
        String projectId = "02a70003-e864-464e-b62c-e0ede97deb8c";

        this.serverBootstrap.registerHandler(
                String.format("/%s/%s", projectId, "items/on_roasts"),
                (request, response, context) -> response.setEntity(
                        new InputStreamEntity(
                                this.getClass().getResourceAsStream("SampleContentItem.json")
                        )
                ));
        HttpHost httpHost = this.start();
        DeliveryClient client = new DeliveryClient(projectId);

        final SimpleInMemoryCacheManager testCache = new SimpleInMemoryCacheManager();
        client.setCacheManager(testCache);

        String testServerUri = httpHost.toURI();
        client.getDeliveryOptions().setProductionEndpoint(testServerUri);

        ContentItemResponse item = client.getItem("on_roasts")
                .toCompletableFuture()
                .get();
        Assert.assertNotNull(item);
        Assert.assertEquals(1, testCache.queries.get());
        Assert.assertEquals(0, testCache.hits.get());
        Assert.assertEquals(1, testCache.puts.get());
    }


    @Test
    public void testExplicitlySkipCache() throws Exception {
        String projectId = "02a70003-e864-464e-b62c-e0ede97deb8c";

        this.serverBootstrap.registerHandler(
                String.format("/%s/%s", projectId, "items/on_roasts"),
                (request, response, context) -> response.setEntity(
                        new InputStreamEntity(
                                this.getClass().getResourceAsStream("SampleContentItem.json")
                        )
                ));
        HttpHost httpHost = this.start();
        DeliveryClient client = new DeliveryClient(projectId);

        final SimpleInMemoryCacheManager testCache = new SimpleInMemoryCacheManager();
        client.setCacheManager(testCache);

        String testServerUri = httpHost.toURI();
        client.getDeliveryOptions().setProductionEndpoint(testServerUri);
        client.getDeliveryOptions().setWaitForLoadingNewContent(true);

        ContentItemResponse item = client.getItem("on_roasts")
                .toCompletableFuture()
                .get();
        Assert.assertNotNull(item);

        Assert.assertEquals(0, testCache.queries.get());
        Assert.assertEquals(1, testCache.puts.get());
    }

    @Test
    public void testCacheWriteOnceReadMany() throws Exception {
        String projectId = "02a70003-e864-464e-b62c-e0ede97deb8c";

        final AtomicInteger kenticoGets = new AtomicInteger(0);

        this.serverBootstrap.registerHandler(
                String.format("/%s/%s", projectId, "items/on_roasts"),
                (request, response, context) -> {
                    kenticoGets.incrementAndGet();
                    response.setEntity(
                            new InputStreamEntity(
                                    this.getClass().getResourceAsStream("SampleContentItem.json")
                            ));
                });
        HttpHost httpHost = this.start();
        DeliveryClient client = new DeliveryClient(projectId);

        final String testServerUri = httpHost.toURI();
        client.getDeliveryOptions().setProductionEndpoint(testServerUri);

        final SimpleInMemoryCacheManager testCache = new SimpleInMemoryCacheManager();
        client.setCacheManager(testCache);

        final int nrOfTimesToRetrieveItem = 10;
        for (int i = 0; i < nrOfTimesToRetrieveItem; i++) {
            Assert.assertNotNull(client.getItem("on_roasts")
                    .toCompletableFuture()
                    .get());
        }

        Assert.assertEquals(1, kenticoGets.get());
        Assert.assertEquals(1, testCache.puts.get());
        Assert.assertEquals(nrOfTimesToRetrieveItem, testCache.queries.get());
        Assert.assertEquals(nrOfTimesToRetrieveItem - 1, testCache.hits.get());
        Assert.assertTrue(testCache.cache.containsKey(testServerUri + "/02a70003-e864-464e-b62c-e0ede97deb8c/items/on_roasts"));
    }


    @Test
    public void testCacheRepopulationAfterInvalidation() throws Exception {
        String projectId = "02a70003-e864-464e-b62c-e0ede97deb8c";

        final AtomicInteger kenticoGets = new AtomicInteger(0);

        this.serverBootstrap.registerHandler(
                String.format("/%s/%s", projectId, "items/on_roasts"),
                (request, response, context) -> {
                    kenticoGets.incrementAndGet();
                    response.setEntity(
                            new InputStreamEntity(
                                    this.getClass().getResourceAsStream("SampleContentItem.json")
                            ));
                });
        HttpHost httpHost = this.start();
        DeliveryClient client = new DeliveryClient(projectId);

        final String testServerUri = httpHost.toURI();
        client.getDeliveryOptions().setProductionEndpoint(testServerUri);

        final SimpleInMemoryCacheManager testCache = new SimpleInMemoryCacheManager();
        client.setCacheManager(testCache);

        Assert.assertNotNull(client.getItem("on_roasts").toCompletableFuture().get());
        Assert.assertNotNull(client.getItem("on_roasts").toCompletableFuture().get());

        testCache.invalidate(new SimpleInMemoryCacheManager.CacheTag("origins_of_arabica_bourbon", "en-US"));

        Assert.assertNotNull(client.getItem("on_roasts").toCompletableFuture().get());
        Assert.assertEquals(2, kenticoGets.get());
        Assert.assertEquals(2, testCache.puts.get());
        Assert.assertEquals(3, testCache.queries.get());
    }

    @Test
    public void testReplacingResolver() {
        String projectId = "02a70003-e864-464e-b62c-e0ede97deb8c";
        DeliveryClient deliveryClient = new DeliveryClient(projectId);
        Assert.assertTrue(deliveryClient.getRichTextElementResolver() instanceof DelegatingRichTextElementResolver);
        deliveryClient.setRichTextElementResolver(null);
        //Test that the entire resolver is replaced
        deliveryClient.addRichTextElementResolver(content -> "resolver1");
        Assert.assertFalse(deliveryClient.getRichTextElementResolver() instanceof DelegatingRichTextElementResolver);
        Assert.assertEquals("resolver1", deliveryClient.getRichTextElementResolver().resolve("replaceme"));
        //Test that adding a new one saves the existing one by creating a delegating resolver
        deliveryClient.addRichTextElementResolver(content -> content.replace("1", "2"));
        Assert.assertTrue(deliveryClient.getRichTextElementResolver() instanceof DelegatingRichTextElementResolver);
        Assert.assertEquals(2, ((DelegatingRichTextElementResolver) deliveryClient.getRichTextElementResolver()).resolvers.size());
        Assert.assertEquals("resolver2", deliveryClient.getRichTextElementResolver().resolve("replaceme"));
    }

    @Test
    public void testGetStronglyTypedItem() throws Exception {
        String projectId = "02a70003-e864-464e-b62c-e0ede97deb8c";

        this.serverBootstrap.registerHandler(
                String.format("/%s/%s", projectId, "items/on_roasts"),
                (request, response, context) -> response.setEntity(
                        new InputStreamEntity(
                                this.getClass().getResourceAsStream("SampleContentItem.json")
                        )
                ));
        HttpHost httpHost = this.start();
        DeliveryClient client = new DeliveryClient(projectId);

        String testServerUri = httpHost.toURI();
        client.getDeliveryOptions().setProductionEndpoint(testServerUri);

        ArticleItem item = client.getItem("on_roasts", ArticleItem.class)
                .toCompletableFuture()
                .get();

        Assert.assertNotNull(item);
        Assert.assertNotNull(item.getSystemInformationObject());
        Assert.assertNull(item.getRandomValue());
        Assert.assertNull(item.getRandomStringList());
        Assert.assertNull(item.getRandomStringMap());
        Assert.assertNull(item.randomStringListWithNoAccessors);
        Assert.assertNull(item.getRandomStringListWithNoSetter());
        Assert.assertNotNull(item.getPostDate());
        Assert.assertEquals(2014, item.getPostDate().getYear());
        Assert.assertNotNull(item.getTeaserImage());
        Assert.assertEquals(1, item.getTeaserImage().size());
        Assert.assertNotNull(item.getCoffeeProcessingTechniques());
        Assert.assertEquals("Coffee processing techniques", item.getCoffeeProcessingTechniques().getTitle());
        Assert.assertNotNull(item.getArabicaBourbonOrigin());
        Assert.assertEquals("Origins of Arabica Bourbon", item.getArabicaBourbonOrigin().getSystem().getName());
        Assert.assertNotNull(item.getArticleItems());
        Assert.assertEquals(2, item.getArticleItems().size());
        Assert.assertNotNull(item.getAllLinkedItems());
        Assert.assertEquals(2, item.getAllLinkedItems().size());
        Assert.assertNotNull(item.getAllLinkedItemsMap());
        Assert.assertEquals(2, item.getAllLinkedItemsMap().size());
    }

    @Test
    public void testGetStronglyTypedNestedItems() throws Exception {
        String projectId = "bac6b90c-4f0d-01e9-a3d8-3bc0ec36c3e3";

        this.serverBootstrap.registerHandler(
                String.format("/%s/%s", projectId, "items/sample_page"),
                (request, response, context) -> response.setEntity(
                        new InputStreamEntity(
                                this.getClass().getResourceAsStream("SampleNestedItemWithDepth2.json")
                        )
                ));
        HttpHost httpHost = this.start();
        DeliveryClient client = new DeliveryClient(projectId);

        String testServerUri = httpHost.toURI();
        client.getDeliveryOptions().setProductionEndpoint(testServerUri);

        kentico.kontent.delivery.nestedmodels.Page item = client.getItem(
                "sample_page",
                kentico.kontent.delivery.nestedmodels.Page.class,
                DeliveryParameterBuilder.params()
                        .linkedItemsDepth(2)
                        .build()
        ).toCompletableFuture()
                .get();

        Assert.assertNotNull(item);
        Assert.assertEquals("Sample page",item.getTitle());
        Assert.assertEquals(2, item.getSections().size());

        Assert.assertEquals("Section 1", item.getSections().get(0).getHeadline());
        Assert.assertEquals(2, item.getSections().get(0).getSectionParts().size());
        Assert.assertEquals("Part 1", item.getSections().get(0).getSectionParts().get(0).getTitle());
        Assert.assertEquals("Part 2", item.getSections().get(0).getSectionParts().get(1).getTitle());

        Assert.assertEquals("Section 2", item.getSections().get(1).getHeadline());
        Assert.assertEquals(2, item.getSections().get(1).getSectionParts().size());
        Assert.assertEquals("Part A", item.getSections().get(1).getSectionParts().get(0).getTitle());
        Assert.assertEquals("Part B", item.getSections().get(1).getSectionParts().get(1).getTitle());
    }

    @Test

    public void securedAPI() {
        DeliveryOptions.builder().projectId("02a70003-e864-464e-b62c-e0ede97deb8c").productionApiKey("ksldajhflkjshfdlkj");


    }


    @Test
    public void testGetStronglyTypedItemByRegisteringType() throws Exception {
        String projectId = "02a70003-e864-464e-b62c-e0ede97deb8c";

        this.serverBootstrap.registerHandler(
                String.format("/%s/%s", projectId, "items/on_roasts"),
                (request, response, context) -> {
                    String uri = String.format("http://testserver%s", request.getRequestLine().getUri());

                    List<NameValuePair> nameValuePairs =
                            URLEncodedUtils.parse(URI.create(uri), Charset.defaultCharset());
                    Assert.assertFalse(nameValuePairs.stream()
                            .anyMatch(nameValuePair -> nameValuePair.getName().equals("system.type")));

                    response.setEntity(
                            new InputStreamEntity(
                                    this.getClass().getResourceAsStream("SampleContentItem.json")
                            )
                    );
                });
        HttpHost httpHost = this.start();
        DeliveryClient client = new DeliveryClient(projectId);
        client.registerType("article", ArticleItem.class);

        String testServerUri = httpHost.toURI();
        client.getDeliveryOptions().setProductionEndpoint(testServerUri);

        Object itemObj = client.getItem("on_roasts", Object.class)
                .toCompletableFuture()
                .get();
        Assert.assertNotNull(itemObj);
        Assert.assertTrue(itemObj instanceof ArticleItem);
    }

    @Test
    public void testGetStronglyTypedItemAutomaticallyAddsSystemType() throws Exception {
        String projectId = "02a70003-e864-464e-b62c-e0ede97deb8c";

        this.serverBootstrap.registerHandler(
                String.format("/%s/%s", projectId, "items/on_roasts"),
                (request, response, context) -> {
                    String uri = String.format("http://testserver%s", request.getRequestLine().getUri());

                    List<NameValuePair> nameValuePairs =
                            URLEncodedUtils.parse(URI.create(uri), Charset.defaultCharset());
                    Assert.assertEquals(1, nameValuePairs.stream()
                            .filter(nameValuePair -> nameValuePair.getName().equals("system.type"))
                            .count());

                    Map<String, String> params = convertNameValuePairsToMap(nameValuePairs);

                    Assert.assertTrue(params.containsKey("system.type"));
                    Assert.assertEquals("article", params.get("system.type"));

                    response.setEntity(
                            new InputStreamEntity(
                                    this.getClass().getResourceAsStream("SampleContentItem.json")
                            )
                    );
                });
        HttpHost httpHost = this.start();
        DeliveryClient client = new DeliveryClient(projectId);
        client.registerType("article", ArticleItem.class);

        String testServerUri = httpHost.toURI();
        client.getDeliveryOptions().setProductionEndpoint(testServerUri);

        ArticleItem itemObj = client.getItem("on_roasts", ArticleItem.class)
                .toCompletableFuture()
                .get();
        Assert.assertNotNull(itemObj);
    }


    @Test
    public void testGetStronglyTypedItemAutomaticallyDoesNotSentSystemTypeWhenAdded() throws Exception {
        String projectId = "02a70003-e864-464e-b62c-e0ede97deb8c";

        this.serverBootstrap.registerHandler(
                String.format("/%s/%s", projectId, "items/on_roasts"),
                (request, response, context) -> {
                    String uri = String.format("http://testserver%s", request.getRequestLine().getUri());

                    List<NameValuePair> nameValuePairs =
                            URLEncodedUtils.parse(URI.create(uri), Charset.defaultCharset());
                    Assert.assertEquals(1, nameValuePairs.stream()
                            .filter(nameValuePair -> nameValuePair.getName().equals("system.type"))
                            .count());
                    Map<String, String> params = convertNameValuePairsToMap(nameValuePairs);

                    Assert.assertTrue(params.containsKey("system.type"));
                    Assert.assertEquals("customVal", params.get("system.type"));

                    response.setEntity(
                            new InputStreamEntity(
                                    this.getClass().getResourceAsStream("SampleContentItem.json")
                            )
                    );
                });
        HttpHost httpHost = this.start();
        DeliveryClient client = new DeliveryClient(projectId);
        client.registerType("article", ArticleItem.class);

        String testServerUri = httpHost.toURI();
        client.getDeliveryOptions().setProductionEndpoint(testServerUri);

        ArticleItem itemObj = client.getItem(
                "on_roasts",
                ArticleItem.class,
                DeliveryParameterBuilder.params()
                        .filterEquals("system.type", "customVal")
                        .build())
                .toCompletableFuture()
                .get();
        Assert.assertNotNull(itemObj);
    }


    @Test
    public void testGetStronglyTypedItemByRegisteringMapping() throws Exception {
        String projectId = "02a70003-e864-464e-b62c-e0ede97deb8c";

        this.serverBootstrap.registerHandler(
                String.format("/%s/%s", projectId, "items/on_roasts"),
                (request, response, context) -> response.setEntity(
                        new InputStreamEntity(
                                this.getClass().getResourceAsStream("SampleContentItem.json")
                        )
                ));
        HttpHost httpHost = this.start();
        DeliveryClient client = new DeliveryClient(projectId);
        client.registerType(ArticleItem.class);

        String testServerUri = httpHost.toURI();
        client.getDeliveryOptions().setProductionEndpoint(testServerUri);

        Object itemObj = client.getItem("on_roasts", Object.class)
                .toCompletableFuture()
                .get();
        Assert.assertNotNull(itemObj);
        Assert.assertTrue(itemObj instanceof ArticleItem);
    }


    @Test
    public void testCastToDefaultStronglyTypedItem() throws Exception {
        String projectId = "02a70003-e864-464e-b62c-e0ede97deb8c";

        this.serverBootstrap.registerHandler(
                String.format("/%s/%s", projectId, "items/on_roasts"),
                (request, response, context) -> response.setEntity(
                        new InputStreamEntity(
                                this.getClass().getResourceAsStream("SampleContentItem.json")
                        )
                ));
        HttpHost httpHost = this.start();
        DeliveryClient client = new DeliveryClient(projectId);
        client.registerType(ArticleItem.class);

        String testServerUri = httpHost.toURI();
        client.getDeliveryOptions().setProductionEndpoint(testServerUri);

        ContentItemResponse response = client.getItem("on_roasts")
                .toCompletableFuture()
                .get();
        ContentItem itemObj = response.getItem();
        Assert.assertNotNull(itemObj);
        Object casted = itemObj.castToDefault();
        Assert.assertNotNull(casted);
        Assert.assertTrue(casted instanceof ArticleItem);
    }

    @Test
    public void testCastToStronglyTypedItemByContentTypeString() throws Exception {
        String projectId = "02a70003-e864-464e-b62c-e0ede97deb8c";

        this.serverBootstrap.registerHandler(
                String.format("/%s/%s", projectId, "items/on_roasts"),
                (request, response, context) -> response.setEntity(
                        new InputStreamEntity(
                                this.getClass().getResourceAsStream("SampleContentItem.json")
                        )
                ));
        HttpHost httpHost = this.start();
        DeliveryClient client = new DeliveryClient(projectId);
        client.registerType(ArticleItem.class);

        String testServerUri = httpHost.toURI();
        client.getDeliveryOptions().setProductionEndpoint(testServerUri);

        ContentItemResponse response = client.getItem("on_roasts")
                .toCompletableFuture()
                .get();
        ContentItem itemObj = response.getItem();
        Assert.assertNotNull(itemObj);
        Object casted = itemObj.castTo("article");
        Assert.assertNotNull(casted);
        Assert.assertTrue(casted instanceof ArticleItem);
    }

    @Test
    @SuppressWarnings("all")
    public void testContentItemListMapping() throws Exception {
        String projectId = "02a70003-e864-464e-b62c-e0ede97deb8c";

        this.serverBootstrap.registerHandler(
                String.format("/%s/%s", projectId, "items/on_roasts"),
                (request, response, context) -> response.setEntity(
                        new InputStreamEntity(
                                this.getClass().getResourceAsStream("SampleContentItem.json")
                        )
                ));
        HttpHost httpHost = this.start();
        DeliveryClient client = new DeliveryClient(projectId);
        client.registerType(ArticleItem.class);

        String testServerUri = httpHost.toURI();
        client.getDeliveryOptions().setProductionEndpoint(testServerUri);

        ArticleItem itemObj = client.getItem("on_roasts", ArticleItem.class)
                .toCompletableFuture()
                .get();
        Assert.assertNotNull(itemObj);
        Assert.assertNotNull(itemObj.getRelatedArticles());
        Assert.assertEquals(2, itemObj.getRelatedArticles().size());
        //The following assertion will probably have a warning in an IDE, but because reflection is being used, is
        //ensures that this actually is a ContentItem and not a LinkedItem
        Assert.assertTrue(itemObj.getRelatedArticles().get(0) instanceof ContentItem);

        Assert.assertNotNull(itemObj.getRelatedArticlesMap());
        Assert.assertEquals(2, itemObj.getRelatedArticlesMap().size());
        Assert.assertTrue(itemObj.getRelatedArticlesMap().get("coffee_processing_techniques") instanceof ContentItem);
    }


    @Test
    public void testGetStronglyTypedItemByClasspathScan() throws Exception {
        String projectId = "02a70003-e864-464e-b62c-e0ede97deb8c";

        this.serverBootstrap.registerHandler(
                String.format("/%s/%s", projectId, "items/on_roasts"),
                (request, response, context) -> response.setEntity(
                        new InputStreamEntity(
                                this.getClass().getResourceAsStream("SampleContentItem.json")
                        )
                ));
        HttpHost httpHost = this.start();
        DeliveryClient client = new DeliveryClient(projectId);
        client.scanClasspathForMappings("kentico.kontent");

        String testServerUri = httpHost.toURI();
        client.getDeliveryOptions().setProductionEndpoint(testServerUri);

        Object itemObj = client.getItem("on_roasts", Object.class)
                .toCompletableFuture()
                .get();
        Assert.assertNotNull(itemObj);
        Assert.assertTrue(itemObj instanceof ArticleItem);
    }

    @Test
    public void testGetStronglyTypedItems() throws Exception {
        String projectId = "02a70003-e864-464e-b62c-e0ede97deb8c";

        this.serverBootstrap.registerHandler(
                String.format("/%s/%s", projectId, "items"),
                (request, response, context) -> {
                    String uri = String.format("http://testserver%s", request.getRequestLine().getUri());

                    List<NameValuePair> nameValuePairs =
                            URLEncodedUtils.parse(URI.create(uri), Charset.defaultCharset());
                    Map<String, String> params = convertNameValuePairsToMap(nameValuePairs);

                    Assert.assertTrue(params.containsKey("system.type"));
                    Assert.assertEquals("article", params.get("system.type"));

                    response.setEntity(
                            new InputStreamEntity(
                                    this.getClass().getResourceAsStream("SampleContentItemList.json")
                            )
                    );
                });
        HttpHost httpHost = this.start();
        DeliveryClient client = new DeliveryClient(projectId);

        String testServerUri = httpHost.toURI();
        client.getDeliveryOptions().setProductionEndpoint(testServerUri);
        client.registerType(ArticleItem.class);
        client.registerInlineContentItemsResolver(new InlineContentItemsResolver<ArticleItem>() {
            @Override
            public String resolve(ArticleItem data) {
                return "WE REPLACED SUCCESSFULLY";
            }
        });

        List<ArticleItem> items = client.getItems(ArticleItem.class)
                .toCompletableFuture()
                .get();
        Assert.assertNotNull(items);
        Assert.assertFalse(items.get(1).getDescription().contains("<object"));
        Assert.assertTrue(items.get(1).getDescription().contains("WE REPLACED SUCCESSFULLY"));
    }

    @Test
    public void testGetProductionKey() throws Exception {
        String projectId = "02a70003-e864-464e-b62c-e0ede97deb8c";
        String productionApiKey = "production_api_key";

        this.serverBootstrap.registerHandler(
                String.format("/%s/%s", projectId, "items/on_roasts"),
                (request, response, context) -> {
                    Assert.assertEquals(
                            "Bearer production_api_key",
                            request.getHeaders("Authorization")[0].getValue());
                    response.setEntity(
                            new InputStreamEntity(
                                    this.getClass().getResourceAsStream("SampleContentItem.json")
                            )
                    );
                });
        HttpHost httpHost = this.start();
        String testServerUri = httpHost.toURI();

        DeliveryOptions options = new DeliveryOptions();
        options.setProjectId(projectId);
        options.setProductionEndpoint(testServerUri);
        options.setProductionApiKey(productionApiKey);

        DeliveryClient client = new DeliveryClient(options);

        ContentItemResponse item = client.getItem("on_roasts")
                .toCompletableFuture()
                .get();
        Assert.assertNotNull(item);
    }

    @Test
    public void testGetPreviewItem() throws Exception {
        String projectId = "02a70003-e864-464e-b62c-e0ede97deb8c";
        String previewApiKey = "preview_api_key";

        this.serverBootstrap.registerHandler(
                String.format("/%s/%s", projectId, "items/on_roasts"),
                (request, response, context) -> {
                    Assert.assertEquals(
                            "Bearer preview_api_key",
                            request.getHeaders("Authorization")[0].getValue());
                    response.setEntity(
                            new InputStreamEntity(
                                    this.getClass().getResourceAsStream("SampleContentItem.json")
                            )
                    );
                });
        HttpHost httpHost = this.start();
        DeliveryClient client = new DeliveryClient(projectId, previewApiKey);

        String testServerUri = httpHost.toURI();
        client.getDeliveryOptions().setPreviewEndpoint(testServerUri);

        ContentItemResponse item = client.getItem("on_roasts")
                .toCompletableFuture()
                .get();
        Assert.assertNotNull(item);
    }

    @Test
    public void testWaitForLoadingNewContentHeader() throws Exception {
        String projectId = "02a70003-e864-464e-b62c-e0ede97deb8c";

        this.serverBootstrap.registerHandler(
                String.format("/%s/%s", projectId, "items/on_roasts"),
                (request, response, context) -> {
                    Assert.assertEquals(
                            "true",
                            request.getHeaders("X-KC-Wait-For-Loading-New-Content")[0].getValue());
                    response.setEntity(
                            new InputStreamEntity(
                                    this.getClass().getResourceAsStream("SampleContentItem.json")
                            )
                    );
                });
        HttpHost httpHost = this.start();
        String testServerUri = httpHost.toURI();
        DeliveryOptions deliveryOptions = new DeliveryOptions();
        deliveryOptions.setProjectId(projectId);
        deliveryOptions.setProductionEndpoint(testServerUri);
        deliveryOptions.setWaitForLoadingNewContent(true);
        DeliveryClient client = new DeliveryClient(deliveryOptions);

        ContentItemResponse item = client.getItem("on_roasts")
                .toCompletableFuture()
                .get();
        Assert.assertNotNull(item);
    }


    @Test
    @SuppressWarnings("all")
    public void testKenticoException() throws Exception {
        String projectId = "02a70003-e864-464e-b62c-e0ede97deb8c";

        this.serverBootstrap.registerHandler(
                String.format("/%s/%s", projectId, "items/error"),
                (request, response, context) -> {
                    response.setStatusCode(404);
                    response.setEntity(
                            new InputStreamEntity(
                                    this.getClass().getResourceAsStream("SampleKenticoError.json")
                            )
                    );
                });
        HttpHost httpHost = this.start();
        DeliveryClient client = new DeliveryClient(projectId);

        String testServerUri = httpHost.toURI();
        client.getDeliveryOptions().setProductionEndpoint(testServerUri);

        try {
            ContentItemResponse item = client.getItem("error")
                    .toCompletableFuture()
                    .get();
            Assert.fail("Expected KenticoErrorException");
        } catch (ExecutionException e) {
            Assert.assertTrue(e.getCause() instanceof KenticoErrorException);
            KenticoErrorException kenticoErrorException = (KenticoErrorException) e.getCause();
            Assert.assertEquals("The requested content item 'error' was not found.", kenticoErrorException.getMessage());
            Assert.assertEquals("The requested content item 'error' was not found.", kenticoErrorException.getKenticoError().getMessage());
        }
    }


    @Test
    @SuppressWarnings("all")
    public void testKentico500Exception() throws Exception {
        String projectId = "02a70003-e864-464e-b62c-e0ede97deb8c";

        this.serverBootstrap.registerHandler(
                String.format("/%s/%s", projectId, "items/error"),
                (request, response, context) -> response.setStatusCode(500));
        HttpHost httpHost = this.start();
        DeliveryClient client = new DeliveryClient(projectId);

        String testServerUri = httpHost.toURI();
        client.getDeliveryOptions().setProductionEndpoint(testServerUri);

        try {
            ContentItemResponse item = client.getItem("error")
                    .toCompletableFuture()
                    .get();
            Assert.fail("Expected IOException");
        } catch (ExecutionException e) {
            Assert.assertTrue(e.getCause() instanceof KenticoRetryException);
            Assert.assertTrue(e.getCause().getCause() instanceof KenticoIOException);
            Assert.assertEquals("Retry attempty reached max retry attempts (3) ", e.getCause().getMessage());
            Assert.assertEquals("Kentico API retry status returned: 500 (one of [408, 429, 500, 502, 503, 504])", e.getCause().getCause().getMessage());
        }
    }

    @Test
    public void testGetTypesWithParams() throws Exception {
        String projectId = "02a70003-e864-464e-b62c-e0ede97deb8c";

        this.serverBootstrap.registerHandler(
                String.format("/%s/%s", projectId, "types"),
                (request, response, context) -> {
                    String uri = String.format("http://testserver%s", request.getRequestLine().getUri());

                    List<NameValuePair> nameValuePairs =
                            URLEncodedUtils.parse(URI.create(uri), Charset.defaultCharset());
                    Map<String, String> params = convertNameValuePairsToMap(nameValuePairs);
                    Assert.assertEquals(1, params.size());
                    Assert.assertEquals("/path1/path2/test-article", params.get("elements.url_pattern"));
                    response.setEntity(
                            new InputStreamEntity(
                                    this.getClass().getResourceAsStream("SampleContentTypeList.json")
                            )
                    );
                });
        HttpHost httpHost = this.start();
        String testServerUri = httpHost.toURI();
        DeliveryOptions deliveryOptions = new DeliveryOptions();
        deliveryOptions.setProjectId(projectId);
        deliveryOptions.setProductionEndpoint(testServerUri);

        DeliveryClient client = new DeliveryClient(deliveryOptions);
        client.setContentLinkUrlResolver(Link::getUrlSlug);

        List<kentico.kontent.delivery.NameValuePair> urlPattern = DeliveryParameterBuilder.params().filterEquals("elements.url_pattern", "/path1/path2/test-article").build();
        ContentTypesListingResponse types = client.getTypes(urlPattern)
                .toCompletableFuture()
                .get();

        Assert.assertNotNull(types);
    }


    @Test
    public void testGetTypes() throws Exception {
        String projectId = "02a70003-e864-464e-b62c-e0ede97deb8c";

        this.serverBootstrap.registerHandler(
                String.format("/%s/%s", projectId, "types"),
                (request, response, context) -> response.setEntity(
                        new InputStreamEntity(
                                this.getClass().getResourceAsStream("SampleContentTypeList.json")
                        )
                ));
        HttpHost httpHost = this.start();
        DeliveryClient client = new DeliveryClient(projectId);

        String testServerUri = httpHost.toURI();
        client.getDeliveryOptions().setProductionEndpoint(testServerUri);

        ContentTypesListingResponse types = client.getTypes()
                .toCompletableFuture()
                .get();
        Assert.assertNotNull(types);
    }

    @Test
    @SuppressWarnings("all")
    public void testGetType() throws Exception {
        String projectId = "02a70003-e864-464e-b62c-e0ede97deb8c";

        this.serverBootstrap.registerHandler(
                String.format("/%s/%s", projectId, "types/coffee"),
                (request, response, context) -> response.setEntity(
                        new InputStreamEntity(
                                this.getClass().getResourceAsStream("SampleContentType.json")
                        )
                ));
        HttpHost httpHost = this.start();
        DeliveryClient client = new DeliveryClient(projectId);

        String testServerUri = httpHost.toURI();
        client.getDeliveryOptions().setProductionEndpoint(testServerUri);

        ContentType type = client.getType("coffee")
                .toCompletableFuture()
                .get();
        Assert.assertNotNull(type);
    }

    @Test
    public void testGetTypeElementWithParams() throws Exception {
        String projectId = "02a70003-e864-464e-b62c-e0ede97deb8c";

        this.serverBootstrap.registerHandler(
                String.format("/%s/%s", projectId, "types/coffee/elements/processing"),
                (request, response, context) -> {
                    String uri = String.format("http://testserver%s", request.getRequestLine().getUri());

                    List<NameValuePair> nameValuePairs =
                            URLEncodedUtils.parse(URI.create(uri), Charset.defaultCharset());
                    Map<String, String> params = convertNameValuePairsToMap(nameValuePairs);
                    Assert.assertEquals(1, params.size());
                    Assert.assertEquals("/path1/path2/test-article", params.get("elements.url_pattern"));
                    response.setEntity(
                            new InputStreamEntity(
                                    this.getClass().getResourceAsStream("SampleContentTypeElementResponse.json")
                            )
                    );
                });
        HttpHost httpHost = this.start();
        String testServerUri = httpHost.toURI();
        DeliveryOptions deliveryOptions = new DeliveryOptions();
        deliveryOptions.setProjectId(projectId);
        deliveryOptions.setProductionEndpoint(testServerUri);

        DeliveryClient client = new DeliveryClient(deliveryOptions);
        client.setContentLinkUrlResolver(Link::getUrlSlug);

        List<kentico.kontent.delivery.NameValuePair> urlPattern = DeliveryParameterBuilder.params().filterEquals("elements.url_pattern", "/path1/path2/test-article").build();
        Element element = client.getContentTypeElement("coffee", "processing", urlPattern)
                .toCompletableFuture()
                .get();

        Assert.assertNotNull(element);
        Assert.assertEquals("processing", element.getCodeName());
        Assert.assertTrue(element instanceof MultipleChoiceElement);
    }

    @Test
    @SuppressWarnings("all")
    public void testGetTypeElement() throws Exception {
        String projectId = "02a70003-e864-464e-b62c-e0ede97deb8c";

        this.serverBootstrap.registerHandler(
                String.format("/%s/%s", projectId, "types/coffee/elements/processing"),
                (request, response, context) -> response.setEntity(
                        new InputStreamEntity(
                                this.getClass().getResourceAsStream("SampleContentTypeElementResponse.json")
                        )
                ));
        HttpHost httpHost = this.start();
        DeliveryClient client = new DeliveryClient(projectId);

        String testServerUri = httpHost.toURI();
        client.getDeliveryOptions().setProductionEndpoint(testServerUri);

        Element element = client.getContentTypeElement("coffee", "processing")
                .toCompletableFuture()
                .get();
        Assert.assertNotNull(element);
        Assert.assertEquals("processing", element.getCodeName());
        Assert.assertTrue(element instanceof MultipleChoiceElement);
    }

    @Test
    @SuppressWarnings("all")
    public void testExceptionWhenProjectIdIsNull() {
        DeliveryOptions deliveryOptions = new DeliveryOptions();
        try {
            DeliveryClient client = new DeliveryClient(deliveryOptions);
            Assert.fail("Expected IllegalArgumentException due to null Project Id");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("Kentico Kontent project identifier is not specified.", e.getMessage());
        }
    }

    @Test
    @SuppressWarnings("all")
    public void testExceptionWhenProjectIdIsEmpty() {
        DeliveryOptions deliveryOptions = new DeliveryOptions();
        deliveryOptions.setProjectId("");
        try {
            DeliveryClient client = new DeliveryClient(deliveryOptions);
            Assert.fail("Expected IllegalArgumentException due to empty Project Id");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("Kentico Kontent project identifier is not specified.", e.getMessage());
        }
    }

    @Test
    @SuppressWarnings("all")
    public void testExceptionWhenDeliveryOptionsIsNull() {
        DeliveryOptions deliveryOptions = null;
        try {
            DeliveryClient client = new DeliveryClient(deliveryOptions);
            Assert.fail("Expected IllegalArgumentException due to null Delivery options");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("The Delivery options object is not specified.", e.getMessage());
        }
    }

    @Test
    @SuppressWarnings("all")
    public void testExceptionWhenInvalidProjectIdProvided() {
        DeliveryOptions deliveryOptions = new DeliveryOptions();
        deliveryOptions.setProjectId("Invalid GUID");
        try {
            DeliveryClient client = new DeliveryClient(deliveryOptions);
            Assert.fail("Expected IllegalArgumentException due to invalid Project ID");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("Provided string is not a valid project identifier (Invalid GUID).  Have you accidentally passed the Preview API key instead of the project identifier?", e.getMessage());
        }
    }

    @Test
    @SuppressWarnings("all")
    public void testExceptionWhenNullPreviewApiKeyProvided() {
        DeliveryOptions deliveryOptions = new DeliveryOptions();
        deliveryOptions.setProjectId("02a70003-e864-464e-b62c-e0ede97deb8c");
        deliveryOptions.setUsePreviewApi(true);
        try {
            DeliveryClient client = new DeliveryClient(deliveryOptions);
            Assert.fail("Expected IllegalArgumentException due to null Preview API Key");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("The Preview API key is not specified.", e.getMessage());
        }
    }

    @Test
    @SuppressWarnings("all")
    public void testExceptionWhenEmptyPreviewApiKeyProvided() {
        DeliveryOptions deliveryOptions = new DeliveryOptions();
        deliveryOptions.setProjectId("02a70003-e864-464e-b62c-e0ede97deb8c");
        deliveryOptions.setUsePreviewApi(true);
        deliveryOptions.setPreviewApiKey("");
        try {
            DeliveryClient client = new DeliveryClient(deliveryOptions);
            Assert.fail("Expected IllegalArgumentException due to empty Preview API Key");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("The Preview API key is not specified.", e.getMessage());
        }
    }

    @Test
    @SuppressWarnings("all")
    public void testExceptionWhenBothProductionApiKeyAndPreviewApiKeyProvided() {
        DeliveryOptions deliveryOptions =
                new DeliveryOptions("02a70003-e864-464e-b62c-e0ede97deb8c", "preview_api_key");
        deliveryOptions.setProductionApiKey("production_api_key");
        try {
            DeliveryClient client = new DeliveryClient(deliveryOptions);
            Assert.fail("Expected IllegalArgumentException due to providing both a preview and production API key");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("Cannot provide both a preview API key and a production API key.", e.getMessage());
        }
    }

    @Test
    @SuppressWarnings("all")
    public void testExceptionWhenRetryCountIsLessThanZero() {
        DeliveryOptions deliveryOptions = new DeliveryOptions();
        deliveryOptions.setProjectId("02a70003-e864-464e-b62c-e0ede97deb8c");
        deliveryOptions.setRetryAttempts(-1);
        try {
            DeliveryClient client = new DeliveryClient(deliveryOptions);
            Assert.fail("Expected IllegalArgumentException due to negative number provided for retry count");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("Cannot retry connections less than 0 times.", e.getMessage());
        }
    }

    @Test
    public void testRichTextWithTableFoesNotFail() throws Exception {
        final String projectId = "02a70003-e864-464e-b62c-e0ede97deb8c";

        this.serverBootstrap.registerHandler(
                String.format("/%s/%s", projectId, "items/test_tables"),
                (request, response, context) -> response.setEntity(
                        new InputStreamEntity(
                                this.getClass().getResourceAsStream("SampleKontentItemWithComplexTablesRichText.json")
                        )
                ));
        final HttpHost httpHost = this.start();
        final DeliveryClient client = new DeliveryClient(projectId);

        final String testServerUri = httpHost.toURI();
        client.getDeliveryOptions().setProductionEndpoint(testServerUri);

        final ContentItemResponse response = client.getItem("test_tables")
                .toCompletableFuture()
                .get();
        Assert.assertEquals(
                "<p>Hello world</p>\n<figure data-asset-id=\"bf509e0d-d9ed-4925-968e-29a1f6a561c4\" data-image-id=\"bf509e0d-d9ed-4925-968e-29a1f6a561c4\"><img src=\"https://qa-assets-us-01.global.ssl.fastly.net:443/34aaa010-a788-0004-ce89-a1c49a062a65/625c11f0-9783-47d3-b80e-2afafb8862e3/pikachu.jpg\" data-asset-id=\"bf509e0d-d9ed-4925-968e-29a1f6a561c4\" data-image-id=\"bf509e0d-d9ed-4925-968e-29a1f6a561c4\" alt=\"\"></figure>\n<p>Hello <a data-item-id=\"3ce384e6-ba4b-49c4-993a-ae4ee1e0a1cc\" href=\"\">world</a></p>\n<p><a data-item-id=\"5e1997a2-9f9b-43ba-92f3-3cb36409d811\" href=\"\">Hello </a>world</p>\n<table><tbody>\n  <tr><td><h1>Beautiful table on steroids</h1>\n<h2>Which was changed BTW</h2>\n<p>Supports</p>\n<ul>\n  <li>Lists</li>\n  <li><strong>Formatting</strong></li>\n  <li>Images\n    <ol>\n      <li>Yes, <a data-item-id=\"4ffc70ea-62f6-4726-a5bf-39896d7c91c4\" href=\"\">totally</a></li>\n      <li>Really</li>\n      <li><a data-item-id=\"3120ec15-a4a2-47ec-8ccd-c85ac8ac5ba5\" href=\"\">Wanna </a>see?</li>\n    </ol>\n  </li>\n</ul>\n<figure data-asset-id=\"a0c1b647-e0b1-478a-b7bd-8df80b55e9f7\" data-image-id=\"a0c1b647-e0b1-478a-b7bd-8df80b55e9f7\"><img src=\"https://qa-assets-us-01.global.ssl.fastly.net:443/34aaa010-a788-0004-ce89-a1c49a062a65/d74603bb-2109-4d45-885d-ff15c6ed9582/likeaboss.jpg\" data-asset-id=\"a0c1b647-e0b1-478a-b7bd-8df80b55e9f7\" data-image-id=\"a0c1b647-e0b1-478a-b7bd-8df80b55e9f7\" alt=\"\"></figure>\n<p><em>Thanks for watching!</em></p>\n</td><td>with</td><td>some</td></tr>\n  <tr><td>text</td><td>in</td><td>various</td></tr>\n  <tr><td>table</td><td>cells</td><td>!</td></tr>\n</tbody></table>\n<p>z</p>\n<p>dd</p>\n<table><tbody>\n  <tr><td>d</td><td>f</td><td>g</td></tr>\n  <tr><td>g</td><td>g</td><td>gg</td></tr>\n  <tr><td>g</td><td>g</td><td>g</td></tr>\n</tbody></table>\n<p>x</p>",
                response.item.elements.get("rich_text").getValue()
        );
    }

    @Test
    public void testRichTextWithEmptyValueNotFail() throws Exception {
        final String projectId = "02a70003-e864-464e-b62c-e0ede97deb8c";

        this.serverBootstrap.registerHandler(
                String.format("/%s/%s", projectId, "items/test_empty_value"),
                (request, response, context) -> response.setEntity(
                        new InputStreamEntity(
                                this.getClass().getResourceAsStream("SampleKontentItemWithComplexEmptyValueRichText.json")
                        )
                ));
        final HttpHost httpHost = this.start();
        final DeliveryClient client = new DeliveryClient(projectId);

        final String testServerUri = httpHost.toURI();
        client.getDeliveryOptions().setProductionEndpoint(testServerUri);

        final ContentItemResponse response = client.getItem("test_empty_value")
                .toCompletableFuture()
                .get();
        Assert.assertEquals(
                "",
                response.item.elements.get("empty_value").getValue()
        );
        Assert.assertEquals(
                "<p><br></p>",
                response.item.elements.get("no_content").getValue()
        );
    }

    private Map<String, String> convertNameValuePairsToMap(List<NameValuePair> nameValuePairs) {
        HashMap<String, String> map = new HashMap<>();
        for (NameValuePair nameValuePair : nameValuePairs) {
            map.put(nameValuePair.getName(), nameValuePair.getValue());
        }
        return map;
    }
}
