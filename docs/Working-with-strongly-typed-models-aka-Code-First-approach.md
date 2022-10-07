## Contents

<!-- TOC -->

- [Contents](#contents)
- [Strongly-typed models](#strongly-typed-models)
- [Defining a model](#defining-a-model)
  - [Typing the properties](#typing-the-properties)
    - [Typing simple elements](#typing-simple-elements)
    - [Typing linked content items](#typing-linked-content-items)
    - [Typing Rich text](#typing-rich-text)
  - [Naming the properties](#naming-the-properties)
  - [Examples](#examples)
- [Retrieving content items](#retrieving-content-items)
  - [Casting to strong types](#casting-to-strong-types)
- [Adding support for runtime type resolution](#adding-support-for-runtime-type-resolution)

<!-- /TOC -->

## Strongly-typed models

Besides the [basic methods](https://github.com/Kentico/delivery-sdk-java#basic-querying) for retrieving content items, the `DeliveryClient` supports fetching of strongly-typed models.

```java
// Basic retrieval
new DeliveryClient("975bf280-fd91-488c-994c-2f04416e5ee3")
.getItem("article_about_coffee");

// Strongly-typed model retrieval
new DeliveryClient("975bf280-fd91-488c-994c-2f04416e5ee3")
.getItem("article_about_coffee", Article.class);
```

This approach is beneficial for its:

- type safety during compile-time
- convenience of usage by a developer (`article.getArticleTitle()` vs. `@article.getString("article_title")`)
- support of type-dependent functionalities (such as usages in your chosen view template engine)

## Defining a model

The models are simple [JavaBean POJO](https://en.wikipedia.org/wiki/Plain_old_Java_object#JavaBeans) classes, which means they don't have any attached behavior or dependency on an external framework.  Note, you must ensure that you have a [default no-argument constructor](https://en.wikipedia.org/wiki/Nullary_constructor) as well as [setter methods](http://docs.oracle.com/javaee/6/tutorial/doc/gjbbp.html) that are named to match your properties.

### Typing the properties

#### Typing simple elements

Here are the data types you can use for different content type elements:

- Built-in Java types such as `String`, `ZonedDateTime`, `Double` and their nullable equivalents for simple elements like Number or Text.
- `List<kontent.ai.delivery.Option>` for Multiple choice elements
- `List<kontent.ai.delivery.Asset>` for Assets elements
- `List<kontent.ai.deliveryTaxonomy>` for Taxonomy elements

#### Typing linked content items

Mapping to individual linked content items is supported, see [Naming the properties](#naming-the-properties).

To map to several *Linked items* elements, use either `List<T>` or `Map<String, T>`.

Depending on your scenario, use one of the following as the data type parameter:

- Specific content type model (e.g., `Article`) &ndash; when the element contains content items based on a single content type.
- `ContentItem` &ndash; when the element can contain mixed content types and you don't need type safety.

When mapping to a specific content type model as a list or map, you must use the `ContentItemMapping` annotation on the target class to specify the content type it maps too.

```java
@ContentItemMapping("article")
public class Article {
...
}
```

#### Typing Rich text

For Rich text elements, use `String` to receive HTML code resolved using string-based resolver as outlined in [Rendering  content items in Rich text](https://github.com/Kentico/delivery-sdk-java/wiki/Rendering-content-items-in-Rich-text).

### Naming the properties

By default, the model properties and content type elements are matched by codenames of the elements. The SDK tries to convert the element codenames to [CamelCase](https://en.wikipedia.org/wiki/Camel_case). For example, a content type element with the codename of `article_title` translates to a property called `articleTitle`.

If you need to change the codename of an element the property is bound to, you can enrich the property with the `ElementMapping` annotation.

```java
@ElementMapping("text_field")
public string articleTitle;
```

Linked items elements are matched in the same manner as content type elements.

If you need to change the codename of a single linked content item the property is bound to, you can enrich the property with the `ContentItemMapping` annotation.

```java
@ContentItemMapping("origins_of_arabica_bourbon")
ContentItem arabicaBourbonOrigin;
```

### Examples

You can find a sample model at [ArticleItem.java](../delivery-sdk/src/test/java/kontent/ai/delivery/ArticleItem.java)

## Retrieving content items

All the `getItem` and `getItems` methods have their corresponding methods where you can pass in a Class that represents the model you want to load. The parameters are the same as for the non-generic variants. The only difference is that you have to specify the class as an additional argument.

You can either specify the type directly (e.g., `getItem("on_roasts", ArticleItem.class)`) or pass the type as `Object.class` (e.g., `getItem("on_roasts", Object.class)`). Use the second approach if you don't know what the type is to let the SDK resolve it during runtime.

This argument represents the model you want to load. You can specify the parameter in two ways:

- by using a content type model, for example `getItem("on_roasts", ArticleItem.class)`
- by passing `Object.class`, for example, `getItem("on_roasts", Object.class)`

Use the second approach if you don't know what the content type will be and you want the application to resolve it during runtime. See [Adding support for runtime type resolution](#adding-support-for-runtime-type-resolution) for more details.


### Casting to strong types

Note that it's possible to cast `ContentItemResponse` and `ContentItemsListingResponse` to strongly-typed equivalents by calling `castTo(Class<T> tClass)`.  Calling this method on `ContentItemsListingResponse` returns `List<T>`.

## Adding support for runtime type resolution

The `DeliveryClient` supports runtime type resolution. This means you can pass `Object.class` as an argument instead of explicitly specifying the data type in the model or when calling the `getItem` and `getItems` methods. The data type will be resolved dynamically during the runtime.

For example:

```java
Object model = client.getItem("on_roasts", Object.class);
Assert.assertTrue(model instanceOf ArticleItem); // type will be e.g. 'ArticleItem'
```

For this to work, the SDK needs to know the mappings between the content types and your models.

If you want to use the runtime type resolution in your application, you have 3 options.  You can either register the codename of the type with your class.  You can annotate you class with `@ContentItem(codename)` and register just the class.  You can also scan the classpath for annotated classes.

```java
// register by codename
client.registerType("article", ArticleItem.class);

// register by annotated class
client.registerType(ArticleItem.class);

// register by scanning the classpath for annotated classes
client.scanClasspathForMappings("com.dancinggoat");
```
