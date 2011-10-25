package am.app.ontology.instance.endpoint;

import java.util.List;

import am.AMException;
import am.app.ontology.instance.Instance;

/**
 * SPARQL Endpoint wrapper.
 * 
 * @author federico
 *
 */
public class SparqlEndpoint implements SemanticWebEndpoint {

	@Override
	public List<Instance> freeTextQuery(String searchTerm, String type) throws Exception {
		throw new AMException("Method not implemented.");
	}

	@Override
	public String getPropertyValue(Instance i, String propertyURI) throws Exception {
		throw new AMException("Method not implemented.");
	}

	@Override
	public List<Instance> listInstances(String type, int limit)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
