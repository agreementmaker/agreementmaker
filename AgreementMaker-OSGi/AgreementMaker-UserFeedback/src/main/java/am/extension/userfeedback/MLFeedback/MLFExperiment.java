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
import am.app.mappingEngine.MatchingTask;
import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.mappingEngine.referenceAlignment.ReferenceAlignmentMatcher;
import am.app.mappingEngine.similarityMatrix.SimilarityMatrix;
import am.app.mappingEngine.similarityMatrix.SparseMatrix;
import am.app.ontology.Ontology;
import am.extension.userfeedback.UFLExperiment;
import am.extension.userfeedback.UserFeedback.Validation;
import am.extension.userfeedback.experiments.IndependentSequentialLogic;
import am.extension.userfeedback.experiments.IndependentSequentialLogicMultiUser;
import am.extension.userfeedback.experiments.UFLControlLogic;

public class MLFExperiment extends UFLExperiment {

private BufferedWriter logFile;
//public TreeSet<Integer> forbidden_column_classes=new TreeSet<Integer>();
//public TreeSet<Integer> forbidden_row_classes=new TreeSet<Integer>();
//public TreeSet<Integer> forbidden_column_properties=new TreeSet<Integer>();
//public TreeSet<Integer> forbidden_row_properties=new TreeSet<Integer>();
private Alignment<Mapping> MLAlignment;
private Object[][] trainingSet_classes;
private Object[][] trainingSet_property;
private Object[][] dataSet_classes;
private Object[][] dataSet_property;
private SimilarityMatrix uflClassMatrix;
private SimilarityMatrix uflPropertyMatrix;
public List<Mapping> allRanked;
public List<Mapping> alreadyEvaluated;
public List<Mapping> conflictualClass;
public List<Mapping> conflictualProp;
//don't change the cardinality
private int sourceCardinality=1;
private int targetCardinality=1;
public SparseMatrix classesSparseMatrix=new SparseMatrix(Core.getInstance().getSourceOntology(),Core.getInstance().getTargetOntology(), alignType.aligningClasses);
public SparseMatrix propertiesSparseMatrix=new SparseMatrix(Core.getInstance().getSourceOntology(),Core.getInstance().getTargetOntology(), alignType.aligningProperties);


public SparseMatrix getClassesSparseMatrix() {
	return classesSparseMatrix;
}


public void setClassesSparseMatrix(SparseMatrix classesSparseMatrix) {
	this.classesSparseMatrix = classesSparseMatrix;
}


public SparseMatrix getPropertiesSparseMatrix() {
	return propertiesSparseMatrix;
}


public void setPropertiesSparseMatrix(SparseMatrix propertiesSparseMatrix) {
	this.propertiesSparseMatrix = propertiesSparseMatrix;
}


public int getSourceCardinality() {
	return sourceCardinality;
}


public void setSourceCardinality(int sourceCardinality) {
	this.sourceCardinality = sourceCardinality;
}


public int getTargetCardinality() {
	return targetCardinality;
}


public void setTargetCardinality(int targetCardinality) {
	this.targetCardinality = targetCardinality;
}


public Object[][] getTrainingSet_classes() {
	return trainingSet_classes;
}


public void setTrainingSet_classes(Object[][] trainingSet_classes) {
	this.trainingSet_classes = trainingSet_classes;
}


public Object[][] getTrainingSet_property() {
	return trainingSet_property;
}


public void setTrainingSet_property(Object[][] trainingSet_property) {
	this.trainingSet_property = trainingSet_property;
}


public Object[][] getDataSet_classes() {
	return dataSet_classes;
}


public void setDataSet_classes(Object[][] dataSet_classes) {
	this.dataSet_classes = dataSet_classes;
}


public Object[][] getDataSet_property() {
	return dataSet_property;
}


public void setDataSet_property(Object[][] dataSet_property) {
	this.dataSet_property = dataSet_property;
}



public SimilarityMatrix getUflClassMatrix() {
	return uflClassMatrix;
}


public void setUflClassMatrix(SimilarityMatrix uflClassMatrix) {
	this.uflClassMatrix = uflClassMatrix;
}


public SimilarityMatrix getUflPropertyMatrix() {
	return uflPropertyMatrix;
}


public void setUflPropertyMatrix(SimilarityMatrix uflPropertyMatrix) {
	this.uflPropertyMatrix = uflPropertyMatrix;
}

public Alignment<Mapping> getMLAlignment() {
	return MLAlignment;
}


public void setMLAlignment(Alignment<Mapping> mLAlignment) {
	MLAlignment = mLAlignment;
}




	public MLFExperiment() {
		// setup the log file
		try {
			FileWriter fr = new FileWriter("C:/Users/GELI/WorkFolder/ufllog.txt");
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
		return new IndependentSequentialLogicMultiUser();
		//return new IndependentSequentialLogic();
	}

	@Override
	public String getDescription() {
		return "Work in progress";
	}	
}
