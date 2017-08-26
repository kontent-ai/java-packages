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

import org.apache.http.*;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.localserver.LocalServerTestBase;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeliveryClientTest extends LocalServerTestBase {

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
                    Assert.assertEquals("/path1/path2/test-article",params.get("elements.url_pattern"));
                    response.setEntity(
                            new InputStreamEntity(
                                    this.getClass().getResourceAsStream("SampleContentList.json")
                            )
                    );
                });
        HttpHost httpHost = this.start();
        String testServerUri = httpHost.toURI() + "/%s";
        DeliveryOptions deliveryOptions = new DeliveryOptions();
        deliveryOptions.setProjectId(projectId);
        deliveryOptions.setProductionEndpoint(testServerUri);

        DeliveryClient client = new DeliveryClient(deliveryOptions);

        List<NameValuePair> urlPattern = DeliveryParameterBuilder.params().filterEquals("elements.url_pattern", "/path1/path2/test-article").build();
        ContentItemsListingResponse items = client.getItems(urlPattern);

        Assert.assertNotNull(items);
    }

    @Test
    public void testExceptionWhenProjectIdIsNull() {
        DeliveryOptions deliveryOptions = new DeliveryOptions();
        try {
            DeliveryClient client = new DeliveryClient(deliveryOptions);
            Assert.fail("Expected IllegalArgumentException due to null Project Id");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("Kentico Cloud project identifier is not specified.", e.getMessage());
        }
    }

    @Test
    public void testExceptionWhenProjectIdIsEmpty() {
        DeliveryOptions deliveryOptions = new DeliveryOptions();
        deliveryOptions.setProjectId("");
        try {
            DeliveryClient client = new DeliveryClient(deliveryOptions);
            Assert.fail("Expected IllegalArgumentException due to empty Project Id");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("Kentico Cloud project identifier is not specified.", e.getMessage());
        }
    }

    @Test
    public void testExceptionWhenDeliveryOptionsIsNull() {
        try {
            DeliveryOptions deliveryOptions = null;
            DeliveryClient client = new DeliveryClient(deliveryOptions);
            Assert.fail("Expected IllegalArgumentException due to null Delivery options");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("The Delivery options object is not specified.", e.getMessage());
        }
    }

    @Test
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

    private Map<String, String> convertNameValuePairsToMap(List<NameValuePair> nameValuePairs) {
        HashMap<String, String> map = new HashMap<>();
        for (NameValuePair nameValuePair : nameValuePairs) {
            map.put(nameValuePair.getName(), nameValuePair.getValue());
        }
        return map;
    }

}
