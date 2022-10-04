# Using local HTTP server

You could use `org.apache.http.localserver.LocalServerTestBase` to create a mock server. And then configure the client to use server's generate URL as the base URL.

A showcase of the configuration could be found in [~/kontent-delivery/src/test/java/kentico/kontent/delivery/DeliveryClientTest.java](https://github.com/kontent-ai/java-packages/blob/master/kontent-delivery/src/test/java/kentico/kontent/delivery/DeliveryClientTest.java#L287-L326)

```java
this.serverBootstrap.registerHandler(
    String.format("/%s/%s", projectId, "items/on_roasts"),
    (request, response, context) -> {
        response.setEntity(
            new InputStreamEntity(
                this.getClass().getResourceAsStream("SampleContentItem.json")
            )
        );
    }
);

HttpHost httpHost = this.start();
String testServerUri = httpHost.toURI();
DeliveryOptions deliveryOptions = new DeliveryOptions();
deliveryOptions.setProjectId(projectId);
deliveryOptions.setProductionEndpoint(testServerUri);

DeliveryClient client = new DeliveryClient(deliveryOptions, null);
```

---

## Using Cache manager

It should be possible to use a custom Cache manager based on `kontent.ai.delivery.CacheManager`, or `kontent.ai.delivery.AsyncCacheManager`. Prepare mock responses by implementing `get` method with custom JSON. And lastlyd then use `kontent.ai.delivery.Deliveryclient.setCacheManager`.

> This approach was not tested.
