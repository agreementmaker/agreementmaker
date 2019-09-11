package am.app.ontology.instance.datasets;

import java.util.Iterator;
import java.util.List;

import am.AMException;
import am.app.ontology.instance.Instance;
import am.app.ontology.instance.InstanceDataset;
import am.app.ontology.instance.endpoint.GeoNamesEndpoint;

/**
 * An instance dataset for a GeoNames endpoint.
 */
public class GeoNamesInstanceDataset implements InstanceDataset {

	private GeoNamesEndpoint instanceSource;
	
	public GeoNamesInstanceDataset(GeoNamesEndpoint endpoint) {
		instanceSource = endpoint;
	}
	
	@Override public boolean isIterable() { return false; }
	@Override public long size() { return -1; }

	@Override
	public List<Instance> getInstances(String type, int limit)
			throws AMException {
		try {
			return instanceSource.listInstances( type, limit );
		} catch( Exception e ) {
			e.printStackTrace();
			throw new AMException(e.getMessage());
		}
	}

	@Override
	public List<Instance> getCandidateInstances(String searchTerm, String type)
			throws AMException {
		try {
			return instanceSource.freeTextQuery(searchTerm, type);
		} catch( Exception e ) {
			e.printStackTrace();
			throw new AMException(e.getMessage());
		}
	}

	@Override
	public Iterator<Instance> getInstances() throws AMException {
		throw new AMException("This functionality is not available for an endpoint.");
	}

	@Override
	public Instance getInstance(String uri) throws AMException {
		throw new AMException("This feature is not yet implemented.");
	}
	
	public void setCacheFile(String cacheFile){
		instanceSource.setCacheFile(cacheFile);
	}

	public void persistCache(){
		try {
			instanceSource.persistCache();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}

