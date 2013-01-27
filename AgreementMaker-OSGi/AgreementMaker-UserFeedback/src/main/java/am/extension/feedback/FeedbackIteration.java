package am.extension.feedback;

import am.app.mappingEngine.ReferenceEvaluationData;

public class FeedbackIteration{

	public int iteration;
	public ReferenceEvaluationData evaluationData;
	public int EDSIcorrect =0;
	public int EDSIwrong =0;
	public int EFScorrect =0;
	public int EFSwrong =0;
	
	public FeedbackIteration(int n){
		iteration = n;
		
	}
}
