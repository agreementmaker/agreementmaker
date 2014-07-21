package am.extension.userfeedback;

import java.io.IOException;

import am.extension.multiUserFeedback.experiment.MUExperiment;
import am.extension.userfeedback.common.CandidateMappingEvaluation;
import am.extension.userfeedback.common.ServerFeedbackEvaluationData;
import am.extension.userfeedback.evaluation.CandidateSelectionEvaluation;
import am.extension.userfeedback.evaluation.MultiplexCandidateSelectionEvaluation;
import am.extension.userfeedback.evaluation.RankingAccuracy;
import am.extension.userfeedback.evaluation.RankingAccuracy.ServerCSEvaluationData;


public class ServerStatistics extends UFLStatistics<MUExperiment>{
	MUExperiment experiment;
	@Override
	public void compute(MUExperiment exp) 
	{
		this.experiment=exp;
		// TODO Auto-generated method stub
		ServerFeedbackEvaluationData dataPE = exp.feedbackEvaluationData;
		
		CandidateSelectionEvaluation cse=exp.csEvaluation;
		ServerCSEvaluationData dataCS=null;
		if( cse instanceof MultiplexCandidateSelectionEvaluation ) 
		{
			MultiplexCandidateSelectionEvaluation multiplexeval = (MultiplexCandidateSelectionEvaluation) cse;
			RankingAccuracy ra = (RankingAccuracy) multiplexeval.getEvaluation(RankingAccuracy.class);
			dataCS = ra.getData();
		} else {
			throw new RuntimeException("Expecting server feedback evalution data");
		}
		
		writeLog(dataPE, dataCS);
		
		try {
			experiment.logFile.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	private double computeDelta(double[] array)
	{
		return array[array.length-1]-array[0];
	}
	
	private int computeDelta(int[] array)
	{
		return array[0]-array[array.length-1];
	}
	
	private double computeAUC(double[] array)
	{
		double auc=0;
		for (double d :array)
		{
			auc+=d;
		}
		return auc;
	}
	
	private int getFeedbackNumber(int[] array)
	{
		return array[array.length-1];
	}
	
	
	private void writeLog(ServerFeedbackEvaluationData dataFPE, ServerCSEvaluationData dataCSE)
	{
		double dDelta = computeDelta(dataFPE.deltaArray);
		double dRecall = computeDelta(dataFPE.recallArray)*100;
		double dPrecision = computeDelta(dataFPE.precisionArray)*100;
		double dFMeasure = computeDelta(dataFPE.fmeasureArray)*100;
		
		double auc = computeAUC(dataCSE.accuracy);
		int falsePositive = getFeedbackNumber(dataCSE.falsePositive);
		int falseNegative = getFeedbackNumber(dataCSE.falseNegative);
		int totalFeedback = getFeedbackNumber(dataCSE.totalFeedback);
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("\n------------------------------SUMMARY------------------------------\n\n")
		  .append("Candidate Selection Evaluation Data\n")
		  .append("Area Under Curve:  ").append(auc).append("\n")
		  .append("# False Positive:  ").append(falsePositive).append("\n")
		  .append("# False Negative:  ").append(falseNegative).append("\n")
		  .append("# Total Feedback:  ").append(totalFeedback).append("\n");
		
		sb.append("\nFeedback propagation Evaluation Data\n")
		  .append("    Delta Delta:  ").append(dDelta).append("\n")
		  .append("Precision Delta:  ").append(dPrecision).append("\n")
		  .append("  RecacallDelta:  ").append(dRecall).append("\n")
		  .append(" FMeasure Delta:  ").append(dFMeasure).append("\n");
		
		experiment.info(sb.toString());
	}
}
