package am.extension.userfeedback.common;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import am.app.Core;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.MatchingTask;
import am.app.mappingEngine.referenceAlignment.ReferenceAlignmentMatcher;
import am.app.ontology.Ontology;
import am.extension.userfeedback.UserFeedback.Validation;
import am.extension.userfeedback.experiments.UFLExperiment;
import am.extension.userfeedback.experiments.UFLExperimentParameters.Parameter;
import am.extension.userfeedback.experiments.UFLExperimentSetup;
import am.extension.userfeedback.logic.IndependentSequentialLogic;
import am.extension.userfeedback.logic.NonPersistentUFLControlLogic;

/**
 * This is a manual experiment setup.
 * 
 * The source and target ontologies are loaded into AgreementMaker,
 * and the reference alignment is imported.
 * 
 * @author Cosmin Stroe - Feb 1, 2011.
 *
 */
public class ManualExperimentSetup extends UFLExperiment {
	
	private BufferedWriter logFile;
	
	public ManualExperimentSetup(UFLExperimentSetup setup) {
		super(setup);
		// setup the log file
		try {
			String log = setup.parameters.getParameter(Parameter.LOGFILE);
			String root = Core.getInstance().getRoot();
			FileWriter fr = new FileWriter(root + log, false);
			logFile = new BufferedWriter(fr);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public Ontology getSourceOntology() {
		return Core.getInstance().getSourceOntology();
	}

	@Override
	public Ontology getTargetOntology() {
		return Core.getInstance().getTargetOntology();
	}

	@Override
	public Alignment<Mapping> getReferenceAlignment() {
		List<MatchingTask> tasks = Core.getInstance().getMatchingTasks();
		for( MatchingTask m : tasks ) {
			if( m.matchingAlgorithm instanceof ReferenceAlignmentMatcher ) {
				// return the alignment of the first reference alignment matcher
				return m.selectionResult.getAlignment();
			}
		}
		return null;
	}

	@Override
	public Alignment<Mapping> getFinalAlignment() {
		return initialMatcher.getAlignment();
	}

	@Override
	public void info(String line) {
		if( logFile != null )
			try {
				logFile.write(line + "\n");
				logFile.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

	@Override
	public NonPersistentUFLControlLogic getControlLogic() {
		return new IndependentSequentialLogic();
	}
	
	@Override
	public String getDescription() {
		return  "Everything for this experiment is manually chosen by the user, through the user interface.\n" +
				"The source and target ontologies are loaded into AgreementMaker,\n" + 
				"and the reference alignment is imported.";
	}
}
