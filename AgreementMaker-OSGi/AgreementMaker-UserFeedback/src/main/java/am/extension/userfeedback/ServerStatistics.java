package am.extension.userfeedback;

import java.io.IOException;

import am.extension.multiUserFeedback.experiment.MUExperiment;
import am.extension.userfeedback.common.ServerFeedbackEvaluationData;
import am.extension.userfeedback.evaluation.CandidateSelectionEvaluation;
import am.extension.userfeedback.evaluation.MultiplexCandidateSelectionEvaluation;
import am.extension.userfeedback.evaluation.PropagationEvaluation;
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
		}
		else
		{
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
		return array[0]-array[array.length-1];
	}
	
	private int computeDelta(int[] array)
	{
		return array[array.length-1]-array[0];
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
		double dDelta=computeDelta(dataFPE.deltaArray);
		double dRecall=computeDelta(dataFPE.recallArray);
		double dPrecision = computeDelta(dataFPE.precisionArray);
		double dFMeasure= computeDelta(dataFPE.fmeasureArray);
		
		double auc=computeAUC(dataCSE.accuracy);
		int falsePositive=getFeedbackNumber(dataCSE.falsePositive);
		int falseNegative=getFeedbackNumber(dataCSE.falseNegative);
		int totalFeedback=getFeedbackNumber(dataCSE.totalFeedback);
		
		experiment.info("");
		experiment.info("------------------------------SUMMARY------------------------------");
		experiment.info("");
		experiment.info("Candidate Selection Evaluation Data");
		experiment.info("AUC: "+auc+" ,#FalsePositive: "+falsePositive+" ,#FalseBegative: "+falseNegative+" ,#TotalFeedback: "+totalFeedback);
		experiment.info("");
		experiment.info("Feedback propagation Evaluation Data");
		experiment.info("DoD: "+dDelta+" ,PrecisionDelta: "+dPrecision+" ,RecacallDelta: "+dRecall+" ,FMeasureDelata: "+dFMeasure);
		
	}

}
