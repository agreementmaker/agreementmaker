package am.extension.batchmode.internal;

import am.extension.batchmode.internal.providers.MatcherProviderFromClasspath;
import am.extension.batchmode.internal.providers.OntologyFile;
import am.extension.batchmode.internal.providers.SelectorProviderFromClasspath;
import am.extension.batchmode.internal.providers.WriteOAEIToFile;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static am.extension.batchmode.TestUtil.assertEqualStreams;
import static am.extension.batchmode.TestUtil.openResource;

public class BatchModeFileWriterImplTest {
    @Test
    public void test_simple_output() throws IOException {
        BatchModeTaskImpl newTask = new BatchModeTaskImpl();
        newTask.setSourceOntology(new OntologyFile("/path/to/some/file.owl"));
        newTask.setTargetOntology(new OntologyFile("/path/to/some/file.owl"));
        newTask.setMatcher(new MatcherProviderFromClasspath("com.some.package.Matcher"));
        newTask.setSelector(new SelectorProviderFromClasspath("com.some.package.Selector"));
        newTask.setOutput(new WriteOAEIToFile("/path/some/file.rdf"));

        BatchModeSpecImpl newSpec = new BatchModeSpecImpl();
        newSpec.addTask(newTask);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BatchModeFileWriterImpl writer = new BatchModeFileWriterImpl();
        writer.write(baos, newSpec);

        assertEqualStreams(openResource("json/simple.json"), new ByteArrayInputStream(baos.toByteArray()));
    }
}