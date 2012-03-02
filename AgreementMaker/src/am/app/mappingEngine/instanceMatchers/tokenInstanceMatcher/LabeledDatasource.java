package am.app.mappingEngine.instanceMatchers.tokenInstanceMatcher;

/**
 * FIXME: This interface should be in AgreementMaker. - Cosmin.
 */
public interface LabeledDatasource {
	
	/**
	 * Given a URI, returns the text representation of the resource having that URI, 
	 * also known as label.
	 *  
	 * @param URI
	 * @return
	 */
	public String getLabelFromURI(String URI);	
}
