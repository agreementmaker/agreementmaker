package am.extension.batchmode.internal;

import am.extension.batchmode.TestUtil;
import am.extension.batchmode.api.BatchModeFileReader;
import am.extension.batchmode.api.BatchModeSpec;
import am.extension.batchmode.api.BatchModeTask;
import am.extension.batchmode.internal.providers.MatcherProviderFromClasspath;
import am.extension.batchmode.internal.providers.OntologyFile;
import am.extension.batchmode.internal.providers.SelectorProviderFromClasspath;
import am.extension.batchmode.internal.providers.WriteOAEIToFile;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.*;

public class BatchModeFileReaderImplTest {
    @Test
    public void simple_reader() throws IOException {
        BatchModeFileReader reader = new BatchModeFileReaderImpl();
        BatchModeSpec spec = reader.read(TestUtil.openResource("json/simple.json"));

        assertNotNull(spec);
        assertThat(spec.getTasks().size(), is(1));

        BatchModeTask task = spec.getTasks().get(0);
        assertNotNull(task.getSourceOntology());
        assertTrue(task.getSourceOntology() instanceof OntologyFile);
        assertThat(((OntologyFile) task.getSourceOntology()).getFilePath(), is(equalTo("/path/to/some/file1.owl")));

        assertNotNull(task.getTargetOntology());
        assertTrue(task.getTargetOntology() instanceof OntologyFile);
        assertThat(((OntologyFile) task.getTargetOntology()).getFilePath(), is(equalTo("/path/to/some/file2.owl")));

        assertNotNull(task.getMatcher());
        assertTrue(task.getMatcher() instanceof MatcherProviderFromClasspath);
        assertThat(((MatcherProviderFromClasspath) task.getMatcher()).getCanonicalClassName(), is(equalTo("com.some.package.Matcher")));

        assertNotNull(task.getSelector());
        assertTrue(task.getSelector() instanceof SelectorProviderFromClasspath);
        assertThat(((SelectorProviderFromClasspath) task.getSelector()).getCanonicalClassName(), is(equalTo("com.some.package.Selector")));

        assertNotNull(task.getOutput());
        assertTrue(task.getOutput() instanceof WriteOAEIToFile);
        assertThat(((WriteOAEIToFile) task.getOutput()).getFilePath(), is(equalTo("/path/some/file.rdf")));
    }
}