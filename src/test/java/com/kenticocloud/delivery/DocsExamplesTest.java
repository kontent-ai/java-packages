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
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.localserver.LocalServerTestBase;
import org.junit.Assert;
import org.junit.Test;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.lang.reflect.Field;
import java.util.List;

public class DocsExamplesTest extends LocalServerTestBase {

    @Test
    public void testRetrievingContentInSpecificLanguage() throws Exception {
        String projectId = "02a70003-e864-464e-b62c-e0ede97deb8c";

        this.serverBootstrap.registerHandler(
                String.format("/%s/%s", projectId, "items/on_roasts"),
                (request, response, context) -> response.setEntity(
                        new InputStreamEntity(
                                this.getClass().getResourceAsStream("DocsExamplesRetrievingContentInSpecificLanguage.json")
                        )
                ));
        HttpHost httpHost = this.start();
        DeliveryClient client = new DeliveryClient(projectId);

        //modify default baseurl to point to test server, this is private so using reflection
        String testServerUri = httpHost.toURI() + "/%s";
        Field deliveryOptionsField = client.getClass().getDeclaredField("deliveryOptions");
        deliveryOptionsField.setAccessible(true);
        ((DeliveryOptions) deliveryOptionsField.get(client)).setProductionEndpoint(testServerUri);

        List<NameValuePair> params = DeliveryParameterBuilder.params().language("es-ES").build();

        ArticleItem item = client.getItem("on_roasts", ArticleItem.class, params);
        Assert.assertNotNull(item);
    }

    @Test
    public void testListContentItems() throws Exception {
        String projectId = "02a70003-e864-464e-b62c-e0ede97deb8c";

        this.serverBootstrap.registerHandler(
                String.format("/%s/%s", projectId, "items"),
                (request, response, context) -> response.setEntity(
                        new InputStreamEntity(
                                this.getClass().getResourceAsStream("DocsExamplesListContentItems.json")
                        )
                ));
        HttpHost httpHost = this.start();
        DeliveryClient client = new DeliveryClient(projectId);

        //modify default baseurl to point to test server, this is private so using reflection
        String testServerUri = httpHost.toURI() + "/%s";
        Field deliveryOptionsField = client.getClass().getDeclaredField("deliveryOptions");
        deliveryOptionsField.setAccessible(true);
        ((DeliveryOptions) deliveryOptionsField.get(client)).setProductionEndpoint(testServerUri);

        List<NameValuePair> params = DeliveryParameterBuilder.params()
            .filterEquals("system.type", "article")
            .projection("title", "summary", "post_date", "teaser_image")
            .filterContains("elements.personas", "coffee_lover")
            .orderByDesc("elements.post_date")
            .modularContentDepth(0)
            .page(null, 3)
            .build();

        List<ArticleItem> items = client.getItems(ArticleItem.class);
        Assert.assertNotNull(items);
    }

    @Test
    public void testViewContentItem() throws Exception {
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

        //modify default baseurl to point to test server, this is private so using reflection
        String testServerUri = httpHost.toURI() + "/%s";
        Field deliveryOptionsField = client.getClass().getDeclaredField("deliveryOptions");
        deliveryOptionsField.setAccessible(true);
        ((DeliveryOptions) deliveryOptionsField.get(client)).setProductionEndpoint(testServerUri);

        List<NameValuePair> params = DeliveryParameterBuilder.params().projection("title", "summary", "post_date", "teaser_image", "related_articles").build();

        ArticleItem item = client.getItem("on_roasts", ArticleItem.class, params);
        Assert.assertNotNull(item);
    }

    @Test
    public void testListContentTypes() throws Exception {
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

        //modify default baseurl to point to test server, this is private so using reflection
        String testServerUri = httpHost.toURI() + "/%s";
        Field deliveryOptionsField = client.getClass().getDeclaredField("deliveryOptions");
        deliveryOptionsField.setAccessible(true);
        ((DeliveryOptions) deliveryOptionsField.get(client)).setProductionEndpoint(testServerUri);

        List<NameValuePair> params = DeliveryParameterBuilder.params().page(null, 3).build();
        ContentTypesListingResponse types = client.getTypes(params);

        Assert.assertNotNull(types);
    }

    @Test
    public void testViewContentType() throws Exception {
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

        //modify default baseurl to point to test server, this is private so using reflection
        String testServerUri = httpHost.toURI() + "/%s";
        Field deliveryOptionsField = client.getClass().getDeclaredField("deliveryOptions");
        deliveryOptionsField.setAccessible(true);
        ((DeliveryOptions) deliveryOptionsField.get(client)).setProductionEndpoint(testServerUri);

        ContentType type = client.getType("coffee");
        Assert.assertNotNull(type);
    }

    @Test
    public void testViewContentTypeElement() throws Exception {
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

        //modify default baseurl to point to test server, this is private so using reflection
        String testServerUri = httpHost.toURI() + "/%s";
        Field deliveryOptionsField = client.getClass().getDeclaredField("deliveryOptions");
        deliveryOptionsField.setAccessible(true);
        ((DeliveryOptions) deliveryOptionsField.get(client)).setProductionEndpoint(testServerUri);

        Element element = client.getContentTypeElement("coffee", "processing");
        Assert.assertNotNull(element);
        Assert.assertEquals("processing", element.getCodeName());
        Assert.assertTrue(element instanceof MultipleChoiceElement);
    }

    @Test
    public void testListTaxonomyGroups() throws Exception {
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

        //modify default baseurl to point to test server, this is private so using reflection
        String testServerUri = httpHost.toURI() + "/%s";
        Field deliveryOptionsField = client.getClass().getDeclaredField("deliveryOptions");
        deliveryOptionsField.setAccessible(true);
        ((DeliveryOptions) deliveryOptionsField.get(client)).setProductionEndpoint(testServerUri);

        List<NameValuePair> params = DeliveryParameterBuilder.params().page(null, 3).build();

        TaxonomyGroupListingResponse response = client.getTaxonomyGroups(params);
        Assert.assertNotNull(response);
        Assert.assertEquals(3, response.taxonomies.size());
    }

    @Test
    public void testViewTaxonomyGroup() throws Exception {
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

        //modify default baseurl to point to test server, this is private so using reflection
        String testServerUri = httpHost.toURI() + "/%s";
        Field deliveryOptionsField = client.getClass().getDeclaredField("deliveryOptions");
        deliveryOptionsField.setAccessible(true);
        ((DeliveryOptions) deliveryOptionsField.get(client)).setProductionEndpoint(testServerUri);

        TaxonomyGroup taxonomyGroup = client.getTaxonomyGroup("personas");
        Assert.assertNotNull(taxonomyGroup);
    }

    @Test
    public void testNotificationSignatures() throws Exception {
        String message = "The quick brown fox jumps over the lazy dog.";
        String secret = "3mlfw0i7LGSI+/n/cFd3I9cnLLr4e0ttUzKxwMqZTVU=";
        String expectedBase64EncodedHmac = "36883ecb7b56cca6153564e3e943677f9d2d74caab090f4bc5d2f2c626e7ecbc";
        String actualHmac = generateHash(message, secret);

        Assert.assertEquals(expectedBase64EncodedHmac, actualHmac);
    }

    public static String generateHash(String message, String secret) throws Exception {
        Mac sha256Hmac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes("UTF-8"), "HmacSHA256");
        sha256Hmac.init(secretKeySpec);

        return DatatypeConverter.printHexBinary(sha256Hmac.doFinal(message.getBytes("UTF-8"))).toLowerCase();
    }
}
