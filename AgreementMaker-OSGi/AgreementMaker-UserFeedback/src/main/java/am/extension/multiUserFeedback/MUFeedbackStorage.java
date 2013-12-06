package am.extension.multiUserFeedback;

import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.similarityMatrix.SimilarityMatrix;
import am.extension.userfeedback.UFLExperiment;
import am.extension.userfeedback.UserFeedback.Validation;

public abstract class MUFeedbackStorage <T extends UFLExperiment>{
	SimilarityMatrix classes;
	SimilarityMatrix properties;

	public abstract void addFeedback(T exp, Mapping candidateMapping, Validation val, String id);
	
	public abstract Object[][] getTrainingSet();
	
	public abstract void computeFinalMatrix();
	
}
