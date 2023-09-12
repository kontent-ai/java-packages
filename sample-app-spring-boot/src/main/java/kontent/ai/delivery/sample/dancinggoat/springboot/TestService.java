package kontent.ai.delivery.sample.dancinggoat.springboot;

import kontent.ai.delivery.ContentItem;
import kontent.ai.delivery.DeliveryClient;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
public class TestService implements Serializable {

    @Cacheable(cacheNames = "test", key = "#contentName") //, key = "#contentName"
    public ContentItem fetchContentByCodeName(String contentName, DeliveryClient deliveryClient) {
        try {
            simulateSlowService();
            ContentItem contentItem = deliveryClient.getItem(contentName, ContentItem.class).toCompletableFuture().get();
            return contentItem;
        } catch (Exception exp) {
            throw new RuntimeException("Error fetching content with codename = " + contentName, exp);
        }
    }


    // Don't do this at home
    private void simulateSlowService() {
        try {
            long time = 3000L;
            Thread.sleep(time);
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
    }
}
