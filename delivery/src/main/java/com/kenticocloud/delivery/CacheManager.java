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

import java.io.IOException;

/**
 * Interface to provide caching to the {@link DeliveryClient}.
 * <p>
 * An implementation of this can be provided via {@link DeliveryClient#setCacheManager(CacheManager)} and it is invoked
 * before every API request.  It is expected to return a {@link JsonNode} representation of the API response.  In the
 * case of a cache miss, the {@link HttpRequestExecutor} provides an {@code .execute()} callback which returns JsonNode
 * from the KenticoCloud endpoint.  This JsonNode can then be stored by the CacheManager, and returned per the contract.
 * <p>
 * It is up to the CacheManager to determine how to key it's cache, and how much it will introspect the JsonNode,
 * however due to varying operations that can be done with the {@link DeliveryParameterBuilder}, it is highly
 * recommended to key off the {@code requestUri}.
 * <p>
 * A CacheManager also can leverage notifications from KenticoCloud's webhooks that can be sent when the project's
 * content is changed.
 * <p>
 * This is a {@link FunctionalInterface} to simplify implementation.
 * <p>
 * A simple implementation of this would be as follows (although this example isn't thread safe, do not use):
 * <pre>{@code
 * Map<String, JsonNode> memoryCache = new HashMap<>();
 * deliveryClient.setCacheManager((requestUri, executor) -> {
 *         if (memoryMap.containsKey(requestUri)){
 *             return memoryMap.get(requestUri);
 *         } else {
 *             JsonNode jsonNode = executor.execute();
 *             memoryMap.put(requestUri, jsonNode);
 *             return jsonNode;
 *         }
 *     }
 * );
 * ...
 * protected void purgeCache() {
 *     memoryCache = new HashMap<>();
 * }
 * }</pre>
 * <p>
 * By default, if no CacheManager is provided, the DeliveryClient will use it's default, which just passes through:
 * <pre>{@code
 * private CacheManager cacheManager = (requestUri, executor) -> executor.execute();
 * }</pre>
 *
 * @see DeliveryClient#setCacheManager(CacheManager)
 * @see HttpRequestExecutor
 * @see <a href="https://developer.kenticocloud.com/v1/reference#webhooks-and-notifications">
 *      KenticoCloud API reference - Webhooks and notifications</a>
 */
@FunctionalInterface
public interface CacheManager {

    /**
     * Invocation of this is a request for the {@link JsonNode} representation of the KenticoCloud response the the API
     * requestUri built by the SDK.  If this implementation cannot respond, it is expected to acquire the JsonNode from
     * the {@link HttpRequestExecutor} and return it's response.
     *
     * @param requestUri    The requestUri of the KenticoCloud Delivery API request.
     * @param executor      A callback to execute the API request against the KenticoCloud endpoint.
     * @return              JsonNode response to the API request.
     * @throws              IOException This can be thrown by the HttpRequestExecutor indicating a problem with the
     *                      service, or worse case scenario by the CacheManager itself.
     */
    JsonNode resolveRequest(String requestUri, HttpRequestExecutor executor) throws IOException;
}
