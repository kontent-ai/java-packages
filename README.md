# Kontent.ai Sample app appetize

🛈 This repository contains Kontent.ai's internal code that is of no use to the general public. Please explore our [other repositories](https://github.com/kontent-ai).

<!-- ABOUT THE PROJECT -->

## About The Project

Android simulator that is loaded in the HTML page for SafeLife Android Kotlin project

## How to run locally

### Prerequisites

**Required:**

- Java 8 SDK (Oracle & OpenJDK both tested and supported)
- [Android SDK](https://developer.android.com/studio#downloads) for the sample application (minimal version 28) - command line tools would be sufficient

### Build and Test

- Ensure your `JAVA_HOME` environment is set.
- Clone this repository

  ```sh
  git clone https://github.com/kontent-ai/java-packages
  ```

- Enter the cloned repository

  ```sh
  cd kontent-java-packages
  ```

- Copy `local.properties.template` file in this directory to `local.properties` (which will be ignored by Git)

- Set the `sdk.dir` variable Android SDK location

- Build the project via the provided Gradle wrapper.

  > To grant execution rights for `gradlew` binary, you could use `chmod a+x ./gradlew` which allows execution to everybody.

  ```sh
  ./gradlew clean build
  ```

You could install the app from:

- Debug version - `/sample-app-android-kotlin/build/outputs/apk/debug/sample-app-android-kotlin-debug.apk`
- Unsigned release version - `/sample-app-android-kotlin/build/outputs/apk/release/sample-app-android-kotlin-release-unsigned.apk`

> Alternatively, you could run [the application on your Android device](https://developer.android.com/studio/run), alternatively [use the Android emulator](https://developer.android.com/studio/run/emulator).

## How to update app on Appetize

1. Run locally first
2. Use debug version `.apk` from above
3. Log in to [appetize.io](appetize.io)
4. Go to **Dashboard**
5. Click **manage** SafeLife - _double-check you are editing the correct app_
6. **Select file** in **Upload a new build** section
7. Select already created archive and upload
8. Test preview is correctly rendered in the web spotlight, the app works as intended. Check the `projectId` and `previewApiKey` are reflected in the app