package am.evaluation.alignment;

import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.Mapping;

import com.ibm.icu.text.NumberFormat;

/**
 * This class implements the following metrics:
 * 
 * Precision = # of correct mappings / # of mappings in computed alignment.
 * Recall = # of correct mappings / # of mappings in reference alignment.
 * 
 * F-Measure = harmonic mean of precision and recall = 2PR/(P+R)
 * 
 * @author cosmin
 *
 */
public class AlignmentMetrics {

	private Alignment<Mapping> referenceAlignment;
	private Alignment<Mapping> computedAlignment;
	
	private int numCorrect;
	
	/**
	 * @param referenceAlignment The set of correct mappings.
	 * @param computedAlignment The set that must be evaluated.
	 */
	public AlignmentMetrics(Alignment<Mapping> referenceAlignment, Alignment<Mapping> computedAlignment) {	
		
		this.referenceAlignment = referenceAlignment;
		this.computedAlignment = computedAlignment;
		
		numCorrect = computeNumCorrect();
	}
	
	private int computeNumCorrect() {
		if( referenceAlignment == null || computedAlignment == null ) return 0;
		
		int numCorrect = 0;
		for( Mapping computedMapping : computedAlignment ) {
			if( referenceAlignment.contains(computedMapping.getEntity1(), computedMapping.getEntity2())!=null ) {//, computedMapping.getRelation()) ) {
				numCorrect++;
			}
		}
		return numCorrect;
	}
	
	/** @return numCorrect / computedAlignment.size(). */
	public double getPrecision() { 
		return (new Integer(getNumCorrect())).doubleValue() / (new Integer(getNumFound()).doubleValue()); 
	}
	
	public String getPrecisionPercent() {
		double precision = getPrecision();
		NumberFormat numForm = NumberFormat.getPercentInstance();
		numForm.setMaximumFractionDigits(2);
		return numForm.format(precision);
	}
	
	/** @return numCorrect / referenceAlignment.size(). */
	public double getRecall() { 
		return (new Integer(getNumCorrect())).doubleValue() / (new Integer(getNumInReference())).doubleValue(); 
	}
	
	public String getRecallPercent() {
		double recall = getRecall();
		NumberFormat numForm = NumberFormat.getPercentInstance();
		numForm.setMaximumFractionDigits(2);
		return numForm.format(recall);
	}
	
	/** @return FMeasure = ( 2 * precision * recall ) / ( precision + recall ); */
	public double getFMeasure() {
		double precision = getPrecision();
		double recall = getRecall();
		return (2.0d * precision * recall) / (precision + recall);
	}
	
	public String getFMeasurePercent() {
		double fmeasure = getFMeasure();
		NumberFormat numForm = NumberFormat.getPercentInstance();
		numForm.setMaximumFractionDigits(2);
		return numForm.format(fmeasure);
	}
	
	public int getNumCorrect() {
		return numCorrect;
	}
	
	public int getNumFound() {
		return computedAlignment.size();
	}
	
	public int getNumInReference() {
		return referenceAlignment.size();
	}
}
