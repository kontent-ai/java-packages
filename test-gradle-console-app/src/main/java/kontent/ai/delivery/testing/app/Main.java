package kontent.ai.delivery.testing.app;

import kontent.ai.delivery.DeliveryClient;
import kontent.ai.delivery.DeliveryOptions;
import kontent.ai.delivery.DeliveryParameterBuilder;

import java.util.concurrent.ExecutionException;

public class Main {
        public static void main(final String[] args) throws ExecutionException, InterruptedException {
                final DeliveryClient client = new DeliveryClient("975bf280-fd91-488c-994c-2f04416e5ee3");

                DeliveryOptions options = DeliveryOptions.builder()
                                .projectId("975bf280-fd91-488c-994c-2f04416e5ee3")
                                .retryAttempts(2)
                                .waitForLoadingNewContent(false)
                                .build();

                DeliveryClient client2 = new DeliveryClient(options);

                client.getItem("about_us")
                                .thenAccept(item -> System.out.println(item.getItem().getSystem().getName()));

                client2.getItems(DeliveryParameterBuilder.params()
                                .filterEquals("system.type", "article")
                                .filterEquals("system.collection", "default")
                                .projection("title", "summary", "post_date", "teaser_image")
                                .filterContains("elements.personas", "coffee_lover")
                                .orderByDesc("elements.post_date")
                                .linkedItemsDepth(0)
                                .page(null, 3)
                                .build())
                                .thenAccept(items -> items.getItems()
                                                .stream()
                                                .forEach(item -> System.out.println(item.getSystem().getName())));
        }
}