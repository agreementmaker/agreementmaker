package am.extension.userfeedback.common;

public class ExperimentIteration {

	public int iterationNum;
	
	private double r, p;
	private int d;
	
	public ExperimentIteration(double precision, double recall, int deltaFromRef) {
		this.p = precision;
		this.r = recall;
		this.d = deltaFromRef;
	}
	
	public double getRecall() {
		return r;
	}
	
	public double getPrecision() {
		return p;
	}
	
	public int getDelta() {
		return d;
	}
	
	/**
	 * @return -1 if there is an error
	 */
	public double getFMeasure() {
		if (p > 1.0 || p < 0) return -1;
		if (r > 1.0 || r < 0) return -1;
		if (p + r == 0) return 0;
		return 2.0d * ((p * r) / ( p + r ));
	}
	
	@Override
	public boolean equals(Object that) {
		if (this == that) {
			return true;
		}
		
		if (that instanceof ExperimentIteration) {
			ExperimentIteration thatIteration = (ExperimentIteration) that;
			return this.iterationNum == thatIteration.iterationNum &&
				   this.p == thatIteration.p &&
				   this.r == thatIteration.r &&
				   this.d == thatIteration.d;
		}
		return false;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Iteration: ").append(iterationNum)
		   .append(", Delta from reference: ").append(d)
		   .append(", Precision: ").append(p)
		   .append(", Recall: ").append(r)
		   .append(", FMeasure: ").append(getFMeasure());
		return sb.toString();
	}
}
