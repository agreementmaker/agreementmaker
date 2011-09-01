package am.app.mappingEngine;

public class AbstractParameters {

	// fields are initialized to their defaults.
	public double threshold = 0.6;
	public int maxSourceAlign = 1;
	public int maxTargetAlign = 1;
	public boolean completionMode = false;
	public boolean storeProvenance = false; // whether the matcher stores provenance information for mappings.
	protected boolean largeOntologyMode = false;//if true values in the sparse matrix are thrown away to save memory
	
	
	public AbstractParameters() { /* work is done by the field initialization; */ }
	
	public AbstractParameters(double threshold, int maxSourceAlign,
			int maxTargetAlign) {
		this.threshold = threshold;
		this.maxSourceAlign = maxSourceAlign;
		this.maxTargetAlign = maxTargetAlign;
	}

}
