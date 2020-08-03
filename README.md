# Kontent Java Packages

Monorepo with Java Kontent packages.

## Packages

|                               Package                                | Summary                                                                                                                          |                                                                                                             Version                                                                                                             |
| :------------------------------------------------------------------: | :------------------------------------------------------------------------------------------------------------------------------- | :-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------: |
|        [Kontent Delivery Java SDK](/kontent-delivery#readme)         | The Kentico Kontent Delivery Java SDK is a client library used for retrieving content from [Kentico Kontent](https://kontent.ai) |            [![Download](https://api.bintray.com/packages/kentico/kontent-java-packages/kontent-delivery/images/download.svg)](https://bintray.com/kentico/kontent-java-packages/kontent-delivery/_latestVersion)            |
|      [Kontent Generators](/kontent-delivery-generators#readme)       | This tool generates strongly-typed models based on Content Types in a Kentico Kontent project.                                   | [![Download](https://api.bintray.com/packages/kentico/kontent-java-packages/kontent-delivery-generators/images/download.svg)](https://bintray.com/kentico/kontent-java-packages/kontent-delivery-generators/_latestVersion) |
|   [Kontent Sample Spring Boot app](/sample-app-spring-boot#readme)   | Showcase of the [Spring boot](https://spring.io/projects/spring-boot) application displaying data from Kontent.                  |                                                                                           [source](/sample-app-spring-boot/README.md)                                                                                           |
| [Kontent Sample Gradle Console app](/test-gradle-console-app#readme) | Simple Java console application showcasing how to load data from Kontent.                                                        |                                                                                          [source](/test-gradle-console-app/README.md)                                                                                           |

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

  > This command will build whole solution and run all tests in this monorepo.

  ```sh
  cd kontent-java-packages
  ./gradlew clean build
  ```

> :bulb: Then ideal next step is to test out the [Kontent Sample Spring Boot app](/sample-app-spring-boot#readme) or load the project in In IDE ([IntelliJ IDEA](https://www.jetbrains.com/idea/) recommended) and run/debug tests in [Kontent Delivery Java SDK](/kontent-delivery#readme).

## Publishing

// TODO finish publishing

// TODO add analytics
