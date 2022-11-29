# Kontent.ai Sample Spring Android (Java)

The showcase of the [Android](https://www.android.com/) application displaying data from Kontent.

![Application walkthrough](./android-app-showcase.gif)

## Get started

First, [build the whole monorepo](../README.md#Build-and-Test) and then you could install the app from:

* Debug version - `/sample-app-android/build/outputs/apk/debug/sample-app-android-debug.apk`
* Unsigned release version - `/sample-app-android/build/outputs/apk/release/sample-app-android-release-unsigned.apk`

> Alternatively, you could run [the application on your Android device](https://developer.android.com/studio/run), alternatively [use the Android emulator](https://developer.android.com/studio/run/emulator).

## Features

Application is showcasing listing-detail screens with three content types `Article`, `Coffee`, and `Cafe`. The listings are selectable in the Menu.

This application demonstrates it is possible to load data from Kontent.ai using Java SDK. It is not meant to be used as a boilerplate.

>⚠ There are two Android-specific rules you need to follow in order for the Delivery SDK to work correctly. First is to [disable template engine integration when instantiating the client](../kontent-delivery/README.md#1-initialize-the-delivery-client-for-android-development) and the second is to [avoid using `scanClasspathForMappings` method](../kontent-delivery/README.md#2-register-strongly-typed-models).

### Data loading using RxJava

All the data loaded from Kontent.ai is using [RxJava](https://github.com/ReactiveX/RxJava) approach. Basically, the `CompletionStage` returned from Kontent.ai Java SDK is wrapped to the `Observable`. This allows to easily synchronize IO and UI operation with proper thread. Take a look i.e. to [`ArticlesKontentSource#getArticles` method](src/main/java/kontent/ai/data/source/articles/ArticlesKontentSource.java#L40) to see the actual implementation using `io.reactivex.rxjava3:rxjava` and `io.reactivex.rxjava3:rxandroid` packages.

```java
Observable.fromCompletionStage(client.getItems(Article.class))
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Observer<List<Article>>() {
          // ...
        });
```

### Using Appetize

The application is preset to be able to consume data from different project (with the same scheme as in sample project). This feature is used for showcasing the application using [Appetize](https://appetize.io) service.

To be able to consume the external configuration, the app checks whether the [Appetize Playback option - params](https://docs.appetize.io/core-features/playback-options) where provided and if so, it will reinitialize the client with specific project ID (and preview API key if provided).

The decision logic is stored in `BaseActivity.java#onCreate` method.

If you want to use the Appetize you can create an Appetize iframe and provide `KontentProjectId` (and `KontentPreviewApiKey` if you want to use unpublished content) like this:

```js
  const queryParams = new URLSearchParams(window.location.search);

    if (!queryParams.has("projectId")) {
        console.error(`ProjectId parameter is not set in Content Type's preview URL`);
    }

    // contract defined by app's Kontent.ai client, user defaults, and Appetize's param API
    const appetizeParams = {
        KontentProjectId: queryParams.get("projectId"),
    };

    // if URL's got preview API key, add it to appetize params too
    if (queryParams.has("previewApiKey")) {
        appetizeParams.KontentPreviewApiKey = queryParams.get("previewApiKey")
    }

    // we want to pass projectId and API key params to appetize
    const appetizeParamsStringifiedEncoded = encodeURIComponent(JSON.stringify(appetizeParams));
    const appetizeUrl = `https://appetize.io/embed/<YOUR APP ID FROM APPETIZE>?device=pixel4&orientation=portrait&screenOnly=true&xdocMsg=true&params=${appetizeParamsStringifiedEncoded}`;

    const appetizeIframe = document.createElement("iframe");
    appetizeIframe.setAttribute("id", "appetize-iframe");
    appetizeIframe.setAttribute("src", appetizeUrl);
    appetizeIframe.setAttribute("height", "609");
    appetizeIframe.setAttribute("width", "277");
    appetizeIframe.setAttribute("scrolling", "no");
    appetizeIframe.setAttribute("title", "iOS Preview Content");
    appetizeIframe.setAttribute("sandbox", "allow-scripts allow-same-origin");

    // and inject the iframe to DOM
    const appetizeContainer = document.getElementById("appetize-container");
    appetizeContainer.appendChild(appetizeIframe);
```
