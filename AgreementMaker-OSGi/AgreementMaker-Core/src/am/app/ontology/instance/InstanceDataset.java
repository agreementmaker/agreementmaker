package am.app.ontology.instance;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import am.AMException;

/**
 * 
 * An instance dataset is a way to access instance information in an ontology.
 *  
 * @see {@link edu.uic.advis.im.knowledgebase.ontology.OntModelInstanceDataset}
 */
public interface InstanceDataset {

	/**
	 * @return true if the instance dataset can be iterated through, false
	 *         otherwise. A Semantic Web Endpoint is an example of an endpoint
	 *         that cannot be iterated.
	 */
	public boolean isIterable();
	
	/**
	 * Get instances of a certain type, with at most limit instances returned.
	 * 
	 * @param limit
	 *            Should be a number greater than 0. If it is <= 0, the behavior
	 *            is unspecified.
	 */
	public Collection<Instance> getInstances( String type, int limit ) throws AMException;

	/**
	 * Given a keyword and an instance type, get a list of candidate instances.
	 * In case the type is null, we return instances of any type. The algorithms
	 * implemented here are based on a keyword lookup.
	 */
	public Collection<Instance> getCandidateInstances( String keyword, String type) throws AMException;
	
	/**
	 * For instance sources that are iterable, return an iterator over the
	 * complete list of instances.
	 */
	public Iterator<Instance> getInstances() throws AMException;
	
	/**
	 * @return An instance by its URI.
	 */
	public Instance getInstance(String uri) throws AMException;
	
	/**
	 * @return Returns the number of instances in the dataset. If the dataset is
	 *         not iterable, the return should be -1.
	 */
	public long size();
}
