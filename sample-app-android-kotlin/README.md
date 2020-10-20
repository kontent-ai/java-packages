# Kontent Sample Spring Android (Kotlink)

The showcase of the [Android](https://www.android.com/) application written in Kotlin using [Kotlin Coroutines](https://kotlinlang.org/docs/reference/coroutines-overview.html) for data fetching from Kontent.

![Application walkthrough](./adroid-app-showcase.gif)

## Get started

First, [build the whole monorepo](../README.md#Build-and-Test), and then you could install the app from:

* Debug version - `/sample-app-android-kotlin/build/outputs/apk/debug/sample-app-android-kotlin-debug.apk`
* Unsigned release version - `/sample-app-android-kotlin/build/outputs/apk/release/sample-app-android-kotlin-release-unsigned.apk`

> Alternatively, you could run [the application on your Android device](https://developer.android.com/studio/run), alternatively [use the Android emulator](https://developer.android.com/studio/run/emulator).

## Features

Application is showcasing a simple listing screen with `Article` content type.

> This application is about to demonstrate loading data from Kentico Kontent using Java SDK in Kotlin application using [Kotlin Coroutines](https://kotlinlang.org/docs/reference/coroutines-overview). It is not meant to be used as a boilerplate.


### Instantiating Delivery client

It is important to instantiate the delivery with the constructor that disables the template engine. The template engine is meant to be used on the web platform. **Use constructor `DeliveryClient#DeliveryClient(DeliveryOptions, TemplateEngineConfig)` and set second parameter to `null**`** for Android development.

Use the following constructor (see the [sample](./src/main/java/kentico/kontent/delivery/sample/dancinggoat/data/DeliveryClientProvider.kt)):

```java
DeliveryClient client = new DeliveryClient(new DeliveryOptions(AppConfig.KONTENT_PROJECT_ID), null);
```

### Data loading using Kotlin coroutines

The data from Kentico Kontent is fetched using the [Kotlin Coroutines](https://kotlinlang.org/docs/reference/coroutines-overview). Basically, the `CompletionStage` returned from the Java SDK is using `org.jetbrains.kotlinx:kotlinx-coroutines-jdk8` package to integrate with Kotlin coroutines API. The `org.jetbrains.kotlinx:kotlinx-coroutines-android` package is used to provide a simple API to synchronize the coroutines and Android lifecycle. This allows to easily synchronize IO and UI operations with proper thread. Take a look at [`ArticlesActivity::onCreate` method](./src/main/java/kentico/kontent/delivery/sample/dancinggoat/app/articles/ArticlesActivity.kt#L25) to see the actual implementation.

```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.articles_activity)

    listView = findViewById<ListView>(R.id.articles_list_view);

    GlobalScope.launch(Dispatchers.Main) { // Threads Synchronization
        val articles = withContext(Dispatchers.IO) { loadArticles() }
        displayArticles(articles);
    }
}

@WorkerThread
private suspend fun loadArticles(): MutableList<Article> {
    val client = DeliveryClientProvider.client;

    val params = DeliveryParameterBuilder.params()
        .filterEquals("system.type", "article")
        .build()
    return client.getItems(Article::class.java, params).await();
}
```

### Strongly-typed models with models

This showcase is using a model for `Article` type. You could use the [model generator](../kontent-delivery-generators/README.md) for generating models like that.

The app is registering these models in [DeliveryClientProvider.java](./src/main/java/kentico/kontent/delivery/sample/dancinggoat/data/DeliveryClientProvider.kt) and it is using `registerType` method to register the model to the client.

> ⚠ Method `scanClasspathForMappings` does not work in the Android environment, because of the differences in Android Dalvik VM vs. Java VM the scanning library is not usable here. That is why `registerType` method should be used instead.

![Analytics](https://kentico-ga-beacon.azurewebsites.net/api/UA-69014260-4/Kentico/kontent-java-packages/sample-app-android-kotlin?pixel)
