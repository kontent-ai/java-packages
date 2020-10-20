package kentico.kontent.delivery.sample.dancinggoat.data

import kentico.kontent.delivery.DeliveryClient
import kentico.kontent.delivery.DeliveryOptions
import kentico.kontent.delivery.sample.dancinggoat.models.Article

object DeliveryClientProvider {
    private var instance: DeliveryClient? = null

    val client: DeliveryClient
        get() {
            if (instance == null) {
                var client = DeliveryClient(DeliveryOptions("975bf280-fd91-488c-994c-2f04416e5ee3"), null)
                client.registerType(Article::class.java)
                instance = client
            }
            return instance as DeliveryClient;
        }
}