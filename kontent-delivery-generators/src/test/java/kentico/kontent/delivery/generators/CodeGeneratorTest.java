package kentico.kontent.delivery.generators;


import com.squareup.javapoet.JavaFile;
import kentico.kontent.delivery.DeliveryClient;
import kentico.kontent.delivery.DeliveryOptions;
import org.apache.http.HttpHost;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.localserver.LocalServerTestBase;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
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
                new CodeGenerator(projectId,"com.dancinggoat.models", outputDir);
        List<JavaFile> sources = codeGenerator.generateSources(client);
        codeGenerator.writeSources(sources);

        File[] toplevel = outputDir.listFiles();
        Assert.assertEquals(1, toplevel.length);
        File com = toplevel[0];
        Assert.assertEquals("com", com.getName());
        File[] secondlevel = com.listFiles();
        Assert.assertEquals(1, secondlevel.length);
        File dancinggoat = secondlevel[0];
        Assert.assertEquals("dancinggoat", dancinggoat.getName());
        File[] thirdlevel = dancinggoat.listFiles();
        Assert.assertEquals(1, thirdlevel.length);
        File models = thirdlevel[0];
        Assert.assertEquals("models", models.getName());
        List<String> files = Arrays.asList(models.list());
        Assert.assertEquals(2, files.size());
        Assert.assertTrue(files.contains("Article.java"));
        Assert.assertTrue(files.contains("Brewer.java"));
        // TODO extend check for files content
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
}