package am.app.ontology.instance;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import am.AMException;
import am.app.ontology.Ontology;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 * An instance dataset using instances stored in the loaded ontology.
 */
public class OntologyInstanceDataset implements InstanceDataset {

	private Ontology instanceSource;
	
	public OntologyInstanceDataset(Ontology ont) {
		this.instanceSource = ont;
	}
	
	@Override
	public boolean isIterable() { 
		return true;  // since we load the entire ontology, we can iterate through the instances. 
	}

	@Override public long size() { 
		return instanceSource.getModel().listIndividuals().toList().size(); 
	}
	
	@Override
	public List<Instance> getInstances(String type, int limit) {

		List<Instance> instanceList = new ArrayList<Instance>();
		
		OntModel model = instanceSource.getModel();
		Resource ontClass = model.getResource(type);
		
		if( ontClass == null ) 
			return instanceList; // we cannot find a resource with this uri, return the empty list
		
		// The listing of individuals in an ontology.
		Iterator<Individual> individuals = instanceSource.getModel().listIndividuals(ontClass);
		
		for( int i = 0; individuals.hasNext() && i < limit; i++ ) {
			Individual currentIndividual = individuals.next();
			instanceList.add(new Instance(currentIndividual));
		}
		
		return instanceList;
	}

	
	@Override
	public List<Instance> getCandidateInstances(String searchTerm, String type) throws AMException {
		throw new AMException("This method is not implemented.");
	}

	@Override
	public Iterator<Instance> getInstances() {
		
		Ontology ontology = (Ontology) instanceSource;
		Iterator<Individual> individuals = ontology.getModel().listIndividuals();
		return new IndividualInstanceIterator(individuals);		
	}

	@Override
	public Instance getInstance(String uri) throws AMException {

		Individual individual = instanceSource.getModel().getIndividual(uri);

		if (individual == null)
			throw new AMException(
					"No instance with that URI was found in the ontology.");

		Instance instance = new Instance(individual);

		return instance;
	}

}
