package am.matcher.bsm;

import java.beans.PropertyChangeEvent;
import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.prefs.Preferences;

import javax.swing.BorderFactory;
import javax.swing.JFileChooser;

import am.app.Core;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.MatchingProgressListener;
import am.app.ontology.Ontology;
import am.app.ontology.ontologyParser.OntoTreeBuilder;
import am.app.ontology.profiling.manual.ManualOntologyProfiler;
import am.app.ontology.profiling.manual.ManualProfilerMatchingParameters;
import am.utility.RunTimer;

import com.hp.hpl.jena.rdf.model.Property;

/**
 * An example of running the Base Similarity Matcher on two ontologies.
 * 
 * The steps of running are:
 * <ol>
 * <li>Load the ontologies.</li>
 * <li>Setup the ontology profiler.</li>
 * <li>Instantiate and run BSM.</li>
 * </ol>
 * 
 * @author Cosmin Stroe <cstroe@gmail.com>
 */
public class ExampleBSMwithOntologyProfiler {

	private static final String SOURCE_KEY = "SOURCE_ONTOLOGY";
	private static final String TARGET_KEY = "TARGET_ONTOLOGY";
	
	public static void main(String[] args) {
		
		// remember what the user chose
		Preferences prefs = Preferences.userNodeForPackage(ExampleBSMwithOntologyProfiler.class);
		
		// select the source ontology
		JFileChooser fc = new JFileChooser(new File(prefs.get(SOURCE_KEY, "")));
		fc.setBorder(BorderFactory.createTitledBorder("Select Source Ontology"));
		if( fc.showOpenDialog(null) != JFileChooser.APPROVE_OPTION )
			return; // user canceled

		File sourceOntologyFile = fc.getSelectedFile();
		prefs.put(SOURCE_KEY, sourceOntologyFile.getAbsolutePath());


		// select the target ontology
		fc.setCurrentDirectory(new File(prefs.get(TARGET_KEY, "")));
		if( fc.showOpenDialog(null) != JFileChooser.APPROVE_OPTION )
			return; // user canceled

		File targetOntologyFile = fc.getSelectedFile();
		prefs.put(TARGET_KEY, targetOntologyFile.getAbsolutePath());

		
		Ontology sourceOntology = OntoTreeBuilder.loadOWLOntology(sourceOntologyFile.getAbsolutePath());
		Ontology targetOntology = OntoTreeBuilder.loadOWLOntology(targetOntologyFile.getAbsolutePath());
		
		
		// setup the ontology profiler (this is required by the BSM)
		ManualOntologyProfiler profiler = new ManualOntologyProfiler(sourceOntology, targetOntology);
		
		ManualProfilerMatchingParameters param = new ManualProfilerMatchingParameters();
		
		// we're going to set all the parameters from code, no user interaction

		// do not use the localnames for matching
		param.matchSourceClassLocalname = false;
		param.matchTargetClassLocalname = false;
		param.matchSourcePropertyLocalname = false;
		param.matchTargetPropertyLocalname = false;
		
		// select the rdfs label as the only thing that will match against
		param.sourceClassAnnotations = createAnnotationList("label", profiler.getSourceClassAnnotations());
		param.targetClassAnnotations = createAnnotationList("label", profiler.getTargetClassAnnotations());
		param.sourcePropertyAnnotations = createAnnotationList("label", profiler.getSourcePropertyAnnotations());
		param.targetPropertyAnnotations = createAnnotationList("label", profiler.getTargetPropertyAnnotations());
		
		profiler.setMatchTimeParams(param);
		Core.getInstance().setOntologyProfiler(profiler);
		
		// ok, now create the matching algorithm
		
		BaseSimilarityParameters matcherParam = new BaseSimilarityParameters();
		
		BaseSimilarityMatcher bsm = new BaseSimilarityMatcher(matcherParam);
		bsm.setSourceOntology(sourceOntology);
		bsm.setTargetOntology(targetOntology);
		
		// add a progress display listener so that we can display the progress on the console
		bsm.addProgressDisplay(new MatchingProgressListener() {
			@Override public void setProgressLabel(String label) {}
			@Override public void setIndeterminate(boolean indeterminate) {}
			@Override public void scrollToEndOfReport() {}
			@Override public void propertyChange(PropertyChangeEvent evt) {
		        if ( evt.getPropertyName().equals("progress") ) {
		            final int progress = (Integer) evt.getNewValue();
		            System.out.println("Current progress: " + progress + "%");
		        }
			}
			@Override public void matchingStarted(AbstractMatcher m) {}
			@Override public void matchingComplete() {}
			@Override public void ignoreComplete(boolean ignore) {}
			@Override public void clearReport() {}
			@Override public void appendToReport(String report) {}
		});
		
		// keep track of the length of the runtime.
		RunTimer timer = new RunTimer().start();

		// we're ready to match the ontologies
		try {
			bsm.match();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		
		// matching done.
		System.out.println("Finished running.  Total run time: " + timer.stop().getFormattedRunTime());
		// NOTE: Using the deprecated calls for now, but will change this later.
		System.out.println("Found " + bsm.getClassAlignmentSet().size() + " class mappings.");
		System.out.println("Found " + bsm.getPropertyAlignmentSet().size() + " class mappings.");

	}
	
	
	// filter the annotationProperties list by matching name
	private static List<Property> createAnnotationList(String name, List<Property> annotationProperties) {
		List<Property> list = new LinkedList<Property>();
		for( Property p : annotationProperties ) {
			if( p.getLocalName().equals(name) ) {
				list.add(p);
			}
		}
		return list;
	}
}
