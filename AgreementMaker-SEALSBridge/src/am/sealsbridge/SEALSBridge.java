package am.sealsbridge;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;

import org.apache.log4j.BasicConfigurator;
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
import eu.sealsproject.platform.res.domain.omt.IOntologyMatchingToolBridge;
import eu.sealsproject.platform.res.tool.api.ToolBridgeException;
import eu.sealsproject.platform.res.tool.api.ToolException;
import eu.sealsproject.platform.res.tool.api.ToolType;
import eu.sealsproject.platform.res.tool.impl.AbstractPlugin;

public class SEALSBridge extends AbstractPlugin implements IOntologyMatchingToolBridge {

	/**
	* Aligns to ontologies specified via their URL and returns the 
	* URL of the resulting alignment, which should be stored locally.
	* 
	*/
	public URL align(URL source, URL target) throws ToolBridgeException, ToolException {
		
		try {
			
			//BasicConfigurator.configure();
			
			Logger log = Logger.getLogger(this.getClass());
			log.setLevel(Level.INFO);
			
			// load the ontologies
			Ontology sourceOntology = OntoTreeBuilder.loadOWLOntology(source.toString());
			Ontology targetOntology = OntoTreeBuilder.loadOWLOntology(target.toString());
			
			Core.getInstance().setSourceOntology(sourceOntology);
			Core.getInstance().setTargetOntology(targetOntology);
			
			// create the matching algorithm
			AbstractMatcher matcher = MatcherFactory.getMatcherInstance(MatchersRegistry.OAEI2011, 0);
			
			// create the matching algorithm parameters
			OAEI2011MatcherParameters params = new OAEI2011MatcherParameters();
			
			params.maxSourceAlign = 1;
			params.maxTargetAlign = 1;
			params.threshold = 0.60;
			params.parallelExecution = true;
			
			try {
				File thresholdFile = new File("threshold.txt");
				if( thresholdFile.exists() && thresholdFile.canRead()) {
					BufferedReader thresholdReader = new BufferedReader( new FileReader(thresholdFile) );
					String firstLine = thresholdReader.readLine();
					double threshold = Double.parseDouble(firstLine);
					if( threshold > 0 && threshold <= 1.0 ) params.threshold = threshold;
					log.info("Using threshold " + params.threshold +".");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			matcher.setParam(params);
			matcher.setSourceOntology(sourceOntology);
			matcher.setTargetOntology(targetOntology);
			matcher.setProgressDisplay( new ConsoleProgressDisplay() );  // output status information to the console.
			matcher.setUseProgressDelay(true);
			
			// run the algorithm
			matcher.match();
					
			
			// output the alignment
			try {
				File alignmentFile = File.createTempFile("alignment", ".rdf");
				OutputController.printDocumentOAEI(alignmentFile, matcher.getAlignment(), matcher.getName());
				return alignmentFile.toURI().toURL();
			}
			catch (IOException e) {
				throw new ToolBridgeException("cannot create file for resulting alignment", e);
			}
			
		}
		catch (NumberFormatException numberFormatE) {
			throw new ToolBridgeException("cannot correctly read from configuration file", numberFormatE);
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new ToolException(e.getMessage(), e);
		}
		
	}

	/**
	* This is used by the Anatomy Track in subtrack 4.
	*/
	public URL align(URL source, URL target, URL inputAlignment) throws ToolBridgeException, ToolException {
		// TODO: This should take into account the partial reference alignment.
		return align(source, target);
	}

	/**
	* In our case the DemoMatcher can be executed on the fly. In case
	* prerequesites are required it can be checked here. 
	*/
	public boolean canExecute() {
		return true;
	}

	/**
	* The DemoMatcher is an ontology matching tool. SEALS supports the
	* evaluation of different tool types like e.g., reasoner and storage systems.
	*/
	public ToolType getType() {
		return ToolType.OntologyMatchingTool;
	}
	

}
