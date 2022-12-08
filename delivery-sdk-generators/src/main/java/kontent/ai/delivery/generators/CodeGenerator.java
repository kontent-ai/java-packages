package kontent.ai.delivery.generators;

import com.google.common.base.CaseFormat;
import com.squareup.javapoet.*;
import kontent.ai.delivery.*;
import kontent.ai.delivery.System;

import javax.lang.model.element.Modifier;
import java.io.File;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Generates java source files using the Kontent.ai Type listing endpoint in the Delivery API
 */
public class CodeGenerator {

    private static final String DELIVERY_PACKAGE = "kontent.ai.delivery";
    private static final String JAVA_UTIL_PACKAGE = "java.util";
    private static final String SYSTEM = "system";

    String projectId;
    String packageName;
    File outputDir;

    /**
     * Constructs the CodeGenerator
     * @param projectId the project id from your Kontent.ai account
     * @param packageName the package to place the generated models under
     * @param outputDir the source root to place the generated models
     * @throws UnsupportedOperationException when a there is a problem with the outputDir
     */
    public CodeGenerator(String projectId, String packageName, File outputDir) {
        this.projectId = projectId;
        this.packageName = packageName;
        this.outputDir = outputDir;
        if (!outputDir.exists() && !outputDir.mkdirs()){
            throw new UnsupportedOperationException(
                    String.format("Unable to create directory %s", outputDir.getAbsolutePath()));
        }
        if (!outputDir.isDirectory()) {
            throw new UnsupportedOperationException(
                    String.format("%s exists and is not a directory", outputDir.getAbsolutePath()));
        }
    }

    /**
     * Returns a list of specifications of the sources representing the types in your Kontent.ai account
     * @return A list of specifications
     * @throws ExecutionException when a problem occurs communicating with the Kontent.ai API
     * @throws InterruptedException when a problem occurs communicating with the Kontent.ai API
     */
    public List<JavaFile> generateSources() throws ExecutionException, InterruptedException {
        return generateSources(new DeliveryClient(projectId));
    }

    /**
     * Returns a list of specifications of the sources representing the types in your Kontent.ai account.
     * The provided {@link DeliveryClient} param is useful for testing, however in most environments, the default
     * {@link #generateSources()} method should suffice.
     * @param client A DeliveryClient instance to use to generate the sources.
     * @return A list of specifications
     * @throws ExecutionException when a problem occurs communicating with the Kontent.ai API
     * @throws InterruptedException when a problem occurs communicating with the Kontent.ai API
     */
    public List<JavaFile> generateSources(DeliveryClient client) throws ExecutionException, InterruptedException {
        return generateSources(client.getTypes().toCompletableFuture().get().getTypes());
    }

    /**
     * Returns a list of specifications of the sources representing the types in your Kontent.ai account.
     * The provided List of {@link ContentType} param is useful for testing, however in most environments, the default
     * {@link #generateSources()} method should generally be the only method invoking this.
     * @param types A List of ContentType to generate the sources from
     * @return A list of specifications
     */
    public List<JavaFile> generateSources(List<ContentType> types) {
        List<JavaFile> sources = new ArrayList<>();
        for (ContentType type : types) {
            sources.add(generateSource(type));
        }
        return sources;
    }

