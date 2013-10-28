/*
 * 	Francesco Loprete October 2013
 */
package am.extension.userfeedback.MLFeedback;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.TreeSet;

import am.app.Core;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.referenceAlignment.ReferenceAlignmentMatcher;
import am.app.ontology.Ontology;
import am.extension.userfeedback.UFLExperiment;
import am.extension.userfeedback.UserFeedback.Validation;
import am.extension.userfeedback.experiments.IndependentSequentialLogic;
import am.extension.userfeedback.experiments.UFLControlLogic;

public class MLFExperiment extends UFLExperiment {

private BufferedWriter logFile;
public TreeSet<Integer> forbidden_column=new TreeSet<Integer>();
public TreeSet<Integer> forbidden_row=new TreeSet<Integer>();
private Alignment<Mapping> MLAlignment;

public Alignment<Mapping> getMLAlignment() {
	return MLAlignment;
}



public void setMLAlignment(Alignment<Mapping> mLAlignment) {
	MLAlignment = mLAlignment;
}

public Object[][] getTrainingSet() {
	return trainingSet;
}

public void setTrainingSet(Object[][] trainingSet) {
	this.trainingSet = trainingSet;
}

private Object[][] trainingSet;


	public MLFExperiment() {
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
		List<AbstractMatcher> matchers = Core.getInstance().getMatcherInstances();
		for( AbstractMatcher m : matchers ) {
			if( m instanceof ReferenceAlignmentMatcher ) {
				// return the alignment of the first reference alignment matcher
				return m.getAlignment();
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
		return "Work in progress";
	}	
}
