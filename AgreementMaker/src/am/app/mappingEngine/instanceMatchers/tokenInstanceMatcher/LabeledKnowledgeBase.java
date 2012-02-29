package am.app.mappingEngine.instanceMatchers.tokenInstanceMatcher;

import java.util.List;

import com.hp.hpl.jena.rdf.model.Statement;

public interface LabeledKnowledgeBase {
	
	/**
	 * Given a URI, returns the text representation of the resource having that URI, 
	 * also known as label.
	 *  
	 * @param URI
	 * @return
	 */
	public String getlabelFromURI(String URI);	
}
