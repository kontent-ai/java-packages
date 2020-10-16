package com.github.kentico.delivery_android_sample.data.source;

import com.github.kentico.delivery_android_sample.app.config.AppConfig;
import com.github.kentico.delivery_android_sample.data.models.Article;
import com.github.kentico.delivery_android_sample.data.models.Cafe;
import com.github.kentico.delivery_android_sample.data.models.Coffee;
import kentico.kontent.delivery.DeliveryClient;
import kentico.kontent.delivery.DeliveryOptions;

public class DeliveryClientProvider {
    private static DeliveryClient INSTANCE;

    public static DeliveryClient getClient() {
        if (INSTANCE == null) {
            DeliveryClient client = new DeliveryClient(new DeliveryOptions(AppConfig.KONTENT_PROJECT_ID), null);
            client.registerType(Article.class);
            client.registerType(Cafe.class);
            client.registerType(Coffee.class);
            INSTANCE = client;
        }
        return INSTANCE;
    }
}
