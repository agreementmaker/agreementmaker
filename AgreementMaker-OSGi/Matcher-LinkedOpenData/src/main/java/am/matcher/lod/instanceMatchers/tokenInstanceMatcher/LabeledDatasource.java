package am.matcher.lod.instanceMatchers.tokenInstanceMatcher;

import java.util.List;

/**
 * A datasource that has labels for its concepts. This interface abstracts a
 * Knowledge Base.
 */
public interface LabeledDatasource {
	
	/**
	 * Given a URI, returns the text representation of the resource having that
	 * URI, also known as label. If the resource has multiple labels, the
	 * returned value may be arbitrary, and you should use
	 * {@link #getLabelsFromURI(String)} instead.
	 */
	public String getLabelFromURI(String URI);

	/**
	 * Given a URI, returns the text representations of the resource having that URI, 
	 * also known as label.
	 */
	public List<String> getLabelsFromURI(String URI);	
}
