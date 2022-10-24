package kontent.ai.delivery_android_sample.data.source;

import kontent.ai.delivery_android_sample.app.config.AppConfig;
import kontent.ai.delivery_android_sample.data.models.Article;
import kontent.ai.delivery_android_sample.data.models.Cafe;
import kontent.ai.delivery_android_sample.data.models.Coffee;
import kontent.ai.delivery.DeliveryClient;
import kontent.ai.delivery.DeliveryOptions;
import kontent.ai.delivery.Header;

import java.util.Arrays;

public class DeliveryClientProvider {
    // https://github.com/kontent-ai/kontent-ai.github.io/blob/main/docs/articles/Guidelines-for-Kontent.ai-related-tools.md
    private static final String TRACKING_HEADER_NAME = "X-KC-SOURCE";
    private static final String TRACKING_HEADER_VALUE = "kontent.ai.delivery_android_sample;2.0.0";

    private static DeliveryClient INSTANCE;

    public static DeliveryClient getClient() {
        if (INSTANCE == null) {
            DeliveryClient client = new DeliveryClient(
                    DeliveryOptions
                            .builder()
                            .projectId(AppConfig.KONTENT_PROJECT_ID)
                            .customHeaders(Arrays.asList(
                                    new Header(TRACKING_HEADER_NAME, TRACKING_HEADER_VALUE)
                            ))
                            .build(),
                    null
            );
            client.registerType(Article.class);
            client.registerType(Cafe.class);
            client.registerType(Coffee.class);
            INSTANCE = client;
        }
        return INSTANCE;
    }
}
