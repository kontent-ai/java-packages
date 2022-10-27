package kontent.ai.sample.android.kotlin.data

import kontent.ai.delivery.DeliveryClient
import kontent.ai.delivery.DeliveryOptions
import kontent.ai.delivery.Header
import kontent.ai.sample.android.kotlin.models.Article

object DeliveryClientProvider {
    // https://github.com/kontent-ai/kontent-ai.github.io/blob/main/docs/articles/Guidelines-for-Kontent.ai-related-tools.md
    private const val TRACKING_HEADER_NAME = "X-KC-SOURCE"
    private const val TRACKING_HEADER_VALUE = "kontent.ai.samples.safelife.android.kotlin;1.0.0"

    private var instance: DeliveryClient? = null

    val client: DeliveryClient
        get() {
            if (instance == null) {
                val client = DeliveryClient(
                        DeliveryOptions
                                .builder()
                                .projectId("3df7d191-c096-01aa-e99e-1c3ce15ca8cc")
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