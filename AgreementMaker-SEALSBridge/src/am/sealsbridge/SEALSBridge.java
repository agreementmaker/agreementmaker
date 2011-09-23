package am.sealsbridge;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import am.app.Core;
import am.app.lexicon.LexiconBuilderParameters;
import am.app.lexicon.ontology.OntologyLexiconBuilder;
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
			
			// load the ontologies
			Ontology sourceOntology = OntoTreeBuilder.loadOWLOntology(source.toString());
			Ontology targetOntology = OntoTreeBuilder.loadOWLOntology(target.toString());
			
			Core.getInstance().setSourceOntology(sourceOntology);
			Core.getInstance().setTargetOntology(targetOntology);
			
			// create the lexicons.
			LexiconBuilderParameters lexiconParameters = OntologyLexiconBuilder.getDefaultParameters(sourceOntology, targetOntology);
			Core.getLexiconStore().setParameters(lexiconParameters);
			Core.getLexiconStore().buildAll();
			
			// create the matching algorithm
			AbstractMatcher matcher = MatcherFactory.getMatcherInstance(MatchersRegistry.OAEI2011, 0);
			
			// create the matching algorithm parameters
			OAEI2011MatcherParameters params = new OAEI2011MatcherParameters();
			
			params.maxSourceAlign = 1;
			params.maxTargetAlign = 1;
			params.threshold = 0.73;

			matcher.setParam(params);
			
			matcher.setProgressDisplay( new ConsoleProgressDisplay() );  // output status information to the console.
			
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
