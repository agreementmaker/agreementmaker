package am.extension.batchmode.internal;

import am.extension.batchmode.internal.providers.MatcherProviderFromClasspath;
import am.extension.batchmode.internal.providers.SelectorProviderFromClasspath;
import am.extension.batchmode.internal.providers.WriteOAEIToFile;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static am.extension.batchmode.TestUtil.assertEqualStreams;
import static am.extension.batchmode.TestUtil.openResource;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class BatchModeFileWriterImplTest {
    @Test
    public void test_simple_output() throws IOException {
        BatchModeTaskImpl newTask = new BatchModeTaskImpl();
        newTask.setSourceOntology("/path/to/some/file1.owl");
        newTask.setTargetOntology("/path/to/some/file2.owl");
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

    @Test
    public void test_two_task_output() throws IOException {
        BatchModeSpecImpl newSpec = new BatchModeSpecImpl();
        {
            BatchModeTaskImpl newTask = new BatchModeTaskImpl();
            newTask.setSourceOntology("/path/to/some/file1.owl");
            newTask.setTargetOntology("/path/to/some/file2.owl");
            newTask.setMatcher(new MatcherProviderFromClasspath("com.some.package.Matcher"));
            newTask.setSelector(new SelectorProviderFromClasspath("com.some.package.Selector"));
            newTask.setOutput(new WriteOAEIToFile("/path/some/alignment1.rdf"));
            newSpec.addTask(newTask);
        }
        {
            BatchModeTaskImpl newTask = new BatchModeTaskImpl();
            newTask.setSourceOntology("/path/to/some/file1.owl");
            newTask.setTargetOntology("/path/to/some/file3.owl");
            newTask.setMatcher(new MatcherProviderFromClasspath("com.some.package.Matcher2"));
            newTask.setSelector(new SelectorProviderFromClasspath("com.some.package.Selector3"));
            newTask.setOutput(new WriteOAEIToFile("/path/some/alignment2.rdf"));
            newSpec.addTask(newTask);
        }
        assertThat(newSpec.getTasks().size(), is(2));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BatchModeFileWriterImpl writer = new BatchModeFileWriterImpl();
        writer.write(baos, newSpec);

        assertEqualStreams(openResource("json/twotask.json"), new ByteArrayInputStream(baos.toByteArray()));
    }
}