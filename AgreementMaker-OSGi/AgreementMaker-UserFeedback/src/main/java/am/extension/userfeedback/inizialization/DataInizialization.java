package am.extension.userfeedback.inizialization;

import am.app.mappingEngine.similarityMatrix.SimilarityMatrix;
import am.extension.userfeedback.FeedbackLoopInizialization;
import am.extension.userfeedback.MLFeedback.MLFExperiment;

public class DataInizialization extends FeedbackLoopInizialization<MLFExperiment> {

	public DataInizialization()
	{
		super();
	}
	
	@Override
	public void inizialize(MLFExperiment exp) {
		// TODO Auto-generated method stub
		SimilarityMatrix smClass=exp.initialMatcher.getFinalMatcher().getClassesMatrix().clone();
		SimilarityMatrix smProperty=exp.initialMatcher.getFinalMatcher().getPropertiesMatrix().clone();
		for(int i=0;i<smClass.getRows();i++)
			for(int j=0;j<smClass.getColumns();j++)
				smClass.setSimilarity(i, j, 0.5);
		for(int i=0;i<smProperty.getRows();i++)
			for(int j=0;j<smProperty.getColumns();j++)
				smProperty.setSimilarity(i, j, 0.5);
		exp.setUflClassMatrix(smClass);
		exp.setUflPropertyMatrix(smProperty);
		
		done();
	}


}
