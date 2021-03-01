# Kontent Java Packages

[![GitHub Discussions](https://img.shields.io/badge/GitHub-Discussions-FE7A16.svg?style=popout&logo=github)](https://github.com/Kentico/Home/discussions)
[![Stack Overflow](https://img.shields.io/badge/Stack%20Overflow-ASK%20NOW-FE7A16.svg?logo=stackoverflow&logoColor=white)](https://stackoverflow.com/tags/kentico-kontent)

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

> To publish a new version, it is required to have write permissions for this repository (to be able to create releases) and access to the [Nexus Repository Manager](https://oss.sonatype.org/).


1. Verify that everything in the branch is ready to be published and the [build and tests](https://github.com/Kentico/kontent-java-packages/actions/workflows/gradle.yml) are passing.
1. Create new GitHub release - **the tag name of the release will be used as a version**
    * If you define tag name with "-SNAPSHOT" suffix i.e. `4.4.0-SNAPSHOT` artifact will be published to the `https://oss.sonatype.org/content/repositories/snapshots/`, so that you could use it when you want to try out the beta version.

    * The creation of a release triggers the [Publish Github workflow](https://github.com/Kentico/kontent-java-packages/actions/workflows/publish.yml) and creates and publishes the artifacts to "Staging" repositories on Nexus repository manager.

1. Log in to the [Nexus Repository Manager](https://oss.sonatype.org/).
1. Select "Staging repositories", verify the repository content (*sometimes it takes a couple of minutes until the repository is visible in the Nexus Repository Manager UI*).
1. Close the Staging repository
1. Release the Closed repository
1. Increase the patch version and append `-SNAPSHOT` in the bottom of `/build.gradle` file (i.e. `4.4.1-SNAPSHOT`).

> 💡 This is just an abbreviated description of the publishing process. If you want to see detailed publishing description, checkout the [wiki page "Publishing process"](https://github.com/Kentico/kontent-java-packages/wiki/Release-process).
