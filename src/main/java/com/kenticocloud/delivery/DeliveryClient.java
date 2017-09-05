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

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Executes requests against the Kentico Cloud Delivery API.
 */
public class DeliveryClient {

    static final String ITEMS = "items";
    static final String TYPES = "types";
    static final String ELEMENTS = "elements";

    private static final String URL_CONCAT = "%s/%s";

    private ObjectMapper objectMapper = new ObjectMapper();
    private HttpClient httpClient;
    private DeliveryOptions deliveryOptions;

    private ContentLinkUrlResolver contentLinkUrlResolver;
    private BrokenLinkUrlResolver brokenLinkUrlResolver;
    private StronglyTypedContentItemConverter stronglyTypedContentItemConverter =
            new StronglyTypedContentItemConverter();

    /**
     * Initializes a new instance of the {@link DeliveryClient} class for retrieving content of the specified project.
     * @throws IllegalArgumentException Thrown if the arguments in the {@link DeliveryOptions} are invalid.
     * @param deliveryOptions The settings of the Kentico Cloud project.
     */
    public DeliveryClient(DeliveryOptions deliveryOptions) {
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
        this.deliveryOptions = deliveryOptions;
        httpClient = HttpClients.createDefault();
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

        HttpResponse response = httpClient.execute(request);

        handleErrorIfNecessary(response);

        return objectMapper.readValue(response.getEntity().getContent(), ContentItemsListingResponse.class);
    }

    public <T> List<T> getItems(Class<T> tClass, List<NameValuePair> params) throws IOException {
        ContentItemsListingResponse contentItemsListingResponse = getItems(params);
        return contentItemsListingResponse.castTo(tClass);
    }

    public ContentItemResponse getItem(String contentItemCodename) throws IOException {
        return getItem(contentItemCodename, new ArrayList<>());
    }

    public <T> List<T> getItems(Class<T> tClass) throws IOException {
        return getItems(tClass, new ArrayList<>());
    }

    public <T> T getItem(String contentItemCodename, Class<T> tClass) throws IOException {
        return getItem(contentItemCodename, tClass, new ArrayList<>());
    }

    public ContentItemResponse getItem(String contentItemCodename, List<NameValuePair> params) throws IOException {
        HttpUriRequest request = buildGetRequest(String.format(URL_CONCAT, ITEMS, contentItemCodename), params);

        HttpResponse response = httpClient.execute(request);

        handleErrorIfNecessary(response);

        return objectMapper.readValue(response.getEntity().getContent(), ContentItemResponse.class);
    }

    public <T> T getItem(String contentItemCodename, Class<T> tClass, List<NameValuePair> params) throws IOException {
        ContentItemResponse contentItemResponse = getItem(contentItemCodename, params);
        return contentItemResponse.castTo(tClass);
    }

    public ContentTypesListingResponse getTypes() throws IOException {
        return getTypes(new ArrayList<>());
    }

    public ContentTypesListingResponse getTypes(List<NameValuePair> params) throws IOException {
        HttpUriRequest request = buildGetRequest(TYPES, params);

        HttpResponse response = httpClient.execute(request);

        handleErrorIfNecessary(response);

        return objectMapper.readValue(response.getEntity().getContent(), ContentTypesListingResponse.class);
    }

    public ContentType getType(String contentTypeCodeName) throws IOException {
        return getType(contentTypeCodeName, new ArrayList<>());
    }

    public ContentType getType(String contentTypeCodeName, List<NameValuePair> params) throws IOException {
        HttpUriRequest request = buildGetRequest(String.format(URL_CONCAT, TYPES, contentTypeCodeName), params);

        HttpResponse response = httpClient.execute(request);

        handleErrorIfNecessary(response);

        return objectMapper.readValue(response.getEntity().getContent(), ContentType.class);
    }

    public Element getContentTypeElement(String contentTypeCodeName, String elementCodeName) throws IOException {
        return getContentTypeElement(contentTypeCodeName, elementCodeName, new ArrayList<>());
    }

    public Element getContentTypeElement(String contentTypeCodeName, String elementCodeName, List<NameValuePair> params)
            throws IOException {
        HttpUriRequest request = buildGetRequest(
                String.format("%s/%s/%s/%s", TYPES, contentTypeCodeName, ELEMENTS, elementCodeName), params);

        HttpResponse response = httpClient.execute(request);

        handleErrorIfNecessary(response);

        return objectMapper.readValue(response.getEntity().getContent(), Element.class);
    }

    public ContentLinkUrlResolver getContentLinkUrlResolver() {
        return contentLinkUrlResolver;
    }

    public void setContentLinkUrlResolver(ContentLinkUrlResolver contentLinkUrlResolver) {
        this.contentLinkUrlResolver = contentLinkUrlResolver;
        reconfigureDeserializer();
    }

    public BrokenLinkUrlResolver getBrokenLinkUrlResolver() {
        return brokenLinkUrlResolver;
    }

    public void setBrokenLinkUrlResolver(BrokenLinkUrlResolver brokenLinkUrlResolver) {
        this.brokenLinkUrlResolver = brokenLinkUrlResolver;
        reconfigureDeserializer();
    }

    protected HttpUriRequest buildGetRequest(String apiCall, List<NameValuePair> nameValuePairs) {
        RequestBuilder requestBuilder = RequestBuilder.get(String.format(URL_CONCAT, getBaseUrl(), apiCall));
        if (deliveryOptions.isUsePreviewApi()) {
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
        for (NameValuePair nameValuePair : nameValuePairs) {
            requestBuilder.addParameter(nameValuePair);
        }
        return requestBuilder.build();
    }

    private String getBaseUrl() {
        if (deliveryOptions.isUsePreviewApi()) {
            return String.format(deliveryOptions.getPreviewEndpoint(), deliveryOptions.getProjectId());
        } else {
            return String.format(deliveryOptions.getProductionEndpoint(), deliveryOptions.getProjectId());
        }
    }

    private void handleErrorIfNecessary(HttpResponse response) throws IOException {
        if (response.getStatusLine().getStatusCode() >= 500) {
            throw new IOException("Unknown error with Kentico API.  Kentico is likely suffering site issues.");
        } else if (response.getStatusLine().getStatusCode() >= 400) {
            KenticoError kenticoError = objectMapper.readValue(response.getEntity().getContent(), KenticoError.class);
            throw new KenticoErrorException(kenticoError);
        }
    }

    private void reconfigureDeserializer() {
        objectMapper = new ObjectMapper();

        SimpleModule module = new SimpleModule();
        objectMapper.registerModule(new JSR310Module());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        module.setDeserializerModifier(new BeanDeserializerModifier() {
            @Override
            public JsonDeserializer<?> modifyDeserializer(DeserializationConfig config, BeanDescription beanDesc, JsonDeserializer<?> deserializer) {
                if (beanDesc.getBeanClass() == RichTextElement.class)
                    return new RichTextElementConverter(
                            getContentLinkUrlResolver(),
                            getBrokenLinkUrlResolver(),
                            stronglyTypedContentItemConverter,
                            deserializer);
                return deserializer;
            }
        });

        objectMapper.registerModule(module);
    }
}
