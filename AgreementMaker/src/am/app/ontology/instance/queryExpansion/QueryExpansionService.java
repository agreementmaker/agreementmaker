package am.app.ontology.instance.queryExpansion;

import java.util.List;

/**
 * Interface representing a query expansion service, used for 
 * generating multiple queries containing additional information
 * with respect to an initial query
 *  
 * @author Federico Caimi
 *
 */
public interface QueryExpansionService {

	/**
	 * Given a query, it returns a list of expanded queries
	 */
	public List<String> expandQuery(String query);
	
	/**
	 * Given a query, it returns a list of expanded queries limited to
	 * a parameter
	 */
	public List<String> expandQuery(String query, int limit);
	
}
