package am.extension.batchmode.simpleBatchMode;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import am.app.Core;
import am.app.mappingEngine.AbstractMatcher;

import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.ReferenceEvaluationData;
import am.app.mappingEngine.referenceAlignment.ReferenceAlignmentMatcher;
import am.app.mappingEngine.referenceAlignment.ReferenceAlignmentParameters;
import am.app.mappingEngine.referenceAlignment.ReferenceEvaluator;
import am.app.mappingEngine.similarityMatrix.SimilarityMatrix;

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
import am.parsing.AlignmentOutput;
import am.parsing.OutputController;

/**
 * This class takes runs a batch mode from the given SimpleBatchMode XML file.
 * 
 * The structure of the SimpleBatchMode XML file is described in data/SimpleBatchMode.xsd.
 * 
 * @author Cosmin Stroe - Nov 7, 2011
 *
 */
public class SimpleBatchModeRunner {

	private final File input;
	private final File output; // TODO: Use the output directory if the user specifies it.
	private final SimpleBatchModeType bm;
	
	/**
	 * Batch mode runner with an output directory specified.
	 * @param input The XML file that describes the batch mode.
	 * @param output The directory where to output the final alignment.
	 */
	public SimpleBatchModeRunner(File input, File output) {
		this.input = input;
		this.output = output;
		this.bm = null;
	}

	/**
	 * Run a batch mode.
	 * @param input  The XML file that describes the batch mode.
	 */
	public SimpleBatchModeRunner(File input) {
		this.input = input;
		this.output = null;
		this.bm = null;
	}
	
	/**
	 * Pass the batch mode structure directly, without having the read it from a file.
	 * @param bm
	 */
	public SimpleBatchModeRunner(SimpleBatchModeType bm) {
		this.input = null;
		this.output = null;
		this.bm = bm;
	}
	
	public void runBatchMode() throws Exception {
	
		
		SimpleBatchModeType sbm = bm;
		
		Logger log = Logger.getLogger(this.getClass());
		log.setLevel(Level.INFO);
		
		if( sbm == null ) {
			JAXBContext context = JAXBContext.newInstance(this.getClass().getPackage().getName()) ;
	
			Unmarshaller unmarshaller = context.createUnmarshaller() ;
	
			JAXBElement<SimpleBatchModeType> batchmode = (JAXBElement<SimpleBatchModeType>) unmarshaller.unmarshal(new FileInputStream(input)) ; 
			
			sbm = batchmode.getValue();
		}
		
		// Instantiate the Matching Algorithm.
		AbstractMatcher matcher = instantiateMatcher(sbm);
		
		
		for( OntologyType ontType : sbm.ontologies.ontology) {
			
			File sourceOnt = new File(ontType.sourceOntology);
			File targetOnt = new File(ontType.targetOntology);
			
			if( !checkOntologyFiles( sourceOnt, targetOnt, log ) ) continue;
			
			// load the Ontologies.
			final Ontology sourceOntology = OntoTreeBuilder.loadOWLOntology(sourceOnt.getAbsolutePath());
			final Ontology targetOntology = OntoTreeBuilder.loadOWLOntology(targetOnt.getAbsolutePath());
						
			//Core.getInstance().setSourceOntology(sourceOntology);
			//Core.getInstance().setTargetOntology(targetOntology);
			

			matcher.setSourceOntology(sourceOntology);
			matcher.setTargetOntology(targetOntology);
		
			// run the algorithm
			log.info("Matching: " + sourceOntology.getTitle() + " with " + targetOntology.getTitle() + ".");
			try {
				matcher.match();
				
			} catch( Exception e ) {
				try {
					File alignmentFile = new File(ontType.outputAlignmentFile + ".error");
					log.info("Saving error " + alignmentFile.getName() + "." );
				
					PrintStream ps = new PrintStream(alignmentFile);
					e.printStackTrace(ps);
					ps.close();
				} catch( Exception e2 ) {
					e2.printStackTrace();
				}
				e.printStackTrace();
				continue;
			}
			
			// output the alignment
			try {
				File alignmentFile = new File(ontType.outputAlignmentFile);
				log.info("Saving alignment " + alignmentFile.getName() + "." );
				AlignmentOutput output = 
						new AlignmentOutput(matcher.getAlignment(), alignmentFile.getCanonicalFile());
				String sourceUri = sourceOntology.getURI();
				String targetUri = targetOntology.getURI();
				output.write(sourceUri, targetUri, sourceUri, targetUri, matcher.getName());
			
				referenceEvaluation("C:/workspaceFinalProject/reference_alignments/cmt-conference.rdf",sourceOntology,targetOntology,matcher);
				}

				
			//	referenceEvaluation("/Users/Aseel/Downloads/reference-alignment/cmt-iasted"
			//			+ ".rdf",sourceOntology,targetOntology,matcher);
			//	}

			catch (IOException e) {
				e.printStackTrace();
			}
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


	/**
	 * Check to make sure the ontology files exist and are readable.
	 */
	private boolean checkOntologyFiles(File sourceOnt, File targetOnt, Logger log) {
		if( !sourceOnt.exists() ) {
			log.info("Source ontology file does not exist (" + sourceOnt.getAbsolutePath() + ") ... Skipping.");
			return false;
		}
		
		if( !sourceOnt.canRead() ) {
			log.info("Source ontology is not a file (" + sourceOnt.getAbsolutePath() + ") ... Skipping.");
			return false;
		}
		
		if( !sourceOnt.canRead() ) {
			log.info("Source ontology is not readable (" + sourceOnt.getAbsolutePath() + ") ... Skipping.");
			return false;
		}
		
		if( !targetOnt.exists() ) {
			log.info("Target ontology file does not exist (" + targetOnt.getAbsolutePath() + ") ... Skipping.");
			return false;
		}
		
		if( !targetOnt.canRead() ) {
			log.info("Target ontology is not a file (" + targetOnt.getAbsolutePath() + ") ... Skipping.");
			return false;
		}
		
		if( !targetOnt.canRead() ) {
			log.info("Target ontology is not readable (" + targetOnt.getAbsolutePath() + ") ... Skipping.");
			return false;
		}
		
		return true;
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
