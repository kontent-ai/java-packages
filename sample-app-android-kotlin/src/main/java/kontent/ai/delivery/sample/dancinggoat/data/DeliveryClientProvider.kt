package kontent.ai.delivery.sample.dancinggoat.data

import kontent.ai.delivery.DeliveryClient
import kontent.ai.delivery.DeliveryOptions
import kontent.ai.delivery.Header
import kontent.ai.delivery.sample.dancinggoat.models.Article

object DeliveryClientProvider {
    // https://github.com/kontent-ai/kontent-ai.github.io/blob/main/docs/articles/Guidelines-for-Kontent.ai-related-tools.md
    private const val TRACKING_HEADER_NAME = "X-KC-SOURCE"
    private const val TRACKING_HEADER_VALUE = "kontent.ai.delivery.sample.dancinggoat.android.kotlin;2.0.0"

    private var instance: DeliveryClient? = null

    val client: DeliveryClient
        get() {
            if (instance == null) {
                val client = DeliveryClient(
                        DeliveryOptions
                                .builder()
                                .projectId("975bf280-fd91-488c-994c-2f04416e5ee3")
                                .customHeaders(listOf(
                                        Header(TRACKING_HEADER_NAME, TRACKING_HEADER_VALUE)
                                ))
                                .build(),
                        null
                )
                client.registerType(Article::class.java)
                instance = client
            }
            return instance as DeliveryClient;
        }
}