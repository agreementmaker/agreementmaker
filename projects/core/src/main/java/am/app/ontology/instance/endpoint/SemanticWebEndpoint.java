package am.app.ontology.instance.endpoint;

import java.util.List;

import am.app.ontology.instance.Instance;

/**
 * Interface to abstract an endpoint on the Semantic Web.
 * 
 * @author federico
 *
 */
public interface SemanticWebEndpoint {

	public List<Instance> freeTextQuery(String searchTerm, String type) throws Exception;
	
	public String getPropertyValue( Instance i, String propertyURI ) throws Exception;

	/**
	 * List instances of a certain type, up to a limit.
	 * 
	 * NOTE: The limit will affect performance.
	 * 
	 * @param type
	 * @param limit
	 * @return
	 */
	public List<Instance> listInstances(String type, int limit) throws Exception;
}
