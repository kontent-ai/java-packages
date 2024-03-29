package kontent.ai.delivery.generators;


import com.squareup.javapoet.JavaFile;
import kontent.ai.delivery.DeliveryClient;
import kontent.ai.delivery.DeliveryOptions;
import kontent.ai.delivery.generators.CodeGenerator;
import org.apache.http.HttpHost;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.localserver.LocalServerTestBase;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class CodeGeneratorTest extends LocalServerTestBase {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void testGenerator() throws Exception {
        String projectId = "02a70003-e864-464e-b62c-e0ede97deb8c";

        this.serverBootstrap.registerHandler(
                String.format("/%s/%s", projectId, "types"),
                (request, response, context) -> response.setEntity(
                        new InputStreamEntity(
                                this.getClass().getResourceAsStream("SampleContentTypeList.json")
                        )
                ));
        HttpHost httpHost = this.start();
        DeliveryOptions deliveryOptions = new DeliveryOptions();
        deliveryOptions.setProductionEndpoint(httpHost.toURI());
        deliveryOptions.setProjectId(projectId);
        DeliveryClient client = new DeliveryClient(deliveryOptions);

        File outputDir = temporaryFolder.newFolder();
        Assert.assertTrue(outputDir.exists() && outputDir.isDirectory());
        CodeGenerator codeGenerator =
                new CodeGenerator(projectId,"kontent.ai.delivery.generators", outputDir);
        List<JavaFile> sources = codeGenerator.generateSources(client);
        codeGenerator.writeSources(sources);

        File[] toplevel = outputDir.listFiles();
        Assert.assertEquals(1, toplevel.length);
        File kontent = toplevel[0];
        Assert.assertEquals("kontent", kontent.getName());
        File[] secondlevel = kontent.listFiles();
        Assert.assertEquals(1, secondlevel.length);
        File ai = secondlevel[0];
        Assert.assertEquals("ai", ai.getName());
        File[] thirdlevel = ai.listFiles();
        Assert.assertEquals(1, thirdlevel.length);
        File delivery = thirdlevel[0];
        Assert.assertEquals("delivery", delivery.getName());
        File[] forthlevel = delivery.listFiles();
        Assert.assertEquals(1, forthlevel.length);
        File generators = forthlevel[0];
        Assert.assertEquals("generators", generators.getName());
        List<String> files = Arrays.asList(generators.list());
        Assert.assertEquals(2, files.size());
        Assert.assertTrue(files.contains("Article.java"));
        Assert.assertTrue(files.contains("Brewer.java"));

        Assert.assertEquals(
                readResource("Article.java"),
                readFile(new File(outputDir, "kontent/ai/delivery/generators/Article.java"))
        );
        Assert.assertEquals(
                readResource("Brewer.java"),
                readFile(new File(outputDir, "kontent/ai/delivery/generators/Brewer.java"))
        );
    }

    @Test
    public void testExceptionWhenFolderCannotBeCreated() throws IOException {
        File fileNotFolder = temporaryFolder.newFile("file");
        File output = new File(fileNotFolder, "output");
        try {
            CodeGenerator codeGenerator = new CodeGenerator(null, null, output);
            Assert.fail("Expected exception!");
        } catch (UnsupportedOperationException ex) {
            Assert.assertEquals(
                    String.format("Unable to create directory %s", output.getAbsolutePath()),
                    ex.getMessage());
        }
    }

    @Test
    public void testExceptionWhenOutputDirectoryIsFile() throws IOException {
        File fileNotFolder = temporaryFolder.newFile("file");
        try {
            CodeGenerator codeGenerator = new CodeGenerator(null, null, fileNotFolder);
            Assert.fail("Expected exception!");
        } catch (UnsupportedOperationException ex) {
            Assert.assertEquals(
                    String.format("%s exists and is not a directory", fileNotFolder.getAbsolutePath()),
                    ex.getMessage());
        }

    }

    String readFile(File file) throws IOException {
        byte[] encoded = Files.readAllBytes(file.toPath());
        return new String(encoded, StandardCharsets.UTF_8);
    }

    String readResource(String resourceName) throws IOException, URISyntaxException {
        byte[] encoded = Files.readAllBytes(Paths.get(this.getClass().getResource(resourceName).toURI()));
        return new String(encoded, StandardCharsets.UTF_8);
    }
}