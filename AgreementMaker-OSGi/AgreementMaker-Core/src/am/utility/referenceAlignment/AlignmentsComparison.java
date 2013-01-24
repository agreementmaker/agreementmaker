package am.utility.referenceAlignment;

import java.util.ArrayList;
import java.util.List;

import am.app.mappingEngine.referenceAlignment.MatchingPair;

/**
 * Class representing the differences between alignments
 * 
 * 
 * @author federico
 *
 */
public class AlignmentsComparison {
	/**
	 * Comparison mappings
	 */
	//Mappings that are in the source alignment and not in the target alignment  
	private List<MatchingPair> notInSource;
	//Mappings that are in the target alignment and not in the source alignment  
	private List<MatchingPair> notInTarget;
	//Mappings sharing same source and target but different relation
	//The two lists are coupled.
	private List<MatchingPair> differentRelationInSource;
	private List<MatchingPair> differentRelationInTarget;
	
	private List<MatchingPair> equalMappingsInSource;
	private List<MatchingPair> equalMappingsInTarget;
		
	/**
	 * Unsolvable mappings
	 */
	//Mappings where the source is not in the source list of concepts/properties
	private List<MatchingPair> nonSolvableSource; 
	//Mappings where the target is not in the target list of concepts/properties
	private List<MatchingPair> nonSolvableTarget; 
	
	public AlignmentsComparison(){
		notInSource = new ArrayList<MatchingPair>();
		notInTarget = new ArrayList<MatchingPair>();
		nonSolvableSource = new ArrayList<MatchingPair>();
		nonSolvableTarget = new ArrayList<MatchingPair>();
		differentRelationInSource = new ArrayList<MatchingPair>();
		differentRelationInTarget = new ArrayList<MatchingPair>();
		equalMappingsInSource = new ArrayList<MatchingPair>();
		equalMappingsInTarget = new ArrayList<MatchingPair>();
	}
	
	public List<MatchingPair> getNotInSource() {
		return notInSource;
	}
	
	public List<MatchingPair> getNotInTarget() {
		return notInTarget;
	}

	public List<MatchingPair> getNonSolvableTarget() {
		return nonSolvableTarget;
	}

	public List<MatchingPair> getNonSolvableSource() {
		return nonSolvableSource;
	}
	
	public List<MatchingPair> getDifferentRelationInSource() {
		return differentRelationInSource;
	}

	public List<MatchingPair> getDifferentRelationInTarget() {
		return differentRelationInTarget;
	}	
	
	public List<MatchingPair> getEqualMappingsInSource() {
		return equalMappingsInSource;
	}
	
	public List<MatchingPair> getEqualMappingsInTarget() {
		return equalMappingsInTarget;
	}
	
	@Override
	public String toString() {
		String retValue = "";
		retValue += equalMappingsInSource + "\n";
		retValue += equalMappingsInTarget + "\n";
		retValue += notInSource + "\n";
		retValue += notInTarget + "\n";
		retValue += differentRelationInSource + "\n";
		retValue += differentRelationInTarget + "\n";
		return retValue;
	}
}
