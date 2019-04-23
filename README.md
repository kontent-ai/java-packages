# Kentico Cloud Delivery Java SDK

[![Build Status](https://travis-ci.org/Kentico/delivery-sdk-java.svg?branch=master)](https://travis-ci.org/Kentico/delivery-sdk-java)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Javadocs](http://javadoc.io/badge/com.kenticocloud/delivery.svg)](http://javadoc.io/doc/com.kenticocloud/delivery)
[![SonarQube](http://img.shields.io/badge/SonarQube-Results-blue.svg)](https://sonarcloud.io/dashboard?id=com.kenticocloud%3Adelivery-sdk-java)
[![MavenCentral](http://img.shields.io/badge/Maven_Central-2.0.2-yellow.svg)](https://oss.sonatype.org/content/groups/public/com/kenticocloud/delivery/)
[![Stack Overflow](https://img.shields.io/badge/Stack%20Overflow-ASK%20NOW-FE7A16.svg?logo=stackoverflow&logoColor=white)](https://stackoverflow.com/tags/kentico-cloud)

The Kentico Cloud Delivery Java SDK is a client library used for retrieving content from Kentico Cloud. You can use the SDK in the form of a [Maven dependency](https://oss.sonatype.org/content/repositories/snapshots/com/kenticocloud/).

You can add this to your Gradle project by the following:

```groovy

repositories {
    mavenCentral()
}

dependencies {
    implementation 'com.kenticocloud:delivery:2.0.2'
}
```

Or via your Maven POM:
```xml
<dependency>
    <groupId>com.kenticocloud</groupId>
    <artifactId>delivery</artifactId>
    <version>2.0.2</version>
</dependency>
```

## Using the DeliveryClient

The `DeliveryClient` class is the main class of the SDK. Using this class, you can retrieve content from your Kentico Cloud projects.

To create an instance of the class, you need to provide a [project ID](https://developer.kenticocloud.com/v2/docs/getting-content#section-getting-content-items).

```java
// Initializes an instance of the DeliveryClient client
DeliveryClient client = new DeliveryClient("975bf280-fd91-488c-994c-2f04416e5ee3");
```

You can also provide the project ID and other parameters by passing the [`DeliveryOptions`](https://github.com/Kentico/delivery-sdk-java/blob/master/src/main/java/com/kenticocloud/delivery/DeliveryOptions.java) object to the class constructor. The `DeliveryOptions` object can be used to set the following parameters:

* `setPreviewApiKey(String)` – sets the Delivery Preview API key.
* `setProductionApiKey(String)` - sets the Delivery Client key for secured access.
* `setProjectId(String)` – sets the project identifier.
* `setUsePreviewApi(boolean)` – determines whether to use the Delivery Preview API.
* `setWaitForLoadingNewContent(boolean)` – makes the client instance wait while fetching updated content, useful when acting upon [webhook calls](https://developer.kenticocloud.com/docs/webhooks#section-requesting-new-content).

The `DeliveryOptions.builder()` can also simplify creating a `DeliveryClient`:

```java
DeliveryClient client = new DeliveryClient(DeliveryOptions.builder()
                                                          .projectId("975bf280-fd91-488c-994c-2f04416e5ee3")
                                                          .productionApiKey("secured key")
                                                          .build());
```

Once you create a `DeliveryClient`, you can start querying your project repository by calling methods on the client instance. See [Basic querying](#basic-querying) for details.

### Filtering retrieved data

The SDK supports full scale of the API querying and filtering capabilities as described in the [API reference](https://developer.kenticocloud.com/reference#content-filtering).

```java
// Retrieves a list of the specified elements from the first 10 content items of
// the 'brewer' content type, ordered by the 'product_name' element value
ContentItemsListingResponse response = client.getItems(
    DeliveryParameterBuilder.params()
        .language("es-ES")
        .filterEquals("system.type", "brewer")
        .projection("image", "price", "product_status", "processing")
        .page(null, 10)
        .orderByAsc("elements.product_name")
        .build()
);
```

### Previewing unpublished content

To retrieve unpublished content, you need to create a `DeliveryClient` with both Project ID and Preview API key. Each Kentico Cloud project has its own Preview API key. 

```Java
// Note: Within a single project, we recommend that you work with only
// either the production or preview Delivery API, not both.
DeliveryClient client = new DeliveryClient("YOUR_PROJECT_ID", "YOUR_PREVIEW_API_KEY");
```

For more details, see [Previewing unpublished content using the Delivery API](https://developer.kenticocloud.com/docs/preview-content-via-api).

## Basic querying

Once you have a `DeliveryClient` instance, you can start querying your project repository by calling methods on the instance.

```java
// Retrieves a single content item
ContentItemResponse response = client.getItem("about_us");

// Retrieves a list of all content items
ContentItemsListingResponse listingResponse = client.getItems();
```

## Response structure

For full description of single and multiple content item JSON response formats, see our [API reference](https://developer.kenticocloud.com/reference#response-structure).

### Single content item response

When retrieving a single content item, you get an instance of the `ContentItemResponse` class. This class represents the JSON response from the Delivery API endpoint and contains the requested `ContentItem` as a property.

### Multiple content items response

When retrieving a list of content items, you get an instance of the `ContentItemsListingResponse`. This class represents the JSON response from the Delivery API endpoint and contains:

* `getPagination()` returns a `Pagination` object with information about the following:
  * `getSkip()`: requested number of content items to skip
  * `getLimit()`: requested page size
  * `getCount()`: the total number of retrieved content items
  * `getNextPage()`: the URL of the next page
* A list of the requested content items

### ContentItem structure

The `ContentItem` class provides the following:

* `getSystem()` returns a `System` object with metadata such as code name, display name, type, or sitemap location.
* `getElements()` returns a Map containing all the elements included in the response keyed by code names.
* Methods for easier access to certain types of content elements such as linked items, or assets.

## Getting content item properties

You can access information about a content item (i.e., its ID, codename, name, location in sitemap, date of last modification, and its content type codename) by using the `System` object.

```java
// Retrieves name of an article content item
articleItem.getSystem().getName()

// Retrieves codename of an article content item
articleItem.getSystem().getCodename()

// Retrieves name of the content type of an article content item
articleItem.getSystem().getType()
```

## Getting element values

The SDK provides methods for retrieving content from content elements such as Asset, Text, Rich Text, Multiple choice, etc.

### Text and Rich text

For text elements, you can use the `getString` method.

```java
// Retrieves an article text from the 'body_copy' Text element
articleItem.getString("body_copy")
```

The Rich text element can contain links to other content items within your project. See [Resolving links to content items](https://github.com/Kentico/delivery-sdk-java/wiki/Resolving-links-to-content-items) for more details.

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
 getCodename() | The codename of the option. | `dry__natural_`

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

## Further information

For more developer resources, visit the Kentico Cloud Developer Hub at <https://developer.kenticocloud.com>.

### Building the sources

Prerequisites:

**Required:**
Java 8 SDK (Oracle & OpenJDK both tested and supported)

Ensure your `JAVA_HOME` environment is set.  Then build the project via the provided Gradle wrapper.
```
./gradlew clean build
```

Optional:
[JetBrains IntelliJ Idea](https://www.jetbrains.com/idea/) Import the Gradle project to sync up dependencies.

## Feedback & Contributing

Check out the [contributing](https://github.com/Kentico/delivery-sdk-net/blob/master/CONTRIBUTING.md) page to see the best places to file issues, start discussions, and begin contributing.

### Wall of Fame
We would like to express our thanks to the following people who contributed and made the project possible:

- [Adam J. Weigold](https://github.com/aweigold)
- [Tommaso Garuglieri](https://github.com/GaruGaru)
- [Gabriel Cunha](https://github.com/cunhazera)

Would you like to become a hero too? Pick an [issue](https://github.com/Kentico/delivery-sdk-java/issues) and send us a pull request!

![Analytics](https://kentico-ga-beacon.azurewebsites.net/api/UA-69014260-4/Kentico/delivery-sdk-java?pixel)
