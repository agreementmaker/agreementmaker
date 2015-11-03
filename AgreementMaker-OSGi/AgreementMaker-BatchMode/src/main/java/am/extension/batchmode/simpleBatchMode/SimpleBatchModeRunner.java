package am.extension.batchmode.simpleBatchMode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import am.app.mappingEngine.DefaultMatcherParameters;
import am.app.mappingEngine.DefaultSelectionParameters;
import am.app.mappingEngine.MatchingTask;
import am.app.mappingEngine.SelectionAlgorithm;
import am.extension.batchmode.api.BatchModeRunner;
import am.extension.batchmode.api.BatchModeSpec;
import am.extension.batchmode.api.BatchModeTask;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import am.app.mappingEngine.AbstractMatcher;

import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.ReferenceEvaluationData;
import am.app.mappingEngine.referenceAlignment.ReferenceAlignmentMatcher;
import am.app.mappingEngine.referenceAlignment.ReferenceAlignmentParameters;
import am.app.mappingEngine.referenceAlignment.ReferenceEvaluator;


import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.ReferenceEvaluationData;
import am.app.mappingEngine.referenceAlignment.ReferenceAlignmentMatcher;
import am.app.mappingEngine.referenceAlignment.ReferenceAlignmentParameters;
import am.app.mappingEngine.referenceAlignment.ReferenceEvaluator;

import am.app.ontology.Ontology;
import am.app.ontology.ontologyParser.OntoTreeBuilder;
import am.matcher.oaei.oaei2011.OAEI2011Matcher;
import am.matcher.oaei.oaei2011.OAEI2011MatcherParameters;
import am.output.console.ConsoleProgressDisplay;

/**
 * This class takes runs a batch mode from the given SimpleBatchMode XML file.
 * 
 * The structure of the SimpleBatchMode XML file is described in data/SimpleBatchMode.xsd.
 * 
 * @author Cosmin Stroe - Nov 7, 2011
 *
 */
public class SimpleBatchModeRunner implements BatchModeRunner {

    @Override
    public void run(BatchModeSpec spec) throws Exception {
        for(BatchModeTask task : spec.getTasks()) {
            // load the Ontologies.
            checkOntologyFiles(task.getSourceOntology(), task.getTargetOntology());

            final Ontology sourceOntology = OntoTreeBuilder.loadOWLOntology(task.getSourceOntology());
            final Ontology targetOntology = OntoTreeBuilder.loadOWLOntology(task.getTargetOntology());

            AbstractMatcher matcher = task.getMatcher().getMatcher();
            DefaultMatcherParameters parameters = new DefaultMatcherParameters();
            parameters.setOntologies(sourceOntology, targetOntology);
            matcher.setParameters(parameters);

            SelectionAlgorithm selector = task.getSelector().getSelector();
            DefaultSelectionParameters selectionParameters = new DefaultSelectionParameters();
            selectionParameters.inputResult = matcher.getResult();
            selector.setParameters(selectionParameters);

            MatchingTask newMatchingTask = new MatchingTask(matcher, parameters, selector, selectionParameters);
            selectionParameters.matchingTask = newMatchingTask; // TODO: Fix the circular dependency between matching task and selection parameters!

            System.out.println("Matching: " + sourceOntology.getTitle() + " with " + targetOntology.getTitle() + ".");

            matcher.match();
            selector.select();

            task.getOutput().save(selector.getResult());
        }
    }

