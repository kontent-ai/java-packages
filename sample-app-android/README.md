# Kontent Sample Spring Android (Java)

Showcase of the [Android](https://www.android.com/) application displaying data from Kontent.

![Application walkthrough](./adroid-app-showcase.gif)

## Get started

First [build the whole monorepo](../README.md#Build-and-Test) and then you could install the app from:

* Debug version - `/sample-app-android/build/outputs/apk/debug/sample-app-android-debug.apk`
* Unsigned release version - `/sample-app-android/build/outputs/apk/release/sample-app-android-release-unsigned.apk`

> Alternatively you could run [the application on you Android device](https://developer.android.com/studio/run), alternatively [use the Android emuulator](https://developer.android.com/studio/run/emulator).

## Features

Application is showcasing listing-detail screens wit three content types `Article`, `Coffee`, and `Cafe`. The listing are selectable in the Menu.

> This application is about to demonstrate it is possible to load data from Kentico Kontent using Java SDK. It is not meant to be used as a boilerplate.

### Strongly-typed models with models

This showcase is using models for `Article`, `Coffee`, and `Cafe` type. You could use [model generator](../kontent-delivery-generators/README.md) for generating models like that.

The app is registering these models in [DeliveryClientProvider.java](./src/main/java/com/github/kentico/delivery_android_sample/data/source/DeliveryClientProvider.java) and it is using `registerType` method to register the model to the client.

> ⚠ Method `scanClasspathForMappings` does not work in Android environment, because of the differences in Android Dalvik VM vs. Java VM the scanning library is not usable here. That is why `registerType` method should be used instead.

![Analytics](https://kentico-ga-beacon.azurewebsites.net/api/UA-69014260-4/Kentico/kontent-java-packages/sample-app-android?pixel)