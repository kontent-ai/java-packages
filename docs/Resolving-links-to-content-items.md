# Resolving links to content items

<!-- TOC -->

- [Resolving links to content items](#resolving-links-to-content-items)
  - [Content links](#content-links)
  - [Implementing a resolver](#implementing-a-resolver)
  - [Registering a resolver](#registering-a-resolver)
  - [Retrieving Rich text content](#retrieving-rich-text-content)

<!-- /TOC -->

## Content links

[Rich text elements](https://docs.kontent.ai/tutorials/develop-apps/get-content/dealing-with-structure-in-rich-text) in Kontent.ai can contain links to other content items. For example, if you run a blog, these content item links might represent hyperlinks to other blog posts or your contact page.

Without adjusting your project, any link in a Rich text element that points to a content item will contain an empty value.

```html
<p>
  Each AeroPress comes with a
  <a href="" data-item-id="65832c4e-8e9c-445f-a001-b9528d13dac8"
    >pack of filters</a
  >
  included in the box.
</p>
```

To make sure such links resolve correctly on your website, you need to complete these steps:

1. Implement a content link URL resolver
2. Implement a broken link URL resolver
3. Register the resolvers within the `DeliveryClient` instance
4. Retrieve content of a Rich text element

## Implementing a resolver

Your content link URL resolver must implement the `ContentLinkUrlResolver` interface, which is a functional interface for resolving URLs to content items, with a `String resolveLinkUrl(Link link)` method.

Your broken link URL resolver must implement the `BrokenLinkUrlResolver` interface, which is a functional interface for resolving URLs to missing content items, with a `String resolveBrokenLinkUrl()` method.

- **ContentLinkUrlResolver** – used when the linked content item is available.
- **BrokenLinkUrlResolver** – used when the linked content item is not available.

When are content items available?

- For live environment, a content item is available when published, and unavailable when deleted or unpublished.
- For preview environment, a content item is available when it exists in the project inventory, and unavailable when deleted.

```java
// Sample resolver implementation
public class CustomContentLinkUrlResolver implements ContentLinkUrlResolver {

    @Override
    String resolveLinkUrl(Link link) {
        // Resolves URLs to content items based on the 'accessory' content type
        if ("accessory".equals(link.getCodename())) {
            return String.format("/accessories/%s", link.getUrlSlug());
        }
    }
}

public class CustomBrokenContentLinkUrlResolver implements BrokenLinkUrlResolver {

    @Override
    String resolveBrokenLinkUrl() {
        // Resolves URLs to unavailable content items
        return "/404";
    }
}
```

Note, because both of these are functional interfaces, you can also implement these as lambdas, see [Registering a resolver](#registering-a-resolver) for examples.

When building the resolver logic, you can use the `link` argument in your code.

The `link` argument provides the following information about the linked content item:

| Method          | Description                                                                                                                                | Example             |
| --------------- | ------------------------------------------------------------------------------------------------------------------------------------------ | ------------------- |
| `getCodename()` | The codename of the linked content item.                                                                                                   | `aeropress_filters` |
| `getUrlSlug()`  | The URL slug of the linked content item. The value is `null` if the item's content type doesn't have a URL slug element in its definition. | `aeropress-filters` |
| `getType()`     | The codename of the content type of the linked content item.                                                                               | `accessory`         |

## Registering a resolver

Once you implement the resolver, you need to register it in the `DeliveryClient`.

```java
// Sets the resolver as an optional dependency of the DeliveryClient
DeliveryClient client = new DeliveryClient("975bf280-fd91-488c-994c-2f04416e5ee3");
client.setContentLinkUrlResolver(new CustomContentLinkUrlResolver());
client.setBrokenLinkUrlResolver(new CustomBrokenContentLinkUrlResolver());
```

You can also register lambdas with the `DeliveryClient` as the resolvers are functional interfaces.

```java
DeliveryClient client = new DeliveryClient("975bf280-fd91-488c-994c-2f04416e5ee3");
client.setContentLinkUrlResolver((link) -> {
    if ("accessory".equals(link.getCodename())) {
        return String.format("/accessories/%s", link.getUrlSlug());
    }
});
client.setBrokenLinkUrlResolver(() -> "/404");
```

## Retrieving Rich text content

Now, you can resolve links in Rich text elements by using the `getString()` method on the `ContentItem` object.

```java
// Retrieves the 'aeropress' content item
ContentItemResponse response = client.getItem("aeropress");
ContentItem item = response.getItem();

// Retrieves text from the 'long_description' Rich text element
String description = item.getString("long_description");
```

The URL to the content item in the text is now correctly resolved.

```html
<p>
  Each AeroPress comes with a
  <a
    href="/accessories/aeropress-filters"
    data-item-id="65832c4e-8e9c-445f-a001-b9528d13dac8"
    >pack of filters</a
  >
  included in the box.
</p>
```
