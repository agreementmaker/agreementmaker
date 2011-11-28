package am.app.userfeedback.clustering.disagreement;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import am.app.userfeedback.PropagationEvaluation;
import am.app.userfeedback.UFLExperiment;
import am.evaluation.alignment.AlignmentMetrics;
import am.evaluation.alignment.DeltaFromReference;

public class DeltaFromReferenceEvaluation extends PropagationEvaluation {
	
	private DeltaFromReference deltaFromReference;
	
	private BufferedWriter fileWriter = null;
	
	public DeltaFromReferenceEvaluation() {
		try {
			FileWriter fstream = new FileWriter("/home/cosmin/Desktop/deltaPlot.dat", true);
			BufferedWriter bfr = new BufferedWriter(fstream);
			fileWriter = bfr;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@Override
	public void evaluate(UFLExperiment exp) {

		deltaFromReference = new DeltaFromReference(exp.getReferenceAlignment());
		
		int delta = deltaFromReference.getDelta(exp.getFinalAlignment());
		
		AlignmentMetrics metrics = new AlignmentMetrics(exp.getReferenceAlignment(), exp.getFinalAlignment());
		
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

