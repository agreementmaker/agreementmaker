package am.app.userfeedbackloop.disagreementclustering;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.log4j.Logger;

import am.app.userfeedbackloop.PropagationEvaluation;
import am.app.userfeedbackloop.UFLExperiment;
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

		if( deltaFromReference == null ) {
			deltaFromReference = new DeltaFromReference(exp.getReferenceAlignment());
		}
		
		int delta = deltaFromReference.getDelta(exp.getFinalAlignment());
		
		Logger log = Logger.getLogger(this.getClass());
		log.info("Iteration: " + exp.getIterationNumber() + ", Delta from reference: " + delta);
		
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
