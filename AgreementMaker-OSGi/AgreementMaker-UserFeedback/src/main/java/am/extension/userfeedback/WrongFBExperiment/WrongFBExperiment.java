package am.extension.userfeedback.WrongFBExperiment;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import am.app.Core;
import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.MatchingTask;
import am.app.mappingEngine.referenceAlignment.ReferenceAlignmentMatcher;
import am.app.ontology.Ontology;
import am.extension.userfeedback.UFLExperiment;
import am.extension.userfeedback.experiments.IndependentSequentialLogic;
import am.extension.userfeedback.experiments.UFLControlLogic;

public class WrongFBExperiment extends UFLExperiment {
	private BufferedWriter logFile;
	public ArrayList<Integer> test = new ArrayList<Integer>();
	
	@Override
	public Ontology getSourceOntology() {
		// TODO Auto-generated method stub
		return Core.getInstance().getSourceOntology();
	}

	@Override
	public Ontology getTargetOntology() {
		// TODO Auto-generated method stub
		return Core.getInstance().getTargetOntology();
	}

	@Override
	public Alignment<Mapping> getReferenceAlignment() {
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
		return initialMatcher.getAlignment();
	}

	@Override
	public void info(String line) {
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
		return new IndependentSequentialLogic();
	}

	@Override
	public boolean experimentHasCompleted() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return "Work in progress";
	}

}
