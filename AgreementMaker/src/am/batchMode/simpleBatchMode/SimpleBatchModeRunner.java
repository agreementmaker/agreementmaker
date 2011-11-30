package am.batchMode.simpleBatchMode;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import am.app.Core;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.MatcherFactory;
import am.app.mappingEngine.MatchersRegistry;
import am.app.mappingEngine.oaei.oaei2011.OAEI2011MatcherParameters;
import am.app.ontology.Ontology;
import am.app.ontology.ontologyParser.OntoTreeBuilder;
import am.output.OutputController;
import am.userInterface.console.ConsoleProgressDisplay;

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
			Ontology sourceOntology = OntoTreeBuilder.loadOWLOntology(sourceOnt.getAbsolutePath());
			Ontology targetOntology = OntoTreeBuilder.loadOWLOntology(targetOnt.getAbsolutePath());
						
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
				OutputController.printDocumentOAEI(alignmentFile, matcher.getAlignment(), matcher.getName());
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
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
			AbstractMatcher matcher = MatcherFactory.getMatcherInstance(MatchersRegistry.OAEI2011, 0);
			
			// create the matching algorithm parameters
			OAEI2011MatcherParameters params = new OAEI2011MatcherParameters();
			
			params.maxSourceAlign = 1;
			params.maxTargetAlign = 1;
			params.threshold = 0.60;
			params.parallelExecution = true;
			
			matcher.setParam(params);
			matcher.setProgressDisplay( new ConsoleProgressDisplay() );  // output status information to the console.
			matcher.setUseProgressDelay(true);

			return matcher;
	}
	
}
