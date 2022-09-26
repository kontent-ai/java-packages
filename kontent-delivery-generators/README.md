# Kontent model generator for Java

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

[![Javadocs](https://javadoc.io/badge/com.github.kentico/kontent-delivery-generators.svg)](https://javadoc.io/doc/com.github.kentico/kontent-delivery-generators)
[![Maven Central](https://img.shields.io/maven-central/v/com.github.kentico/kontent-delivery-generators)](https://oss.sonatype.org/content/groups/public/com/github/kentico/kontent-delivery-generators)

[![GitHub Discussions](https://img.shields.io/badge/GitHub-Discussions-FE7A16.svg?style=popout&logo=github)](https://github.com/Kentico/Home/discussions)
[![Stack Overflow](https://img.shields.io/badge/Stack%20Overflow-ASK%20NOW-FE7A16.svg?logo=stackoverflow&logoColor=white)](https://stackoverflow.com/tags/kentico-kontent)

This tool generates strongly-typed models based on Content Types in a Kontent by Kentico project. The models are supposed to be used together with the [Kontent Delivery SDK for Java](../kontent-delivery/README.md). Please read the [documentation](</wiki/Working-with-Strongly-Typed-Models-(aka-Code-First-Approach)#customizing-the-strong-type-binding-logic>) to see all benefits of this approach.

## Get started

1. Create a new instance of Code generator

    ```java
    CodeGenerator generator = new CodeGenerator(
    "975bf280-fd91-488c-994c-2f04416e5ee3",
    'com.kentico.kontent.test.springapp.models',
    file('src/main/java')
    );
    ```

1. Generate models

    ```java
    List<JavaFile> sources = generator.generateSources(client);
    ```

1. Write the sources to the output directory

    ```java
    generator.writeSources(sources);
    ```

### Run as a gradle task

Add to your `build.gradle`

```groovy
import com.squareup.javapoet.JavaFile
import DeliveryClient
import DeliveryOptions
import kentico.kontent.delivery.generators.CodeGenerator

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath('com.github.kentico:kontent-delivery-generators:latest.release')
    }
}

// showcase task
task generateModels {
    doLast {

        // The most complex solution, you could configure the client as you want
        // i.e. set preview API key
        DeliveryOptions options = new DeliveryOptions();
        options.setProjectId("975bf280-fd91-488c-994c-2f04416e5ee3");
        DeliveryClient client = new DeliveryClient(options);

        CodeGenerator generator = new CodeGenerator(
            options.getProjectId(),
            'com.kentico.kontent.test.springapp.models',
            file('src/main/java')
        );
        List<JavaFile> sources = generator.generateSources(client);
        generator.writeSources(sources);
    }
}

```