	private void referenceEvaluation(String pathToReferenceAlignment, Ontology sourceOntology,  Ontology targetOntology,AbstractMatcher matcher)
			throws Exception {
		// Run the reference alignment matcher to get the list of mappings in
		// the reference alignment file
		ReferenceAlignmentMatcher refMatcher = new ReferenceAlignmentMatcher();//(ReferenceAlignmentMatcher) MatcherFactory
//				.getMatcherInstance(MatchersRegistry.ImportAlignment);

		// these parameters are equivalent to the ones in the graphical
		// interface
		ReferenceAlignmentParameters parameters = new ReferenceAlignmentParameters();
		parameters.fileName = pathToReferenceAlignment;
		parameters.format = ReferenceAlignmentMatcher.OAEI;
		parameters.onlyEquivalence = false;
		parameters.skipClasses = false;
		parameters.skipProperties = false;
		refMatcher.setSourceOntology(sourceOntology);
		refMatcher.setTargetOntology(targetOntology);

		// When working with sub-superclass relations the cardinality is always
		// ANY to ANY
		if (!parameters.onlyEquivalence) {
			parameters.maxSourceAlign = AbstractMatcher.ANY_INT;
			parameters.maxTargetAlign = AbstractMatcher.ANY_INT;
		}

		refMatcher.setParam(parameters);

		// load the reference alignment
		refMatcher.match();
		
		Alignment<Mapping> referenceSet;
		if (refMatcher.areClassesAligned() && refMatcher.arePropertiesAligned()) {
			referenceSet = refMatcher.getAlignment(); // class + properties
		} else if (refMatcher.areClassesAligned()) {
			referenceSet = refMatcher.getClassAlignmentSet();
		} else if (refMatcher.arePropertiesAligned()) {
			referenceSet = refMatcher.getPropertyAlignmentSet();
		} else {
			// empty set? -- this should not happen
			referenceSet = new Alignment<Mapping>(Ontology.ID_NONE,
					Ontology.ID_NONE);
		}

		// the alignment which we will evaluate
		Alignment<Mapping> myAlignment;

		if (refMatcher.areClassesAligned() && refMatcher.arePropertiesAligned()) {
			myAlignment = matcher.getAlignment();
		} else if (refMatcher.areClassesAligned()) {
			myAlignment = matcher.getClassAlignmentSet();
		} else if (refMatcher.arePropertiesAligned()) {
			myAlignment = matcher.getPropertyAlignmentSet();
		} else {
			myAlignment = new Alignment<Mapping>(Ontology.ID_NONE,
					Ontology.ID_NONE); // empty
		}

		// use the ReferenceEvaluator to actually compute the metrics
		ReferenceEvaluationData rd = ReferenceEvaluator.compare(myAlignment,
				referenceSet);

		// optional
		//setRefEvaluation(rd);

		// output the report
		StringBuilder report = new StringBuilder();
		report.append("Reference Evaluation Complete\n\n").append(matcher.getName())
				.append("\n\n").append(rd.getReport()).append("\n");

		//log.info(report);
		
		// use system out if you don't see the log4j output
		System.out.println(report);
				

	}

			
				referenceEvaluation("C:/workspaceFinalProject/reference_alignments/cmt-conference.rdf",sourceOntology,targetOntology,matcher);

				
			//	referenceEvaluation("/Users/Aseel/Downloads/reference-alignment/cmt-iasted"
			//			+ ".rdf",sourceOntology,targetOntology,matcher);
			//	}



	

