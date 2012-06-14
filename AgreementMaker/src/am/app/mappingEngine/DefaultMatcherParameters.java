package am.app.mappingEngine;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
/**
 * TODO: Add the source and target ontologies as fields HERE!  -- Maybe not, because they cannot be serialized. -- Cosmin.
 * 
 * @author joe
 *
 */
public class DefaultMatcherParameters implements Serializable {

	private static final long serialVersionUID = -5278587933670353897L;
	
	// fields are initialized to their defaults.
	public double threshold = 0.6;
	public int maxSourceAlign = 1;
	public int maxTargetAlign = 1;
	public boolean completionMode = false;
	public boolean storeProvenance = false; // whether the matcher stores provenance information for mappings.
	public boolean largeOntologyMode = false;//if true values in the sparse matrix are thrown away to save memory
	
	public boolean threadedExecution = false;
	public boolean threadedOverlap = false;
	public int threadedReservedProcessors = 0; // how many processors are reserved (i.e. not used by the threaded mode)
	
	/** the input to the matcher */
	public List<MatchingTask> inputResults = new ArrayList<MatchingTask>();
	
	public DefaultMatcherParameters() { /* work is done by the field initialization; */ }
	
	public DefaultMatcherParameters(double threshold, int maxSourceAlign,
			int maxTargetAlign) {
		this.threshold = threshold;
		this.maxSourceAlign = maxSourceAlign;
		this.maxTargetAlign = maxTargetAlign;
	}

}
