package am.extension.userfeedback.clustering.disagreement;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import am.evaluation.alignment.AlignmentMetrics;
import am.evaluation.alignment.DeltaFromReference;
import am.extension.multiUserFeedback.experiment.MUExperiment;
import am.extension.userfeedback.evaluation.PropagationEvaluation;
import am.extension.userfeedback.experiments.MLFExperiment;
import am.extension.userfeedback.experiments.SUExperiment;
import am.extension.userfeedback.experiments.UFLExperiment;

public class SMatrixDeltaEvaluetion extends PropagationEvaluation<SUExperiment>  {
	
	private DeltaFromReference deltaFromReference;
	
	private BufferedWriter fileWriter = null;
	
	public SMatrixDeltaEvaluetion() {
		try {
			FileWriter fstream = new FileWriter("/home/frank/Desktop/deltaPlot.dat", true);
			BufferedWriter bfr = new BufferedWriter(fstream);
			fileWriter = bfr;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@Override
	public void evaluate(SUExperiment exp) {

		deltaFromReference = new DeltaFromReference(exp.getReferenceAlignment());
		
		int delta = deltaFromReference.getDelta(exp.getMLAlignment());//exp.getFinalAlignment());
		
		AlignmentMetrics metrics = new AlignmentMetrics(exp.getReferenceAlignment(), exp.getMLAlignment()); //exp.getFinalAlignment());
		
		//Logger log = Logger.getLogger(this.getClass());
		UFLExperiment log = exp;
		log.info("Iteration: " + exp.getIterationNumber() + ", Delta from reference: " + delta + 
				", Precision: " + metrics.getPrecisionPercent() + ", Recall: " + metrics.getRecallPercent() + ", FMeasure: " + metrics.getFMeasurePercent());
		log.info("");
		
		if( fileWriter != null ) {
			try {
				fileWriter.write(exp.getIterationNumber() + "," + delta + "\n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		done();
	}

	@Override
	protected void done() {
		try {
			fileWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		super.done();
	}
}

