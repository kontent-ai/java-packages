package com.github.kentico.delivery_android_sample.data.source;

import com.github.kentico.delivery_android_sample.app.config.AppConfig;
import com.github.kentico.delivery_android_sample.data.models.Article;
import com.github.kentico.delivery_android_sample.data.models.Cafe;
import com.github.kentico.delivery_android_sample.data.models.Coffee;
import kontent.ai.delivery.DeliveryClient;
import kontent.ai.delivery.DeliveryOptions;
import kontent.ai.delivery.Header;

import java.util.Arrays;

public class DeliveryClientProvider {
    // https://github.com/Kentico/Home/wiki/Guidelines-for-Kontent-related-tools#analytics
    private static final String TRACKING_HEADER_NAME = "X-KC-SOURCE";
    private static final String TRACKING_HEADER_VALUE = "com.github.kentico.delivery_android_sample;1.0.0";

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
