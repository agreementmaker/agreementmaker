package am.app.ontology.instance.datasets;

import java.util.Iterator;
import java.util.List;

import am.AMException;
import am.app.ontology.instance.Instance;
import am.app.ontology.instance.InstanceDataset;
import am.app.ontology.instance.endpoint.SparqlEndpoint;

public class SparqlInstanceDataset implements InstanceDataset {
	private SparqlEndpoint endpoint;
	
	public SparqlInstanceDataset(SparqlEndpoint endpoint){
		this.endpoint = endpoint;
	}
		
	@Override
	public List<Instance> getInstances(String type, int limit)
			throws AMException {
		throw new AMException("Not yet implemented");
	}

	@Override
	public List<Instance> getCandidateInstances(String searchTerm, String type)
			throws AMException {
		try {
			return endpoint.freeTextQuery(searchTerm, type);
		} catch( Exception e ) {
			e.printStackTrace();
			throw new AMException(e.getMessage());
		}
	}

	@Override public boolean isIterable() { return false; }
	@Override public long size() { return -1; }
	
	@Override
	public Iterator<Instance> getInstances() throws AMException {
		throw new AMException("Not implemented in a Sparql dataset");
	}

	@Override
	public Instance getInstance(String uri) throws AMException {
		// TODO Auto-generated method stub
		return null;
	}

	public void setCacheFile(String cacheFile) {
		endpoint.setCacheFile(cacheFile);
	}

}
