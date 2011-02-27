package am.app.mappingEngine.referenceAlignment;

import am.app.mappingEngine.Mapping.MappingRelation;


/**
 * This class represents a mapping pair between to nodes in the reference file
 * 
 *
 */
public class MatchingPair {
	/**the name of the label of the source node, this is based on the idea that the name is unique in the ontology, for the OWL ontology this should be true because the name is the local name that is part of the URI. namespace#localname*/
	public String sourceURI;
	/**the name of the label of the target node */
	public String targetURI;
	
	public double similarity;
	public MappingRelation relation;
	
	public String provenance;

	public MatchingPair() {
	}
	
	public MatchingPair(String s, String t) {
		sourceURI = s;
		targetURI = t;
	}
	
	public MatchingPair(String s, String t, double sim, MappingRelation rel) {
		similarity = sim;
		relation = rel;
		sourceURI = s;
		targetURI = t;	
	}
	
	public MatchingPair(String s, String t, double sim, MappingRelation rel, String p) {
		similarity = sim;
		relation = rel;
		sourceURI = s;
		targetURI = t;
		provenance = p;
	}
	
	public boolean sameSource(MatchingPair mp) {
		if(sourceURI.equalsIgnoreCase(mp.sourceURI))
			return true;
		else return false;
	}
	
	public boolean sameTarget(MatchingPair mp) {
		if(targetURI.equalsIgnoreCase(mp.targetURI))
			return true;
		else return false;
	}
	
	public String getNameTabName() {
		return sourceURI+"\t"+targetURI;
	}
	
	public String getSimTabRel() {
		return similarity+"\t"+relation;
	}
	
	public String getTabString() {
		return getNameTabName()+"\t"+getSimTabRel();
	}
	
	public boolean equals(Object o) { 
		if(o instanceof MatchingPair) {
			MatchingPair mp = (MatchingPair)o;
			if(sameSource(mp) &&sameTarget(mp))
				return true;
		}
		return false;
	}
}
