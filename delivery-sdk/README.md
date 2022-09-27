
# Kontent.ai Delivery Java SDK

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

[![javadoc](https://javadoc.io/badge2/ai.kontent/delivery-sdk/javadoc.svg)](https://javadoc.io/doc/ai.kontent/delivery-sdk)
[![Maven Central](https://img.shields.io/maven-central/v/ai.kontent/delivery-sdk)](https://oss.sonatype.org/content/groups/public/ai/kontent/delivery-sdk/)

[![Stack Overflow](https://img.shields.io/badge/Stack%20Overflow-ASK%20NOW-FE7A16.svg?logo=stackoverflow&logoColor=white)](https://stackoverflow.com/tags/kontent-ai)
[![Discord](https://img.shields.io/discord/821885171984891914?color=%237289DA&label=Kontent.ai%20Discord&logo=discord)](https://discord.gg/SKCxwPtevJ)

The Kontent.ai Delivery Java SDK is a client library used for retrieving content from [Kontent.ai](https://kontent.ai).

## Get started

You can use the SDK in the form of a Apache Maven package from [Maven Central](https://repo.maven.apache.org/maven2/ai/kontent/delivery/) - so you need to point your Maven to `mavenCentral`

### Gradle

```groovy  

repositories {
    mavenCentral()
}

dependencies {
  implementation 'ai.kontent:delivery-sdk:latest.release'
}
```

> You may want to change `latest.release` to specific one (i.e. `5.0.0`).

### Maven

```xml  
<dependency>
  <groupId>ai.kontent</groupId>
  
  <artifactId>delivery-sdk</artifactId> 
  <version>[5.0.0,)</version> 
  <type>pom</type>
</dependency>  
```  

> You may want to change version specification - `[5.0.0,)` - from [range one](https://cwiki.apache.org/confluence/display/MAVENOLD/Dependency+Mediation+and+Conflict+Resolution#DependencyMediationandConflictResolution-DependencyVersionRanges) to specific one (i.e. `5.0.0`).

## Creating the DeliveryClient

The `DeliveryClient` class is the main class of the SDK. Using this class, you can retrieve content from your Kontent projects.

To create an instance of the class, you need to provide a [project ID](https://docs.kontent.ai/tutorials/develop-apps/get-content/getting-content#a-getting-content-items).

```java  
// Initializes an instance of the DeliveryClient client  
DeliveryClient client = new DeliveryClient("975bf280-fd91-488c-994c-2f04416e5ee3");  
```  

You can also provide the project ID and other parameters by passing the [`DeliveryOptions`](./src/main/java/kontent/ai/delivery/DeliveryOptions.java) object to the class constructor. The `DeliveryOptions` object can be used to set the following parameters:

- `setPreviewApiKey(String)` – sets the [Delivery Preview API key](https://docs.kontent.ai/reference/delivery-api#section/Production-vs.-Preview).
- `setProductionApiKey(String)` - sets the [Delivery Client key for secured access](https://docs.kontent.ai/reference/delivery-api#tag/Secure-access).
- `setProjectId(String)` – sets the project identifier.
- `setUsePreviewApi(boolean)` – determines whether to use the Delivery Preview API.
- `setWaitForLoadingNewContent(boolean)` – makes the client instance wait while fetching updated content, useful when acting upon [webhook calls](https://docs.kontent.ai/tutorials/develop-apps/integrate/using-webhooks-for-automatic-updates#a-getting-the-latest-content).
- `setRetryAttempts(int)` - sets the number of retry attempts the client should make when a request to the API fails.
- `setProductionEndpoint(String)` - sets the production endpoint address. Mainly useful to change for mocks in unit tests, or if you are establishing a proxy.
- `setPreviewEndpoint(String)` - sets the preview endpoint address. Mainly useful to change for mocks in unit tests, or if you are establishing a proxy.
- `setProxyServer(java.net.Proxy)` - sets the proxy server used by the http client. Mainly used to complex Proxy scenarios.
- `setCustomHeaders(java.utils.List<Header>)` - sets custom headers to be included in the request. *Check the reserved header names in method remarks. These will be ignored.*

The `DeliveryOptions.builder()` can also simplify creating a `DeliveryClient`:

```java  
DeliveryClient client = new DeliveryClient(
  DeliveryOptions
  
    .builder()
    .projectId("975bf280-fd91-488c-994c-2f04416e5ee3")
    .productionApiKey("secured key")
    .build()
);  
```  

Once you create a `DeliveryClient`, you can start querying your project repository by calling methods on the client instance. See [Basic querying](#basic-querying) for details.

### Previewing unpublished content

To retrieve unpublished content, you need to create a `DeliveryClient` with both Project ID and Preview API key (You could also configure Preview API key in `DeliveryOptions` described above). Each Kontent project has its own Preview API key.

```java
// Note: Within a single project, we recommend that you work with only
// either the production or preview Delivery API, not both.
DeliveryClient client = new DeliveryClient(
  "YOUR_PROJECT_ID",
  "YOUR_PREVIEW_API_KEY"
);
```  

For more details, see [Previewing unpublished content using the Delivery API](https://kontent.ai/learn/tutorials/write-and-collaborate/preview-your-content/).

## Basic querying

Once you have a `DeliveryClient` instance, you can start querying your project repository by calling methods on the instance.

```java  
// Retrieves a single content item  
CompletionStage<ContentItemResponse> response = client.getItem("about_us");  
  
// Retrieves a list of all content items  
CompletionStage<ContentItemsListingResponse> listingResponse = client.getItems();  
```  

As you may have noticed from the example `DeliveryClient` is returning [`CompletionStage<T>`](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/CompletionStage.html) that allows you to chain the requests, perform filtering, data transformation, etc.

### Transfer asynchronous response to synchronous

Sometimes, it is necessary to transform asynchronous calls `CompletionStage<T>` to synchronous ones and return the data (`T`).

> Keep in mind this transformation needs to handle possible `ExecutionException` and `InterruptedException` that could be raised when waiting to process the transformation.

```java
// Retrieves a single content item
ContentItemResponse response = client.getItem("about_us")
  .toCompletableFuture()
  .get();

// Retrieves a list of all content items
ContentItemsListingResponse listingResponse = client.getItems()
  .toCompletableFuture()
  .get();
```

### Filtering retrieved data

The SDK supports full scale of the API querying and filtering capabilities as described in the [API reference](https://kontent.ai/learn/reference/delivery-api/#tag/Filtering-content).

```java
// Retrieves a list of the specified elements from the first 10 content items of
// the 'brewer' content type, ordered by the 'product_name' element value
// also includes total number of items stored in Kontent.ai i.e. for pagination purposes

CompletionsStage<ContentItemsListingResponse> response = client.getItems(
  DeliveryParameterBuilder.params()
    .language("es-ES")
    .filterEquals("system.type", "brewer")
    .projection("image", "price", "product_status", "processing")
    .page(null, 10)
    .orderByAsc("elements.product_name")
    .includeTotalCount()
    .build()
)
``` 

## Response structure

For full description of single and multiple content item JSON response formats, see our [API reference](https://kontent.ai/learn/reference/delivery-api/#section/Content-item-object).

### Single content item response

When retrieving a single content item, you get an instance of the `ContentItemResponse` class. This class represents the JSON response from the Delivery API endpoint and contains the requested `ContentItem` as a property.

### Multiple content items response

When retrieving a list of content items, you get an instance of the `ContentItemsListingResponse`. This class represents the JSON response from the Delivery API endpoint and contains:

- `getPagination()` returns a `Pagination` object with information about the following:
  - `getSkip()`: requested number of content items to skip
  - `getLimit()`: requested page size
  - `getCount()`: the total number of retrieved content items
  - `getTotalCount()`: total number of content items matching the search criteria
  - `getNextPage()`: the URL of the next page
- A list of the requested content items

### ContentItem structure

The `ContentItem` class provides the following:

- `getSystem()` returns a `System` object with metadata such as code name, display name, type, collection, sitemap location, or workflow step.
- `getElements()` returns a Map containing all the elements included in the response keyed by code names.
- Methods for easier access to certain types of content elements such as linked items, or assets.

## Getting content item properties

You can access information about a content item (i.e., its ID, codename, name, location in sitemap, date of last modification, its collection codename, and its content type codename) by using the `System` object.

```java  
// Retrieves name of an article content item  
articleItem.getSystem().getName()  
  
// Retrieves codename of an article content item  
articleItem.getSystem().getCodename()  
  
// Retrieves codename of the collection of an article content item  
articleItem.getSystem().getCollection()  
  
// Retrieves codename of the content type of an article content item  
articleItem.getSystem().getType()  

// Retrieves codename of the workflow step of an article content item
articleItem.getSystem().getWorkflowStep()  
```  

## Getting element values

The SDK provides methods for retrieving content from content elements such as Asset, Text, Rich Text, Multiple choice, etc.

### Text and Rich text

For text elements, you can use the `getString` method.

```java  
// Retrieves an article text from the 'body_copy' Text element  
articleItem.getString("body_copy")  
```  

The Rich text element can contain links to other content items within your project. See [Resolving links to content items](https://github.com/kontent-ai/java-packages/wiki/Resolving-links-to-content-items) for more details.

### Asset

```java  
// Retrieves a teaser image URL  
articleItem.getAssets("teaser_image").get(0).getUrl()  
```  

### Multiple choice

To get a list of options defined in a Multiple choice content element, you first need to retrieve the content element itself. For this purpose, you can use the `getContentTypeElement` method, which takes the codename of a content type and the codename of a content element.

```java  
// Retrieves the 'processing' element of the 'coffee' content type  
MultipleChoiceElement element = (MultipleChoiceElement) client.getContentTypeElement("coffee", "processing");  
```  

After you retrieve the Multiple choice element, you can work with its list of options. Each option has the following methods:

Method | Description | Example
---------|----------|---------  
getName() | The display name of the option. | `Dry (Natural)`
getCodename() | The codename of the option. | `dry__natural_

To put the element's options in a list, you can use the following code:

```java
List<SelectListItem> items = new List<>();

for (Option option : element.getOptions()) {
    SelectListItem item = new SelectListItem();
    item.setText(option.getName());
    item.setValue(option.getCodename());
    item.setSelected("semi_dry".equals(option.getCodename()));
}
``` 

### Linked items

```java  
// Retrieves related articles  
articleItem.getLinkedItems("related_articles")  
```  

### Custom items

```java  
// Retrieves the value of the custom element 'color'  
String customElementValue = ((CustomElement) articleItem.getElements().get("color")).getValue();  
```  

## Android development

To use this SDK for [Android](https://developer.android.com/) development, you can use any approach compatible with [Java CompletionStage API](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/CompletionStage.html). Most common is to use [Kotlin coroutines](https://kotlinlang.org/docs/reference/coroutines-overview.html) for Android applications written in Kotlin and [Java RX](https://github.com/ReactiveX/RxJava) for Android applications written in Java. Both of these approaches are showcased in this repository:

* [Android Java sample application](../sample-app-android#readme)
* [Android Kotlin Java Sample application](../sample-app-android-kotlin#readme)

⚠ There are two Android-specific rules you need to follow in order for the Delivery SDK to work correctly.

1. Disable template engine integration when initializing the Delivery client.
1. Avoid using the `scanClasspathForMappings` method.

### 1. Initialize the Delivery client for Android development

You need to instantiate the Delivery client with the constructor that disables the template engine. The template engine is meant to be used with the web platform only. For Android development, use the constructor `DeliveryClient#DeliveryClient(DeliveryOptions, TemplateEngineConfig)` and set the second parameter to `null`.

```java  
DeliveryClient client = new DeliveryClient(new DeliveryOptions(AppConfig.KONTENT_PROJECT_ID), null);  
```  

See it [used in a sample app](../sample-app-android/src/main/java/com/github/kentico/delivery_android_sample/data/source/DeliveryClientProvider.java)).

### 2. Register strongly-typed models

Android applications must register the models using the `registerType` method. See a usage example in [DeliveryClientProvider.java](../sample-app-android/src/main/java/com/github/kentico/delivery_android_sample/data/source/DeliveryClientProvider.java).

You can still use the [model generator](../delivery-sdk-generators/README.md) for generating the models.

> ⚠ The `scanClasspathForMappings` method does not work in the Android environment. Because of the differences between Android Dalvik VM and Java VM, the scanning library is not usable here. That's why you need to use the `registerType` method instead.

## Further information

For more developer resources, visit the [Kontent.ai Learn portal](https://kontent.ai/learn).

### Showcase

If you want to explore the possibilities of the SDK, visit [Features section of the Spring boot application](../sample-app-spring-boot/Readme.md#Features).

### Feedback & Contributing

Check out the [contributing](CONTRIBUTING.md) page to see the best places to file issues, start discussions, and begin contributing.

### Wall of Fame

We would like to express our thanks to the following people who contributed and made the project possible:

- [Adam J. Weigold](https://github.com/aweigold)
- [Tommaso Garuglieri](https://github.com/GaruGaru)
- [Gabriel Cunha](https://github.com/cunhazera)
- [Jeremy Martin](https://github.com/jerrmartin)

Would you like to become a hero too? Pick an [issue](https://github.com/kontent-ai/delivery-sdk-java/issues) and send us a pull request!
