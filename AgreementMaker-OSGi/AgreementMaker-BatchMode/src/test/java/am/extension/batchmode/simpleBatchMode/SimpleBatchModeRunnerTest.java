package am.extension.batchmode.simpleBatchMode;

import org.junit.Test;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.Is.is;

import static org.junit.Assert.assertThat;

public class SimpleBatchModeRunnerTest {
    @Test
    public void unmarshall_should_read_correct_values() throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(ObjectFactory.class);

        Unmarshaller unmarshaller = context.createUnmarshaller() ;

        final InputStream is = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("example-batch-mode.xml");
        JAXBElement<SimpleBatchModeType> batchmode = (JAXBElement<SimpleBatchModeType>) unmarshaller.unmarshal(is) ;

        SimpleBatchModeType sbm = batchmode.getValue();

        assertThat(sbm.getOntologies().getOntology().size(), is(1));
        assertThat(sbm.getOntologies().getOntology().get(0).getSourceOntology(), is(equalTo("C:/dws_workspace/conference/cmt/cmt.owl")));
        assertThat(sbm.getOntologies().getOntology().get(0).getTargetOntology(), is(equalTo("C:/dws_workspace/conference/Conference/Conference.owl")));

        assertThat(sbm.getOntologies().getOntology().get(0).getOutputAlignmentFile(), is(equalTo("C:/dws_workspace/outputalign/align_cmt_conf.rdf")));
        assertThat(sbm.getOntologies().getOntology().get(0).getMatcherRegistryEntry(), is(equalTo("NOT USED YET")));
    }

}
