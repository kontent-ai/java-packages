# Kontent.ai Sample Spring Android (Kotlin)

The showcase of the [Android](https://www.android.com/) application written in Kotlin using [Kotlin Coroutines](https://kotlinlang.org/docs/reference/coroutines-overview.html) for data fetching from Kontent.

## Get started

First, [build the whole monorepo](../README.md#Build-and-Test), and then you could install the app from:

* Debug version - `/sample-app-android-kotlin/build/outputs/apk/debug/sample-app-android-kotlin-debug.apk`
* Unsigned release version - `/sample-app-android-kotlin/build/outputs/apk/release/sample-app-android-kotlin-release-unsigned.apk`

> Alternatively, you could run [the application on your Android device](https://developer.android.com/studio/run), alternatively [use the Android emulator](https://developer.android.com/studio/run/emulator).

## Features

Application is showcasing a simple listing screen with `Article` content type.

This application demonstrates loading data from Kontent.ai using Java SDK in Kotlin application using [Kotlin Coroutines](https://kotlinlang.org/docs/reference/coroutines-overview). It is not meant to be used as a boilerplate.

>⚠ There are two Android-specific rules you need to follow in order for the Delivery SDK to work correctly. First is to [disable template engine integration when instantiating the client](https://github.com/kontent-ai/java-packages/blob/master/kontent-delivery/README.md#1-initialize-the-delivery-client-for-android-development) and the second is to [avoid using `scanClasspathForMappings` method](https://github.com/kontent-ai/java-packages/blob/master/kontent-delivery/README.md#2-register-strongly-typed-models).

### Data loading using Kotlin coroutines

The data from Kontent.ai is fetched using the [Kotlin Coroutines](https://kotlinlang.org/docs/reference/coroutines-overview). The `CompletionStage` returned from the Java SDK is using `org.jetbrains.kotlinx:kotlinx-coroutines-jdk8` package to integrate with Kotlin coroutines API. The `org.jetbrains.kotlinx:kotlinx-coroutines-android` package is used to provide a simple API to synchronize the coroutines and Android lifecycle. This allows to easily synchronize IO and UI operations with proper thread. Take a look at [`ArticlesActivity::onCreate` method](./src/main/java/kontent/ai/delivery/sample/dancinggoat/app/articles/ArticlesActivity.kt#L25) to see the actual implementation.

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
