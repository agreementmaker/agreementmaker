package am.extension.userfeedback.common;

import java.util.LinkedList;
import java.util.List;

/**
 * The experiment data must contain an initial state, which is the first
 * iteration added using {@link #addIteration(ExperimentIteration)}.
 * 
 * @author cosmin
 *
 */
public class ExperimentData {

	public static enum DataSeries {
		PRECISION, RECALL, FMEASURE, DELTA_FROM_REF;
	}
	
	private List<ExperimentIteration> iterations = new LinkedList<>();
	
	/**
	 * This method will automatically set the interation number of the
	 * ExperimentIteration passed in.  If it's the initial state, it will get number 0.
	 * 
	 * @param iter
	 */
	public void addIteration(ExperimentIteration iter) {
		iter.iterationNum = iterations.size();
		iterations.add(iter);
	}
	
	public int numIterations() {
		return iterations.size();
	}
	
	/**
	 * @param iteration
	 *            Iteration 0 is the initial state. User feedback iterations are
	 *            numbers from 1 up.
	 */
	public ExperimentIteration getIteration(int iteration) {
		return iterations.get(iteration);
	}
	
	public double[] getSeries(DataSeries series) {
		double[] ret = new double[iterations.size()];
		int i = 0;
		for (ExperimentIteration iteration : iterations) {
			switch (series) {
			case PRECISION:
				ret[i] = iteration.getPrecision(); 
				break;
			case RECALL:
				ret[i] = iteration.getRecall();
				break;
			case FMEASURE:
				ret[i] = iteration.getFMeasure();
				break;
			case DELTA_FROM_REF:
				ret[i] = (double) iteration.getDelta();
				break;
			}
			i++;
		}
		return ret;
	}
	
	@Override
	public boolean equals(Object that) {
		if (this == that) {
			return true;
		}
		
		if (that instanceof ExperimentData) {
			ExperimentData thatData = (ExperimentData) that;
			
			if (this.iterations.size() != thatData.iterations.size()) {
				return false;
			}
			
			for (int i = 0; i < this.iterations.size(); i++) {
				if (!iterations.get(i).equals(thatData.iterations.get(i))) {
					return false;
				}
			}
			
			return true;
		}
		
		return false;
	}
}
