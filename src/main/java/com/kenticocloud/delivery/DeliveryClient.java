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

import com.fasterxml.jackson.databind.ObjectMapper;
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

public class DeliveryClient {

    static final String ITEMS = "items";
    static final String TYPES = "types";
    static final String ELEMENTS = "elements";

    private String baseUrl;
    private String apiKey;

    private String projectId;
    private ObjectMapper objectMapper = new ObjectMapper();
    private HttpClient httpClient;

    public DeliveryClient(String url, String projectId) {
        this.baseUrl = url;
        this.projectId = projectId;

        httpClient = HttpClients.createDefault();
    }

    public DeliveryClient(String url, String projectId, String apiKey) {
        this (url, projectId);
        setApiKey(apiKey);
    }

    public ContentItemsListingResponse getItems() throws IOException {
        return getItems(new ArrayList<>());
    }

    public ContentItemsListingResponse getItems(List<NameValuePair> params) throws IOException {
        HttpUriRequest request = buildGetRequest(String.format("%s/%s", projectId, ITEMS), params);

        HttpResponse response = httpClient.execute(request);

        handleErrorIfNecessary(response);

        return objectMapper.readValue(response.getEntity().getContent(), ContentItemsListingResponse.class);
    }

    public ContentItemResponse getItem(String contentItemCodename) throws IOException {
        return getItem(contentItemCodename, new ArrayList<>());
    }

    public ContentItemResponse getItem(String contentItemCodename, List<NameValuePair> params) throws IOException {
        HttpUriRequest request = buildGetRequest(String.format("%s/%s/%s", projectId, ITEMS, contentItemCodename), params);

        HttpResponse response = httpClient.execute(request);

        handleErrorIfNecessary(response);

        return objectMapper.readValue(response.getEntity().getContent(), ContentItemResponse.class);
    }

    public ContentTypesListingResponse getTypes() throws IOException {
        return getTypes(new ArrayList<>());
    }

    public ContentTypesListingResponse getTypes(List<NameValuePair> params) throws IOException {
        HttpUriRequest request = buildGetRequest(String.format("%s/%s", projectId, TYPES), params);

        HttpResponse response = httpClient.execute(request);

        handleErrorIfNecessary(response);

        return objectMapper.readValue(response.getEntity().getContent(), ContentTypesListingResponse.class);
    }

    public ContentType getType(String contentTypeCodeName) throws IOException {
        return getType(contentTypeCodeName, new ArrayList<>());
    }

    public ContentType getType(String contentTypeCodeName, List<NameValuePair> params) throws IOException {
        HttpUriRequest request = buildGetRequest(String.format("%s/%s/%s", projectId, TYPES, contentTypeCodeName), params);

        HttpResponse response = httpClient.execute(request);

        handleErrorIfNecessary(response);

        return objectMapper.readValue(response.getEntity().getContent(), ContentType.class);
    }

    public Element getContentTypeElement(String contentTypeCodeName, String elementCodeName) throws IOException {
        return getContentTypeElement(contentTypeCodeName, elementCodeName, new ArrayList<>());
    }

    public Element getContentTypeElement(String contentTypeCodeName, String elementCodeName, List<NameValuePair> params) throws IOException {
        HttpUriRequest request = buildGetRequest(String.format("%s/%s/%s/%s/%s", projectId, TYPES, contentTypeCodeName, ELEMENTS, elementCodeName), params);

        HttpResponse response = httpClient.execute(request);

        handleErrorIfNecessary(response);

        return objectMapper.readValue(response.getEntity().getContent(), Element.class);
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    protected HttpUriRequest buildGetRequest(String apiCall, List<NameValuePair> nameValuePairs) {
        RequestBuilder requestBuilder = RequestBuilder.get(String.format("%s/%s", baseUrl, apiCall));
        if (apiKey != null) {
            requestBuilder.setHeader(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", apiKey));
        }
        requestBuilder.setHeader(HttpHeaders.ACCEPT, "application/json");
        for (NameValuePair nameValuePair : nameValuePairs) {
            requestBuilder.addParameter(nameValuePair);
        }
        return requestBuilder.build();
    }

    private void handleErrorIfNecessary(HttpResponse response) throws IOException {
        if (response.getStatusLine().getStatusCode() >= 400) {
            KenticoError kenticoError = objectMapper.readValue(response.getEntity().getContent(), KenticoError.class);
            throw new KenticoErrorException(kenticoError);
        } else if (response.getStatusLine().getStatusCode() >= 500) {
            throw new IOException("Unknown error with Kentico API.  Kentico is likely suffering site issues.");
        }
    }
}
