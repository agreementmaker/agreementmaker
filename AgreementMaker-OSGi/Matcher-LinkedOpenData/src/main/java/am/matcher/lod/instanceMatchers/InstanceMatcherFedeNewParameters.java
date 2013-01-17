package am.app.mappingEngine.instanceMatchers;

import am.app.mappingEngine.instance.DefaultInstanceMatcherParameters;

/**
 * NOTE: Don't forget to set the threshold in the superclass.
 * 
 * @author Cosmin Stroe
 *
 */
public class InstanceMatcherFedeNewParameters extends DefaultInstanceMatcherParameters {

	private static final long serialVersionUID = -1048075119077822762L;

	public boolean useSTIM = true;
	
	public boolean useLIM = true;	
	
	public boolean useTIM = true;	
	
	public String outputFilename = "alignments.rdf";
	
	
	/**
	 * Whether or not to perform the disambiguation. If we do not perform
	 * disambiguation, any match with more than one candidate will not be
	 * matched.
	 */
	public boolean disambiguate = true;
}
