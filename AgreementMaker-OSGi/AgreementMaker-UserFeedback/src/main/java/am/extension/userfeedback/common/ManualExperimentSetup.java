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
import am.extension.userfeedback.UFLExperiment;
import am.extension.userfeedback.UserFeedback.Validation;
import am.extension.userfeedback.experiments.IndependentSequentialLogic;
import am.extension.userfeedback.experiments.UFLControlLogic;

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
	
	public ManualExperimentSetup() {
		// setup the log file
		try {
			FileWriter fr = new FileWriter("/home/cosmin/Desktop/ufllog.txt",true);
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
	public boolean experimentHasCompleted() {
		if( userFeedback != null && userFeedback.getUserFeedback() == Validation.END_EXPERIMENT ) return true;  // we're done when the user says so
		return false;
	}

	@Override
	public void newIteration() {
		super.newIteration();
		// TODO: Save all the objects that we used in the previous iteration.
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
	public UFLControlLogic getControlLogic() {
		return new IndependentSequentialLogic();
	}
	
	@Override
	public String getDescription() {
		return  "Everything for this experiment is manually chosen by the user, through the user interface.\n" +
				"The source and target ontologies are loaded into AgreementMaker,\n" + 
				"and the reference alignment is imported.";
	}
}
