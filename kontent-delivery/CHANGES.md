# Changes in this version

* Delivery client now returns `CompletionStage` and it is async by default
  * Internally [OkHttpClient](https://square.github.io/okhttp/4.x/okhttp/okhttp3/-ok-http-client/) is used to handle network requests
* Place for templates is by default
  * `kentico/templates/`, `META-INF/kentico/templates/`, `kentico/kontent/templates/`, `META-INF/kentico/kontent/templates/` (last two are new)
* Retry codes are now set statically in DeliveryClient: `408, 429, 500, 502, 503, 504`
  * If the retry response is not parsable to KenticoError.class - retry is not performed - https://docs.kontent.ai/reference/delivery-api#section/Errors/Resolving-errors