package am.evaluation.alignment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.Mapping;
import am.app.ontology.Node;

/**
 * Given an alignment, calculate the delta from the reference alignment.
 * 
 * The delta is based on edit distance, # of insertions + # of deletions.
 *
 * @author Cosmin Stroe
 */
public class DeltaFromReference {

	private Alignment<Mapping> referenceAlignment;
	
	public DeltaFromReference(Alignment<Mapping> referenceAlignment) {
		this.referenceAlignment = referenceAlignment;
	}
	
	/**
	 * Calculate the delta between the referenceAlignment and a given alignment.
	 * @return Number of edit operations needed to transform the alignment into the referenceAlignment.
	 */
	public int getDelta( Alignment<Mapping> alignment ) {
		
		int correctMappings = 0;
		// keep track of the mappings discovered in order to avoid counting duplicates more than once
		//HashMap<Mapping,Boolean> mappingsDiscovered = new HashMap<Mapping,Boolean>();
		
		for( Mapping m : alignment ) {
			if( referenceAlignment.contains(m.getEntity1(), m.getEntity2(), m.getRelation()) ) {
				correctMappings++;
			}
		}
		
		// incorrectMappings = alignment.size() - correctMappings;
		Logger log = Logger.getLogger(this.getClass());
		log.debug("Deletions: " + (alignment.size() - correctMappings) + " Insertions: " + (referenceAlignment.size() - correctMappings));
		
	//  return         number of deletions          +              number of insertions
		return (alignment.size() - correctMappings) + (referenceAlignment.size() - correctMappings);
	}
}
