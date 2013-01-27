package am.matcher.MachineLearning;



import am.app.mappingEngine.DefaultMatcherParameters;

public class machineLearningParameters extends DefaultMatcherParameters {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public final static String Matsim = "Matcher Similarity";
	public final static String Matfound = "Matcher Similarity + Matcher found";
	public final static String Matvote = "Matcher Similarity + Matcher found + Matcher vote";
	public String featureType = Matsim; //selected math operation.
	public machineLearningParameters() { super(); }
	public machineLearningParameters(double th, int s, int t) { super(th, s, t); }
	
	
		

}
