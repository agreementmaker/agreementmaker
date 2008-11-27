package agreementMaker.application.mappingEngine.referenceAlignment;

import agreementMaker.application.mappingEngine.Alignment;

/**
 * This class represents a mapping pair between to nodes in the reference file
 * 
 *
 */
public class MatchingPair {
	/**the name of the label of the source node, this is based on the idea that the name is unique in the ontology, for the OWL ontology this should be true because the name is the local name that is part of the URI. namespace#localname*/
	public String sourcename;
	/**the name of the label of the target node */
	public String targetname;
	
	/**the description of the source node*/
	public String sourcedesc;
	/**the description of the target node*/
	public String targetdesc;
	
	public double similarity;
	
	public String relation;

	public MatchingPair() {
	}
	
	public MatchingPair(String s, String t) {
		sourcename = s;
		targetname = t;
	}
	
	public MatchingPair(String s, String t, double sim, String rel) {
		similarity = sim;
		relation = rel;
		sourcename = s;
		targetname = t;
	}
	
	public boolean sameSource(MatchingPair mp) {
		if(sourcename.equalsIgnoreCase(mp.sourcename))
			return true;
		else return false;
	}
	
	public boolean sameTarget(MatchingPair mp) {
		if(targetname.equalsIgnoreCase(mp.targetname))
			return true;
		else return false;
	}
	
	public String getNameTabName() {
		return sourcename+"\t"+targetname;
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
