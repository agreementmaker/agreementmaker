package am.extension.userfeedback.evaluation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Serializable;
import java.util.Arrays;

import am.app.Core;
import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.Mapping;
import am.extension.userfeedback.experiments.UFLExperiment;
import am.extension.userfeedback.experiments.UFLExperimentParameters.Parameter;

public class RankingAccuracy extends CandidateSelectionEvaluation{
	private int falsePositive=0;
	private int falseNegative=0;
	private int count=0;
	private UFLExperiment experiment;
	
	private ServerCSEvaluationData data;
	
	@Override
	public void evaluate(UFLExperiment exp) {

		Alignment<Mapping> referenceAlignment = exp.getReferenceAlignment();
		
		Mapping candidateMapping = exp.candidateSelection.getSelectedMapping();
		
		this.experiment=exp;

		Alignment<Mapping> computedAlignment=exp.getFinalAlignment();
		try{
			if ((computedAlignment.contains(candidateMapping))&&(!referenceAlignment.contains(candidateMapping)))
			{
				falsePositive++;
			}
			if ((!computedAlignment.contains(candidateMapping))&&(referenceAlignment.contains(candidateMapping)))
			{
				falseNegative++;
			}
		}
		catch (Exception e)
		{
			System.out.println("bwhbow");
		}
		count++;
		
		double accuracy=((double)(falseNegative+falsePositive))/(double)count;
		
		try {
			writeAccuracy(accuracy, candidateMapping);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		if( data == null) {
			int numIterations = exp.setup.parameters.getIntParameter(Parameter.NUM_ITERATIONS);
			data = new ServerCSEvaluationData(numIterations);
		}
	
		// save all the values
		int currentIteration = exp.getIterationNumber()-1;
		data.accuracy[currentIteration] = accuracy;
		data.falsePositive   [currentIteration] =falsePositive;
		data.falseNegative [currentIteration] = falseNegative;
		data.totalFeedback    [currentIteration] = count;
		
		
		done();
		
	}
	
	
	private void writeAccuracy(double accuracy, Mapping mp) throws Exception
	{
		if (mp!=null)
		{
			String currentLog = experiment.setup.parameters.getParameter(Parameter.LOGFILE);
			File file = new File(Core.getInstance().getRoot() + currentLog + "-accuracy.txt");
	
			FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(accuracy+" "+falsePositive+" "+falseNegative+" "+count+" "+mp.toString()+"\n");
			bw.close();
		}
	}
	
	
	public ServerCSEvaluationData getData() {
		return data;
	}
	
public class ServerCSEvaluationData implements Serializable {
		

		
	private static final long serialVersionUID = 2530851739600524219L;
	
		public double[] accuracy; // the precision for each iteration
		public int[] falsePositive;    // the recall for each iteration
		public int[] falseNegative;  // the fmeasure for each iteration
		public int[] totalFeedback;        // the delta from reference for each iteration
		
		public ServerCSEvaluationData(int numIterations) {
			// +1 because we will store the initial matchers data also.
			falsePositive = new int[numIterations+1]; 
			falseNegative    = new int[numIterations+1];
			accuracy  = new double[numIterations+1];
			totalFeedback     = new int[numIterations+1];
		}
		
		@Override
		public boolean equals(Object obj) {
			if( !(obj instanceof ServerCSEvaluationData) ) return false;
			
			ServerCSEvaluationData data = (ServerCSEvaluationData) obj;
			
			return Arrays.equals(data.accuracy, accuracy) &&
				   Arrays.equals(data.falsePositive, falsePositive) &&
				   Arrays.equals(data.falseNegative, data.falseNegative) &&
				   Arrays.equals(data.totalFeedback, data.totalFeedback);
		}
	}

}
