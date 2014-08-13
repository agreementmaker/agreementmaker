package am.extension.userfeedback;

import java.io.IOException;

import am.extension.userfeedback.common.ExperimentData;
import am.extension.userfeedback.common.ExperimentData.DataSeries;
import am.extension.userfeedback.evaluation.CandidateSelectionEvaluation;
import am.extension.userfeedback.evaluation.MultiplexCandidateSelectionEvaluation;
import am.extension.userfeedback.evaluation.RankingAccuracy;
import am.extension.userfeedback.evaluation.RankingAccuracy.ServerCSEvaluationData;
import am.extension.userfeedback.experiments.UFLExperiment;


public class ServerStatistics extends UFLStatistics {
	
	private UFLExperiment experiment;
	
	@Override
	public void compute(UFLExperiment exp) 
	{
		this.experiment=exp;
		ExperimentData data = exp.experimentData;
		
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
		
		writeLog(data, dataCS);
		
		try {
			experiment.logFile.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	public double computeDelta(ExperimentData data, DataSeries series)
	{
		double[] dataForSeries = data.getSeries(series);
		return dataForSeries[dataForSeries.length-1] - dataForSeries[0];
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
	
	
	private void writeLog(ExperimentData dataFPE, ServerCSEvaluationData dataCSE)
	{
		double dDelta = computeDelta(dataFPE, DataSeries.DELTA_FROM_REF);
		double dRecall = computeDelta(dataFPE, DataSeries.RECALL)*100;
		double dPrecision = computeDelta(dataFPE, DataSeries.PRECISION)*100;
		double dFMeasure = computeDelta(dataFPE, DataSeries.FMEASURE)*100;
		
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
		  .append(" Recacall Delta:  ").append(dRecall).append("\n")
		  .append(" FMeasure Delta:  ").append(dFMeasure).append("\n");
		
		experiment.info(sb.toString());
	}
}
