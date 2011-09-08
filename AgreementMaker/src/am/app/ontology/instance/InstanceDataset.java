package am.app.ontology.instance;

import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

import am.AMException;
import am.app.ontology.Ontology;
import am.app.ontology.instance.endpoint.SemanticWebEndpoint;
import am.utility.URIConstants;

public class InstanceDataset {

	public enum DatasetType {
		ENDPOINT,
		ONTOLOGY,
		DATASET;
	}
	
	private Object instanceSource;
	private DatasetType datasetType;
	
	public InstanceDataset( SemanticWebEndpoint endpoint ) {
		instanceSource = endpoint;
		datasetType = DatasetType.ENDPOINT;
	}
	
	public List<Instance> getInstances( String type, int limit ) {
		List<Instance> instanceList = new ArrayList<Instance>();
		
		try {
			switch( datasetType ) {
			case ENDPOINT: {
				SemanticWebEndpoint endpoint = (SemanticWebEndpoint) instanceSource;
				instanceList = endpoint.listInstances( type, limit );
			}
			
			case ONTOLOGY: {
				// TODO: Implement the listing of individuals in an ontology.
				Ontology ontology = (Ontology) instanceSource;
				List<Individual> individuals = ontology.getModel().listIndividuals().toList();
				
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
					
					//using individual.getPropertyValue();
					node = individual.getPropertyValue(RDFS.label);
					if(node != null) label = node.asLiteral().toString(); 
					
					if(!label.isEmpty()){
						ArrayList<String> list = new ArrayList<String>();
						list.add(label);
						instance.setProperty("label", list);
					}
					
					instanceList.add(instance);

				}
			}
			
			case DATASET: {
				// TODO: Implement the listing of individuals in a dataset.
				
				OntModel model = (OntModel) instanceSource;
				List<Statement> statements = getIndividualsStatements(model, type);
				
				String uri;
				String label;
				RDFNode node;
				Instance instance;
				for(Statement statement: statements){
					uri = statement.getSubject().getURI();
					
					label = getPropertyValue(model, uri, RDFS.label.getURI());
					if(label.isEmpty()) label = getPropertyValue(model, uri, URIConstants.SKOS_PREFLABEL);
					
					instance = new Instance(uri, type);	
					instanceList.add(instance);
				}
			}
				
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		return instanceList;
	}
	
	public List<Instance> getCandidateInstances( String searchTerm, String type) {
		List<Instance> instanceList = new ArrayList<Instance>();
		
		try {
			switch( datasetType ) {
			case ENDPOINT: {
				SemanticWebEndpoint endpoint = (SemanticWebEndpoint) instanceSource;
				return endpoint.freeTextQuery(searchTerm, type);
			}
			
			case ONTOLOGY: {
				// TODO: Implement the listing of individuals in an ontology.
				throw new AMException("This method is not implemented.");
			}
			
			case DATASET: {
				// TODO: Implement the listing of individuals in a dataset.
				throw new AMException("This method is not implemented.");
			}
				
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		return instanceList;
	}


	public List<Instance> getInstances() {
		List<Instance> instanceList = new ArrayList<Instance>();
		
		try {
			switch( datasetType ) {
			case ENDPOINT: {
				throw new AMException("This functionality is not available for an endpoint.");
			}
			
			case ONTOLOGY: {
				Ontology ontology = (Ontology) instanceSource;
				List<Individual> individuals = ontology.getModel().listIndividuals().toList();
				
				Instance instance;
				String uri;
				//String label;
				String rdfType = null;
				
				for(Individual individual: individuals){
					uri = individual.getURI();
					
					RDFNode node = individual.getPropertyValue(RDF.type);
					if(node != null)
						rdfType = node.asLiteral().getString();
					
					instance = new Instance(uri, rdfType);
					
					//TODO get labels and other properties
					//using individual.getPropertyValue();
					
					instanceList.add(instance);

				}	
			}
			
			case DATASET: {
				OntModel model = (OntModel) instanceSource;
				List<Statement> statements = getIndividualsStatements(model, null);
				
				String uri;
				String label;
				String type;
				RDFNode node;
				Instance instance;
				for(Statement statement: statements){
					uri = statement.getSubject().getURI();
					
					label = getPropertyValue(model, uri, RDFS.label.getURI());
					if(label.isEmpty()) label = getPropertyValue(model, uri, URIConstants.SKOS_PREFLABEL);
					
					type = getPropertyValue(model, uri, RDF.type.getURI());
					
					instance = new Instance(uri, type);	
					
					instanceList.add(instance);
					
				}
				
			}	
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		return instanceList;
	}
	
	public boolean isIterable() {
		return datasetType == DatasetType.DATASET || datasetType == DatasetType.ONTOLOGY;
	}
	
	/**
	 * type can be null
	 * 
	 * @param model
	 * @param type
	 * @return
	 */
	public static List<Statement> getIndividualsStatements(OntModel model, String type){
		if(type != null){
			Property property = model.getProperty(type);
			if(property == null) model.createProperty(type);
			List<Statement> list = model.listStatements(null, RDF.type, property).toList();
			return list;
		}
		return model.listStatements(null, RDF.type, (RDFNode)null).toList();
	}
	
	public static String getPropertyValue(OntModel model, String instanceURI, String propertyURI){
		Property property = model.getProperty(propertyURI);
		if(property == null) model.createProperty(propertyURI);
		Resource instance = model.createResource(instanceURI);
		List<Statement> list = model.listStatements(instance, property, (RDFNode)null).toList();
		
		if(list.size() == 1){
			RDFNode object = list.get(0).getObject();
			if(object.isLiteral()) return object.asLiteral().getString();
			else if(object.isResource()) return object.asResource().getURI();	
		}	
		return "";
	}
}
