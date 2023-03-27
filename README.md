# Kontent.ai Java Packages

[![Stack Overflow](https://img.shields.io/badge/Stack%20Overflow-ASK%20NOW-FE7A16.svg?logo=stackoverflow&logoColor=white)](https://stackoverflow.com/tags/kontent-ai)
[![Discord](https://img.shields.io/discord/821885171984891914?color=%237289DA&label=Kontent.ai%20Discord&logo=discord)](https://discord.gg/SKCxwPtevJ)

Monorepo with Java Kontent.ai packages.

## Packages

|                                   Package                                   | Summary                                                                                                                                                                                                        |                                                                                       Version                                                                                        |
| :-------------------------------------------------------------------------: | :------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | :----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------: |
|            [Kontent.ai Delivery Java SDK](/delivery-sdk#readme)             | The Kontent.ai Delivery Java SDK is a client library used for retrieving content from [Kontent.ai](https://kontent.ai)                                                                                         |           [![Maven Central](https://img.shields.io/maven-central/v/ai.kontent/delivery-sdk)](https://s01.oss.sonatype.org/content/groups/public/ai/kontent/delivery-sdk/)            |
|          [Kontent.ai Generators](/delivery-sdk-generators#readme)           | This tool generates strongly-typed models based on Content Types in a Kontent.ai project.                                                                                                                      | [![Maven Central](https://img.shields.io/maven-central/v/ai.kontent/delivery-sdk-generators)](https://s01.oss.sonatype.org/content/groups/public/ai/kontent/delivery-sdk-generators) |
|     [Kontent.ai Sample Spring Boot app](/sample-app-spring-boot#readme)     | Showcase of the [Spring boot](https://spring.io/projects/spring-boot) application displaying data from Kontent.ai.                                                                                             |                                                                     [source](/sample-app-spring-boot/README.md)                                                                      |
|   [Kontent.ai Sample Gradle Console app](/test-gradle-console-app#readme)   | Simple Java console application showcasing how to load data from Kontent.ai.                                                                                                                                   |                                                                     [source](/test-gradle-console-app/README.md)                                                                     |
|     [Kontent.ai Sample Android app (Java)](/sample-app-android#readme)      | Showcase of the [Android](https://www.android.com/) application written in Java using [RxJava](https://github.com/ReactiveX/RxJava) for data fetching from Kontent.ai.                                         |                                                                       [source](/sample-app-android/README.md)                                                                        |
| [Kontent.ai Sample Android app (Kotlin)](/sample-app-android-kotlin#readme) | Showcase of the [Android](https://www.android.com/) application written in Kotlin using [Kotlin Coroutines](https://kotlinlang.org/docs/reference/coroutines-overview.html) for data fetching from Kontent.ai. |                                                                    [source](/sample-app-android-kotlin/README.md)                                                                    |
| [Kontent.ai Java packages docs](/docs#readme) | Documentation for Kontent.ai Java packages |[source](/docs/README.md)|

## Development

If you wish to find out more about the project specification. Check out the [Project information](/PROJECT.md).

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

  > The command will build whole solution and run all tests in this monorepo.

#### :bulb: Next steps

The ideal next step is to test out the [Kontent.ai Sample Spring Boot app](/sample-app-spring-boot#readme) or load the
project in In IDE ([IntelliJ IDEA](https://www.jetbrains.com/idea/) recommended) and run/debug tests
in [Kontent.ai Delivery Java SDK](/delivery-sdk#readme).

## Publishing

> To publish a new version, it is required to have write permissions for this repository (to be able to create releases) and access to the [Nexus Repository Manager](https://s01.oss.sonatype.org/).

1. Verify that everything in the branch is ready to be published and
   the [build and tests](https://github.com/kontent-ai/java-packages/actions/workflows/gradle.yml) are passing.
1. Create new GitHub release - **the tag name of the release will be used as a version**

   - If you define tag name with "-SNAPSHOT" suffix i.e. `5.0.0-SNAPSHOT` artifact will be published to
     the `https://s01.oss.sonatype.org/content/repositories/snapshots/`, so that you could use it when you want to try out
     the beta version.

   - The creation of a release triggers
     the [Publish Github workflow](https://github.com/kontent-ai/java-packages/actions/workflows/publish.yml) and
     creates and publishes the artifacts to "Staging" repositories on Nexus repository manager.

1. Log in to the [Nexus Repository Manager](https://s01.oss.sonatype.org/).
1. Select "Staging repositories", verify the repository content (_sometimes it takes a couple of minutes until the
   repository is visible in the Nexus Repository Manager UI_).
1. Close the Staging repository
1. Release the Closed repository
1. Increase the patch version and append `-SNAPSHOT` in the bottom of `/build.gradle` file (i.e. `4.4.1-SNAPSHOT`).

> 💡 This is just an abbreviated description of the publishing process. If you want to see the detailed publishing description, checkout the [wiki page "Publishing process"](./docs/Release-process.md).
