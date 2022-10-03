# Kontent Sample Spring Android (Java)

The showcase of the [Android](https://www.android.com/) application displaying data from Kontent.

![Application walkthrough](./adroid-app-showcase.gif)

## Get started

First, [build the whole monorepo](../README.md#Build-and-Test) and then you could install the app from:

* Debug version - `/sample-app-android/build/outputs/apk/debug/sample-app-android-debug.apk`
* Unsigned release version - `/sample-app-android/build/outputs/apk/release/sample-app-android-release-unsigned.apk`

> Alternatively, you could run [the application on your Android device](https://developer.android.com/studio/run), alternatively [use the Android emulator](https://developer.android.com/studio/run/emulator).

## Features

Application is showcasing listing-detail screens with three content types `Article`, `Coffee`, and `Cafe`. The listings are selectable in the Menu.

This application demonstrates it is possible to load data from Kontent using Java SDK. It is not meant to be used as a boilerplate.

>⚠ There are two Android-specific rules you need to follow in order for the Delivery SDK to work correctly. First is to [disable template engine integration when instantiating the client](../kontent-delivery/README.md#1-initialize-the-delivery-client-for-android-development) and the second is to [avoid using `scanClasspathForMappings` method](../kontent-delivery/README.md#2-register-strongly-typed-models).

### Data loading using RxJava

All the data loaded from Kontent is using [RxJava](https://github.com/ReactiveX/RxJava) approach. Basically, the `CompletionStage` returned from Kontent Java SDK is wrapped to the `Observable`. This allows to easily synchronize IO and UI operation with proper thread. Take a look i.e. to [`ArticlesKontentSource#getArticles` method](src/main/java/kontent/ai/data/source/articles/ArticlesKontentSource.java#L40) to see the actual implementation using `io.reactivex.rxjava3:rxjava` and `io.reactivex.rxjava3:rxandroid` packages.

```java
Observable.fromCompletionStage(client.getItems(Article.class))
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Observer<List<Article>>() {
          // ...
        });
```
