package agreementMaker.development;

public class MatchingPairDesc extends MatchingPair {
	/**the description of the label of the source node*/
	public String sourcedesc;
	/**the description of the label of the target node*/
	public String targetdesc;
	
	public MatchingPairDesc() {super();}
	
	public MatchingPairDesc(String s, String t, String ds, String dt) {
		super(s,t);
		sourcedesc = ds;
		targetdesc = dt;
	}

}