	private void referenceEvaluation(String pathToReferenceAlignment, Ontology sourceOntology,  Ontology targetOntology,AbstractMatcher matcher)
			throws Exception {
		// Run the reference alignment matcher to get the list of mappings in
		// the reference alignment file
		ReferenceAlignmentMatcher refMatcher = new ReferenceAlignmentMatcher();//(ReferenceAlignmentMatcher) MatcherFactory
//				.getMatcherInstance(MatchersRegistry.ImportAlignment);

		// these parameters are equivalent to the ones in the graphical
		// interface
		ReferenceAlignmentParameters parameters = new ReferenceAlignmentParameters();
		parameters.fileName = pathToReferenceAlignment;
		parameters.format = ReferenceAlignmentMatcher.OAEI;
		parameters.onlyEquivalence = false;
		parameters.skipClasses = false;
		parameters.skipProperties = false;
		refMatcher.setSourceOntology(sourceOntology);
		refMatcher.setTargetOntology(targetOntology);

		// When working with sub-superclass relations the cardinality is always
		// ANY to ANY
		if (!parameters.onlyEquivalence) {
			parameters.maxSourceAlign = AbstractMatcher.ANY_INT;
			parameters.maxTargetAlign = AbstractMatcher.ANY_INT;
		}

		refMatcher.setParam(parameters);

		// load the reference alignment
		refMatcher.match();
		
		Alignment<Mapping> referenceSet;
		if (refMatcher.areClassesAligned() && refMatcher.arePropertiesAligned()) {
			referenceSet = refMatcher.getAlignment(); // class + properties
		} else if (refMatcher.areClassesAligned()) {
			referenceSet = refMatcher.getClassAlignmentSet();
		} else if (refMatcher.arePropertiesAligned()) {
			referenceSet = refMatcher.getPropertyAlignmentSet();
		} else {
			// empty set? -- this should not happen
			referenceSet = new Alignment<Mapping>(Ontology.ID_NONE,
					Ontology.ID_NONE);
		}

		// the alignment which we will evaluate
		Alignment<Mapping> myAlignment;

		if (refMatcher.areClassesAligned() && refMatcher.arePropertiesAligned()) {
			myAlignment = matcher.getAlignment();
		} else if (refMatcher.areClassesAligned()) {
			myAlignment = matcher.getClassAlignmentSet();
		} else if (refMatcher.arePropertiesAligned()) {
			myAlignment = matcher.getPropertyAlignmentSet();
		} else {
			myAlignment = new Alignment<Mapping>(Ontology.ID_NONE,
					Ontology.ID_NONE); // empty
		}

		// use the ReferenceEvaluator to actually compute the metrics
		ReferenceEvaluationData rd = ReferenceEvaluator.compare(myAlignment,
				referenceSet);

		// optional
		//setRefEvaluation(rd);

		// output the report
		StringBuilder report = new StringBuilder();
		report.append("Reference Evaluation Complete\n\n").append(matcher.getName())
				.append("\n\n").append(rd.getReport()).append("\n");

		//log.info(report);
		
		// use system out if you don't see the log4j output
		System.out.println(report);

	}


	/**
	 * Check to make sure the ontology files exist and are readable.
	 */
	private void checkOntologyFiles(String sourceOntology, String targetOntology) throws IOException {
        File sourceOnt = new File(sourceOntology);
        File targetOnt = new File(targetOntology);

        if( !sourceOnt.exists() ) {
			throw new FileNotFoundException("Source ontology file does not exist (" + sourceOnt.getAbsolutePath() + ") ... Skipping.");
		}
		
		if( !sourceOnt.isFile() ) {
            throw new IOException("Source ontology is not a file (" + sourceOnt.getAbsolutePath() + ") ... Skipping.");
		}
		
		if( !sourceOnt.canRead() ) {
            throw new IOException("Source ontology is not readable (" + sourceOnt.getAbsolutePath() + ") ... Skipping.");
		}
		
		if( !targetOnt.exists() ) {
            throw new FileNotFoundException("Target ontology file does not exist (" + targetOnt.getAbsolutePath() + ") ... Skipping.");
		}
		
		if( !targetOnt.isFile() ) {
            throw new IOException("Target ontology is not a file (" + targetOnt.getAbsolutePath() + ") ... Skipping.");
		}
		
		if( !targetOnt.canRead() ) {
            throw new IOException("Target ontology is not readable (" + targetOnt.getAbsolutePath() + ") ... Skipping.");
		}
	}

	/**
	 * Instantiate a matching algorithm.
	 * 
	 * TODO: Make the algorithm instantiate the matcher that the user specifies in the XML file.
	 * 
	 * @param batchMode
	 * @return
	 */
	public AbstractMatcher instantiateMatcher(SimpleBatchModeType batchMode) {
			
			Logger log = Logger.getLogger(this.getClass());
			log.setLevel(Level.INFO);

			// create the matching algorithm
			AbstractMatcher matcher = new OAEI2011Matcher();
			
			
			// create the matching algorithm parameters
			OAEI2011MatcherParameters params = new OAEI2011MatcherParameters();
			
		
			
			params.maxSourceAlign = 1;
			params.maxTargetAlign = 1;
			params.threshold = 0.60;
			params.parallelExecution = true;
			
			matcher.setParam(params);
			matcher.addProgressDisplay( new ConsoleProgressDisplay() );  // output status information to the console.
			matcher.setUseProgressDelay(true);

			return matcher;
	}
	
}
