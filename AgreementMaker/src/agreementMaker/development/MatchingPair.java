package agreementMaker.development;

/**
 * This class represents a mapping pair between to nodes in the reference file
 * 
 *
 */
public class MatchingPair {
	/**the name of the label of the source node*/
	public String sourcename;
	/**the name of the label of the target node */
	public String targetname;

	public MatchingPair() {}
	
	public MatchingPair(String s, String t) {
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
	
	public boolean equals(Object o) { 
		if(o instanceof MatchingPair) {
			MatchingPair mp = (MatchingPair)o;
			if(sourcename.equalsIgnoreCase(mp.sourcename) && targetname.equalsIgnoreCase(mp.targetname))
				return true;
		}
		return false;
	}
}
