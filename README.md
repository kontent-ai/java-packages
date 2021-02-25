# Kontent Java Packages

Monorepo with Java Kontent packages.

## Packages

|                                 Package                                  | Summary                                                                                                                                                                                                     |                                                                                                           Version                                                                                                           |
| :----------------------------------------------------------------------: | :---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | :-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------: |
|          [Kontent Delivery Java SDK](/kontent-delivery#readme)           | The Kentico Kontent Delivery Java SDK is a client library used for retrieving content from [Kentico Kontent](https://kontent.ai)                                                                            |            [![Download](https://api.bintray.com/packages/kentico/kontent-java-packages/kontent-delivery/images/download.svg)](https://bintray.com/kentico/kontent-java-packages/kontent-delivery/_latestVersion)            |
|        [Kontent Generators](/kontent-delivery-generators#readme)         | This tool generates strongly-typed models based on Content Types in a Kentico Kontent project.                                                                                                              | [![Download](https://api.bintray.com/packages/kentico/kontent-java-packages/kontent-delivery-generators/images/download.svg)](https://bintray.com/kentico/kontent-java-packages/kontent-delivery-generators/_latestVersion) |
|     [Kontent Sample Spring Boot app](/sample-app-spring-boot#readme)     | Showcase of the [Spring boot](https://spring.io/projects/spring-boot) application displaying data from Kontent.                                                                                             |                                                                                         [source](/sample-app-spring-boot/README.md)                                                                                         |
|   [Kontent Sample Gradle Console app](/test-gradle-console-app#readme)   | Simple Java console application showcasing how to load data from Kontent.                                                                                                                                   |                                                                                        [source](/test-gradle-console-app/README.md)                                                                                         |
|     [Kontent Sample Android app (Java)](/sample-app-android#readme)      | Showcase of the [Android](https://www.android.com/) application written in Java using [RxJava](https://github.com/ReactiveX/RxJava) for data fetching from Kontent.                                         |                                                                                           [source](/sample-app-android/README.md)                                                                                           |
| [Kontent Sample Android app (Kotlin)](/sample-app-android-kotlin#readme) | Showcase of the [Android](https://www.android.com/) application written in Kotlin using [Kotlin Coroutines](https://kotlinlang.org/docs/reference/coroutines-overview.html) for data fetching from Kontent. |                                                                                       [source](/sample-app-android-kotlin/README.md)                                                                                        |

## Development

If you wish to find out more about the project specification. Check out the [Project information](/PROJECT.md).

### Prerequisites

**Required:**
Java 8 SDK (Oracle & OpenJDK both tested and supported)

### Build and Test

- Ensure your `JAVA_HOME` environment is set.
- Clone this repository

  ```sh
  git clone https://github.com/Kentico/kontent-java-packages
  ```

- Enter the cloned repository and build the project via the provided Gradle wrapper.

  > To grant execution rights for `gradlew` binary, you could use `chmod a+x ./gradlew` which allows execution to everybody.

  ```sh
  cd kontent-java-packages
  ./gradlew clean build
  ```

  > The command will build whole solution and run all tests in this monorepo.

#### :bulb: Next steps

The ideal next step is to test out the [Kontent Sample Spring Boot app](/sample-app-spring-boot#readme) or load the project in In IDE ([IntelliJ IDEA](https://www.jetbrains.com/idea/) recommended) and run/debug tests in [Kontent Delivery Java SDK](/kontent-delivery#readme).

## Publishing

> If you want to publish the new version, you need to have an access to [Bintray's Kentico organization](https://bintray.com/kentico).

1. Copy the `local.properties.template` file in this directory to `local.properties` (which will be idnored by Git)).
1. Set each variable on `local.properties` from the password manager, or contact @Kentico/developer-relations team to get them.
1. Set version you want to publish in [`build.gradle`](./build.gradle#L69).
1. Build the solution `./gradlew build`.
1. Run `./gradlew bintrayUpload`.

