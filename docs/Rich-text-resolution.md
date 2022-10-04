# Rich text resolution

This page describes the possibility to resolve rich text elements.

_Prerequisites_

- [Generate models using Model Generator](https://github.com/kontent-ai/java-packages/tree/master/kontent-delivery-generators#readme-task)
- Register models using [`scanClasspathForMappings`, or `registerType`](https://github.com/kontent-ai/java-packages/tree/master/kontent-delivery/src/main/java/kentico/kontent/delivery/DeliveryClient.java).

## Type-based string resolution resolution

[Example](https://github.com/kontent-ai/java-packages/tree/master/sample-app-spring-boot#custom-rich-text-component).

If you wish to resolve the components/inline linked items into the string.You could use `registerInlineContentItemsResolver`.

```java
// Tweet is strongly typed model
client.registerInlineContentItemsResolver(new InlineContentItemsResolver<Tweet>() {
    @Override
    public String resolve(Tweet item) {
        return "<div>" + item.getTweetTitle() + "</div>";
    }
});
```

## (Thymeleaf) Template engine

- [A simple example of Spring Boot application with Thymeleaf template engine](https://github.com/kontent-ai/java-packages/tree/master/sample-app-spring-boot#readme).

### Automatic template resolution

[**😉START with the example😉**](https://github.com/kontent-ai/java-packages/tree/master/sample-app-spring-boot#automatic-rich-text-component-resolution)

Create Thymeleaf templates to the resources to specific places ([currently "kentico/templates/", "META-INF/kentico/templates/", "kentico/kontent/templates/", "META-INF/kentico/kontent/templates/"](https://github.com/kontent-ai/java-packages/blob/master/kontent-delivery/src/main/java/kentico/kontent/delivery/template/ViewResolverConfiguration.java)), with name `<ITEM_TYPE_CODENAME>.html` suffix - as for [hosting_video](https://github.com/kontent-ai/java-packages/blob/master/sample-app-spring-boot/src/main/resources/kentico/kontent/templates/hosted_video.html).

> Location customizable by extending the default Template engine.

```java
TemplateEngineConfig config = new TemplateEngineConfig();
config.getViewResolverConfiguration().addPrefixes("acme/templates/");
config.getViewResolverConfiguration().setSuffix(".template");
DeliveryClient client = new DeliveryClient(
        DeliveryOptions
                .builder()
                .projectId("975bf280-fd91-488c-994c-2f04416e5ee3")
                .build(),
        config);
```

### Completely custom template configuration i.e. Default fallback rich text element resolver

To completely customize the template configuration, it is possible to create your own implementation of `TemplateEngineConfig` (and `TemplateEngine`)

> In the following example, it is possible i.e. to use the default implementation of [`TemplateEngineConfig`](https://github.com/kontent-ai/java-packages/blob/master/kontent-delivery/src/main/java/kentico/kontent/delivery/template/TemplateEngineConfig.java) and [`TemplateEngine`](https://github.com/kontent-ai/java-packages/blob/master/kontent-delivery/src/main/java/kentico/kontent/delivery/template/TemplateEngine.java) and i.e. add "Default fallback resolver" for all other types that are not registered via `registerInlineContentItemsResolver`.

```java
class CustomTemplateEngine implements  TemplateEngine {

    private ViewResolverConfiguration viewResolverConfiguration;

    @Override
    public void setViewResolverConfiguration(ViewResolverConfiguration viewResolverConfiguration) {
        this.viewResolverConfiguration = viewResolverConfiguration;
    }

    @Override
    public String process(TemplateEngineModel data) {
        // You can use viewResolverConfiguration if you need it as well

        if(data.getInlineContentItem() instanceof Tweet) {
            Tweet item = (Tweet) data.getInlineContentItem();

            return "<div>" + item.getTweetTitle() + "</div>";
        }
        else {
            // Or raise exception
            return "UNKNOWN COMPONENT!";
        }
    }
}
```

```java
class CustomTemplateConfiguration extends TemplateEngineConfig {

    @Override
    public void init() {
        super.init();
        this.addResolvers(new TemplateEngineInlineContentItemsResolver() {
            @Override
            public boolean supports(TemplateEngineModel data) {
                // Define supported content items
                return data.getInlineContentItem() instanceof Tweet;
            }

            @Override
            public TemplateEngine getTemplateEngine() {
                return new CustomTemplateEngine();
            }
        });
    }
}
```

```java
DeliveryClient client = new DeliveryClient(
    DeliveryOptions
        .builder()
        .projectId("975bf280-fd91-488c-994c-2f04416e5ee3")
        .customHeaders(Arrays.asList(
            new Header(TRACKING_HEADER_NAME, TRACKING_HEADER_VALUE)
        ))
        .build(),
    new CustomTemplateConfiguration()
);
```

> TODO: [Structured rich text element mapping](https://github.com/kontent-ai/java-packages/issues/125)
