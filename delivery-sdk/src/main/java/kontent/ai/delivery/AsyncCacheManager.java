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
import java.util.concurrent.CompletionStage;

import org.thymeleaf.cache.ICacheManager;

/**
 * Cache responses against the Kontent.ai Delivery API.
 * 
 * @see AsyncCacheManager
 * @see DeliveryClient#setCacheManager(AsyncCacheManager)
 */
public interface AsyncCacheManager {

    /**
     * Returns the cached data or fetches the data and caches it before returning.
     * 
     * @param url URL for retrieving data.
     * @return Returned data.
     */
    CompletionStage<JsonNode> get(final String url);

    /**
     * Put the data to cache.
     * 
     * @param url URL for retrieving the data.
     * @param jsonNode Plain data to cache.
     * @param containedContentItems Strongly typed data.
     * @return Status of the operation.
     */
    CompletionStage put(final String url, JsonNode jsonNode, List<ContentItem> containedContentItems);
}
