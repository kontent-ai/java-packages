package kentico.kontent.delivery.sample.dancinggoat.data

import kentico.kontent.delivery.DeliveryClient
import kentico.kontent.delivery.DeliveryOptions
import kentico.kontent.delivery.Header
import kentico.kontent.delivery.sample.dancinggoat.models.Article
import java.util.*

object DeliveryClientProvider {
    // https://github.com/Kentico/Home/wiki/Guidelines-for-Kontent-related-tools#analytics
    private const val TRACKING_HEADER_NAME = "X-KC-SOURCE"
    private const val TRACKING_HEADER_VALUE = "kentico.kontent.delivery.sample.dancinggoat.android.kotlin;1.0.0"

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