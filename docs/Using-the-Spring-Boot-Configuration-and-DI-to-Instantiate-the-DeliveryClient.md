# Spring Boot and DI to register delivery client

You can use Spring `@Configuration` and `@Bean` data annotation to register the Kontent Delivery client and then used `@Autowired` when you need to use it

The approach is showcased on [Spring Boot sample application](../sample-app-spring-boot) - the code part is in [KontentConfiguration.java file](../delivery-sdk/src/main/java/kontent/ai/delivery/template/ViewResolverConfiguration.java).

You can see there you can register recovers as well.
The template is simple.

```java
// ...

@Configuration
public class KontentConfiguration {

    @Bean
    public DeliveryClient deliveryClient() {
        DeliveryClient client = new DeliveryClient(
                DeliveryOptions
                        .builder()
                        .projectId("975bf280-fd91-488c-994c-2f04416e5ee3")
                        .customHeaders(Arrays.asList(
                                new Header(TRACKING_HEADER_NAME, TRACKING_HEADER_VALUE)
                        ))
                        .build()
        );

    // ... additional configuration of the client

    }
}
```

And then in a (most probably) controller, you just use the client:

```java
// ...

@Controller
public class ArticleController {

    @Autowired
    DeliveryClient deliveryClient;

// ...

}
```
