package am.app.mappingEngine.ontologyLoading;

import java.io.File;
import java.io.InputStream;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.Ignore;

import am.utility.RunTimer;

import com.hp.hpl.jena.ontology.AnnotationProperty;
import com.hp.hpl.jena.ontology.DatatypeProperty;
import com.hp.hpl.jena.ontology.ObjectProperty;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

public class LoadLargeOntologiesTest {

	private Logger log = Logger.getLogger(LoadLargeOntologiesTest.class);
	
	@Ignore("This test is not ready yet")
	@Test
	public void loadLargeOntologyWithJena() {
		
		RunTimer timer = new RunTimer().start();
		
		OntModel m = ModelFactory.createOntologyModel();

		final File owlFile = new File("/home/cosmin/Desktop/OAEI 2012/LargeBioMed_dataset_oaei2012/oaei2012_NCI_whole_ontology.owl"); 
		
		InputStream in = FileManager.get().open(owlFile.getAbsolutePath());
		
		m.read(in, null);
		
		
		log.info("Loaded '" + owlFile.getName() + "' in " + timer.getFormattedRunTime()); 
		
		log.info("Model contains " + m.size() + " triples.");
		
		timer.resetAndStart();
		
		int numClasses = 0;
		ExtendedIterator<OntClass> classesIter = m.listClasses();
		while( classesIter.hasNext() ) {
			numClasses++;
			classesIter.next();
		}

		log.info("Ontology defines " + numClasses + " classes. (" + timer.getFormattedRunTime() + ")");
		
		
		timer.resetAndStart();
		
		int objectProperties = 0;
		ExtendedIterator<ObjectProperty> objectPropertiesIter = m.listObjectProperties();
		while( objectPropertiesIter.hasNext() ) {
			objectProperties++;
			objectPropertiesIter.next();
		}
		
		log.info("Ontology defines " + objectProperties + " object properties. (" + timer.getFormattedRunTime() + ")");
		
		
		timer.resetAndStart();
		
		int datatypeProperties = 0;
		ExtendedIterator<DatatypeProperty> datatypePropertiesIter = m.listDatatypeProperties();
		while( datatypePropertiesIter.hasNext() ) {
			datatypeProperties++;
			datatypePropertiesIter.next();
		}
		
		log.info("Ontology defines " + datatypeProperties + " datatype properties. (" + timer.getFormattedRunTime() + ")");
		
		
		timer.resetAndStart();
		
		int annotationProperties = 0;
		ExtendedIterator<AnnotationProperty> annotationPropertiesIter = m.listAnnotationProperties();
		while( annotationPropertiesIter.hasNext() ) {
			annotationProperties++;
			annotationPropertiesIter.next();
		}
		
		log.info("Ontology defines " + annotationProperties + " annotation properties. (" + timer.getFormattedRunTime() + ")");
		
		
	}
	
}
