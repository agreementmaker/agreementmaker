package am.app.ontology.instance;

import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

import am.AMException;
import am.app.ontology.Ontology;

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

	@Override
	public List<Instance> getInstances(String type, int limit) {

		List<Instance> instanceList = new ArrayList<Instance>();
		
		// The listing of individuals in an ontology.
		List<Individual> individuals = instanceSource.getModel().listIndividuals().toList();
		
		Instance instance;
		String uri;
		String label = null;
		String rdfType = null;
		RDFNode node;
		for(Individual individual: individuals){
			uri = individual.getURI();
			
			node = individual.getPropertyValue(RDF.type);
			if(node != null)
				rdfType = node.asLiteral().getString();
			
			instance = new Instance(uri, rdfType);
			
			// get labels and other properties
			collectProperties(instance, individual);
			
			instanceList.add(instance);
		}
		
		return instanceList;
	}

	
	@Override
	public List<Instance> getCandidateInstances(String searchTerm, String type) throws AMException {
		throw new AMException("This method is not implemented.");
	}

	@Override
	public List<Instance> getInstances() {
		
		List<Instance> instanceList = new ArrayList<Instance>();
		
		Ontology ontology = (Ontology) instanceSource;
		List<Individual> individuals = ontology.getModel().listIndividuals().toList();
		
		Instance instance;
		String uri;
		//String label;
		String rdfType = null;
		
		for(Individual individual: individuals){
			uri = individual.getURI();
			
			RDFNode node = individual.getPropertyValue(RDF.type);
			if(node != null) {
				if( node.canAs(Literal.class)  ) { 
					rdfType = node.asLiteral().getString();
				} else {
					rdfType = node.toString(); // should check for a resource
				}
			}
				
			
			instance = new Instance(uri, rdfType);
			
			// get labels and other properties
			collectProperties( instance, individual );
			
			instanceList.add(instance);
		}
		
		return instanceList;
	}

	@Override
	public Instance getInstance(String uri) throws AMException {
		
		Individual individual = instanceSource.getModel().getIndividual(uri);
		
		if( individual == null ) throw new AMException("No instance with that URI was found in the ontology.");
		
		RDFNode node = individual.getPropertyValue(RDF.type);
		
		String rdfType = null;
		if(node != null) {
			if( node.canAs(Literal.class)  ) { 
				rdfType = node.asLiteral().getString();
			} else {
				rdfType = node.toString(); // should check for a resource
			}
		}
		
		Instance instance = new Instance(uri, rdfType);
		
		// get labels and other properties
		collectProperties( instance, individual );
		
		return instance;
	}
	
	
	private void collectProperties(Instance instance, Individual individual) {
		//TODO get labels and other properties
		//using individual.getPropertyValue();

		// TODO: Implement this.
		
		// Label
		RDFNode node = individual.getPropertyValue(RDFS.label);
		String label = null;
		if(node != null) label = node.asLiteral().toString(); 
		
		if(label != null && !label.isEmpty()){
			ArrayList<String> list = new ArrayList<String>();
			list.add(label);
			instance.setProperty("label", list);
		}
	}

}
