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
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Uses the JVM memory to cache results.
 * It also allows cache to be invalidated based on both the codename and language of content items.
 * This makes it easy to invalidate the cache for incoming webhooks.
 * <p>
 * This implementation mainly serves as an example.
 * Do not use this cache manager when your application is deployed as multiple replicas!
 * In that case a centralized cache (e.g. Redis) is advisable.
 */
@Slf4j
public class SimpleInMemoryCacheManager implements CacheManager {

    final protected Map<String, JsonNode> cache = Collections.synchronizedMap(new HashMap<>());

    final protected Map<String, Set<String>> tagsForUrls = Collections.synchronizedMap(new HashMap<>());

    final protected AtomicInteger queries = new AtomicInteger(0);
    final protected AtomicInteger hits = new AtomicInteger(0);
    final protected AtomicInteger puts = new AtomicInteger(0);

    @Override
    public JsonNode get(final String url) {
        log.debug("Cache get");
        queries.incrementAndGet();


        JsonNode jsonNode = cache.computeIfPresent(url, (key, val) -> {
            log.debug("Cache hit");
            hits.incrementAndGet();
            return val;
        });

        return jsonNode;
    }

    @Override
    public void put(final String url, final JsonNode jsonNode, final List<ContentItem> containedContentItems) {
        puts.incrementAndGet();
        cache.put(url, jsonNode);

        // Store tags that point to the given url.
        // Tags are created for every code_name+language combination that can be determined from the given containedContentItems
        Optional.ofNullable(containedContentItems)
                .map(this::createCacheTags)
                .orElse(Collections.emptySet())
                .forEach(cacheTag -> getUrlsForTag(cacheTag).add(url));
    }

    public void invalidate(final String url) {
        cache.remove(url);
    }

    public void invalidate(final CacheTag cacheTag) {
        // Possible race condition
        Set<String> urls = getUrlsForTag(cacheTag);
        urls.forEach(url -> {
            invalidate(url);
            urls.remove(url);
        });
    }

    private Set<String> getUrlsForTag(final CacheTag cacheTag) {
        tagsForUrls.putIfAbsent(cacheTag.toString(), Collections.synchronizedSet(new HashSet<>()));
        return tagsForUrls.get(cacheTag.toString());
    }

    private Set<CacheTag> createCacheTags(final List<ContentItem> containedContentItems) {
        return containedContentItems.stream()
                .map(this::createCacheTags)
                .flatMap(Set::stream)
                .collect(Collectors.toSet());
    }

    private Set<CacheTag> createCacheTags(final ContentItem contentItem) {
        Set<CacheTag> tags = new HashSet<>();
        tags.add(new CacheTag(contentItem));

        tags.addAll(
                contentItem.getLinkedItemProvider().getLinkedItems().values().stream()
                        .map(CacheTag::new)
                        .collect(Collectors.toSet())
        );

        return tags;
    }

    @Data
    @AllArgsConstructor
    public static class CacheTag {
        String codeName;
        String language;

        public CacheTag(ContentItem contentItem) {
            codeName = contentItem.getSystem().getCodename();
            language = contentItem.getSystem().getLanguage();
        }

        @Override
        public String toString() {
            return String.format("%s#%s", codeName, language);
        }
    }
}