    /**
     * Returns a specification of the source representing this type in your Kontent.ai account.
     * Invoking this directly may be useful for testing, however in most environments, the default
     * {@link #generateSources()} method should suffice.
     * @param type A ContentType to generate the source from
     * @return A specification
     */
    public JavaFile generateSource(ContentType type) {

        List<FieldSpec> fieldSpecs = new ArrayList<>();
        List<MethodSpec> methodSpecs = new ArrayList<>();

        for (Map.Entry<String, Element> element : type.getElements().entrySet()) {
            TypeName typeName = null;
            //Get the TypeName
            switch (element.getValue().getType()) {
                case "text" :
                case "rich_text" :
                case "url_slug" :
                case "custom" :
                    typeName = ClassName.get(String.class);
                    break;
                case "number" :
                    typeName = ClassName.get(Double.class);
                    break;
                case "multiple_choice" :
                    typeName = ParameterizedTypeName.get(
                            ClassName.get(JAVA_UTIL_PACKAGE, "List"),
                            ClassName.get(DELIVERY_PACKAGE, "Option"));
                    break;
                case "date_time" :
                    typeName = ClassName.get(ZonedDateTime.class);
                    break;
                case "asset" :
                    typeName = ParameterizedTypeName.get(
                            ClassName.get(JAVA_UTIL_PACKAGE, "List"),
                            ClassName.get(DELIVERY_PACKAGE, "Asset"));
                    break;
                case "modular_content" :
                    typeName = ParameterizedTypeName.get(
                            ClassName.get(JAVA_UTIL_PACKAGE, "List"),
                            ClassName.get(DELIVERY_PACKAGE, "ContentItem"));
                    break;
                case "taxonomy" :
                    typeName = ParameterizedTypeName.get(
                            ClassName.get(JAVA_UTIL_PACKAGE, "List"),
                            ClassName.get(DELIVERY_PACKAGE, "Taxonomy"));
                    break;
                default :
                    break;
            }
            if (typeName != null) {
                //Add the field
                String fieldName = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, element.getKey());
                Class annoClass = element.getValue().getType().equals("modular_content") ?
                        ContentItemMapping.class : ElementMapping.class;
                fieldSpecs.add(
                        FieldSpec.builder(typeName, fieldName)
                                .addAnnotation(
                                        AnnotationSpec.builder(annoClass)
                                                .addMember("value", "$S", element.getKey())
                                                .build())
                                .build()
                );
                //Add the getter
                String getterName = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, "get_" + element.getKey());
                methodSpecs.add(
                        MethodSpec.methodBuilder(getterName)
                                .addModifiers(Modifier.PUBLIC)
                                .returns(typeName)
                                .addStatement("return $N", fieldName)
                                .build()
                );
                //Add the setter
                String setterName = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, "set_" + element.getKey());
                methodSpecs.add(
                        MethodSpec.methodBuilder(setterName)
                                .addModifiers(Modifier.PUBLIC)
                                .addParameter(typeName, fieldName)
                                .addStatement("this.$N = $N", fieldName, fieldName)
                                .build()
                );
            }
        }

        //Add the System element
        fieldSpecs.add(FieldSpec.builder(ClassName.get(System.class), SYSTEM).build());
        methodSpecs.add(
                MethodSpec.methodBuilder("getSystem")
                        .addModifiers(Modifier.PUBLIC)
                        .returns(ClassName.get(System.class))
                        .addStatement("return $N", SYSTEM)
                        .build()
        );
        methodSpecs.add(
                MethodSpec.methodBuilder("setSystem")
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(ClassName.get(System.class), SYSTEM)
                        .addStatement("this.$N = $N", SYSTEM, SYSTEM)
                        .build()
        );

        //Create the class
        TypeSpec.Builder typeSpecBuilder = TypeSpec
                .classBuilder(CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, type.getSystem().getCodename()))
                .addModifiers(Modifier.PUBLIC)
                .addJavadoc("This code was generated by a " +
                        "<a href=\"https://github.com/kontent-ai/java-packages/tree/master/delivery-sdk-generators\">delivery-sdk-generators tool</a>\n")
                .addJavadoc("\n")
                .addJavadoc("Changes to this file may cause incorrect behavior and will be lost if the code is regenerated.\n")
                .addJavadoc("For further modifications of the class, create a separate file and extend this class.\n")
                .addAnnotation(AnnotationSpec.builder(ContentItemMapping.class)
                        .addMember("value", "$S", type.getSystem().getCodename())
                        .build());

        //Add the fields
        for (FieldSpec fieldSpec : fieldSpecs) {
            typeSpecBuilder.addField(fieldSpec);
        }

        //Add the methods
        for (MethodSpec methodSpec : methodSpecs) {
            typeSpecBuilder.addMethod(methodSpec);
        }

        TypeSpec typeSpec = typeSpecBuilder.build();

        return JavaFile.builder(packageName, typeSpec).build();
    }

    /**
     * Writes the provided specifications to the outputDir provided in the constructor.  This is generally called
     * after a call to {@link #generateSources()}, but is separated in case you need to make modifications to your
     * specifications before writing.
     * @param sources A list of specifications
     * @throws IOException when a problem occurs writing the source files, note some source may have been written when
     * this is thrown
     */
    public void writeSources(List<JavaFile> sources) throws IOException {
        for (JavaFile source : sources) {
            writeSource(source);
        }
    }

    /**
     * Writes the provided specification to the outputDir provided in the constructor.  This is generally called
     * after a call to {@link #generateSources()}, but is separated in case you need to make modifications to your
     * specification before writing.
     * @param source A specification
     * @throws IOException when a problem occurs writing the source files
     */
    public void writeSource(JavaFile source) throws IOException {
        source.writeTo(outputDir);
    }
}

