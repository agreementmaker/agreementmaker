package am.app.ontology.instance;

import java.util.List;

import am.AMException;

/**
 * 
 * An instance dataset is a way to access instance information in an ontology.
 *  
 */
public interface InstanceDataset {

	/**
	 * @return true if the instance dataset can be iterated through, false otherwise.  A Semantic Web Endpoint is an example of an endpoint that cannot be iterated. 
	 */
	public boolean isIterable();
	
	/**
	 * Get instances of a certain type, with at most limit instances returned.
	 */
	public List<Instance> getInstances( String type, int limit ) throws AMException;

	/**
	 * Given a search term, and an instance type, get a list of candidate instances. 
	 */
	public List<Instance> getCandidateInstances( String searchTerm, String type) throws AMException;
	
	/**
	 * For instance sources that are iterable, return the complete list of instances.
	 */
	public List<Instance> getInstances() throws AMException;
	
	/**
	 * Get an instance by its URI.
	 */
	public Instance getInstance(String uri) throws AMException;
	
}
