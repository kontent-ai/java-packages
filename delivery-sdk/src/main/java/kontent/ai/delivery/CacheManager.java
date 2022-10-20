/*
 * MIT License
 *
 * Copyright (c) 2022 Kontent s.r.o.
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

package kontent.ai.delivery;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

/**
 * Interface to provide caching to the {@link DeliveryClient}.
 * <p>
 * An implementation of this can be provided via {@link DeliveryClient#setCacheManager(CacheManager)} and it is invoked
 * before every API request.  It is expected to return a {@link JsonNode} representation of the API response.
 * <p>
 * It is up to the CacheManager to determine how to key it's cache, and how much it will introspect the JsonNode,
 * however due to varying operations that can be done with the {@link DeliveryParameterBuilder}, it is highly
 * recommended to key off the {@code requestUri}.
 * <p>
 * A CacheManager also can leverage notifications from Kontent's webhooks that can be sent when the project's
 * content is changed.
 * <p>
 * By default, if no CacheManager is provided, the DeliveryClient will use it's default, which just passes through.
 *
 * @see DeliveryClient#setCacheManager(CacheManager)
 * @see <a href="https://docs.kontent.ai/tutorials/develop-apps/integrate/using-webhooks-for-automatic-updates">
 *      Kontent.ai API reference - Webhooks and notifications</a>
 */
public interface CacheManager {

    /**
     * Retrieve an earlier cached response from the KontentDelivery API.
     *
     * @param url The url that would be used to retrieve the response from Kontent.ai Delivery API.
     * @return JsonNode response or null if no value is available in the cache for the given url.
     */
    JsonNode get(final String url);

    /**
     * Cache a response from the KontentDelivery API.
     *
     * @param url the URL that was used to retrieve the response from the Kontent.ai Delivery API.
     * @param jsonNode the JsonNode created from the response from the Kontent.ai Deliver API.
     * @param containedContentItems (null allowed) can be used to inspect the original contents of the JsonNode and allow for precise cache invalidation (if implemented).
     */
    void put(final String url, JsonNode jsonNode, List<ContentItem> containedContentItems);
}
