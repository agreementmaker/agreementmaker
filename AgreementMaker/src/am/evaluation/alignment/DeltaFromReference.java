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
	
	private HashMap<Node,List<Mapping>> sourceNodeMap = new HashMap<Node,List<Mapping>>();
	//private HashMap<Node,List<Mapping>> targetNodeMap = new HashMap<Node,List<Mapping>>();
	
	public DeltaFromReference(Alignment<Mapping> referenceAlignment) {
		this.referenceAlignment = referenceAlignment;
		
		// create the hashmaps (used to check if nodes are part of the reference alignment)
		for( Mapping m : referenceAlignment ) {
			Node sourceNode = m.getEntity1();
			
			if( sourceNodeMap.containsKey(sourceNode) ) {
				List<Mapping> mappingList = sourceNodeMap.get(sourceNode);
				mappingList.add(m);
			} else {
				List<Mapping> mappingList = new ArrayList<Mapping>();
				mappingList.add(m);
				sourceNodeMap.put(sourceNode, mappingList);
			}
			
			//Node targetNode = m.getEntity2();
			//targetNodeMap.put(targetNode, m);
		}
	}
	
	/**
	 * Calculate the delta between the referenceAlignment and a given alignment.
	 * @return Number of edit operations needed to transform the alignment into the referenceAlignment.
	 */
	public int getDelta( Alignment<Mapping> alignment ) {
		
		int correctMappings = 0;
		// keep track of the mappings discovered in order to avoid counting duplicates more than once
		HashMap<Mapping,Boolean> mappingsDiscovered = new HashMap<Mapping,Boolean>();
		
		for( Mapping currentMapping : alignment ) {
			
			if( mappingsDiscovered.containsKey(currentMapping) ) continue; // this mapping is a duplicate
			
			Node sourceNode = currentMapping.getEntity1();
			Node targetNode = currentMapping.getEntity2();
			
			// check to see if the mapping is correct
			boolean mappingIsCorrect = false;
			
			if( sourceNodeMap.containsKey(sourceNode) ) {
				List<Mapping> referenceMappingList = sourceNodeMap.get(sourceNode);
				for( Mapping referenceMapping : referenceMappingList ) {
					if( referenceMapping.getEntity2().equals(targetNode) ) {
						mappingIsCorrect = true;
						mappingsDiscovered.put(currentMapping, new Boolean(true) );
					}
				}
			}
			
			if( mappingIsCorrect ) correctMappings++;
		}
		
		// incorrectMappings = alignment.size() - correctMappings;
		Logger log = Logger.getLogger(this.getClass());
		log.debug("Deletions: " + (alignment.size() - correctMappings) + " Insertions: " + (referenceAlignment.size() - correctMappings));
		
	//  return         number of deletions          +              number of insertions
		return (alignment.size() - correctMappings) + (referenceAlignment.size() - correctMappings);
	}
}
