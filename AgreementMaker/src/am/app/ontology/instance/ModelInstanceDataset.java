package am.app.ontology.instance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import am.AMException;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.QuerySolutionMap;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.RDF;

/**
 * 
 * 
 * @author federico
 *
 */
public class ModelInstanceDataset implements InstanceDataset{
	private Model instanceSource;
	private double luceneScoreThreshold = 0.6;
	private HashMap<String, Instance> instancesByURI;
	String indexProperty;
	Logger log = Logger.getLogger(ModelInstanceDataset.class);

	public ModelInstanceDataset(Model model, String indexProperty) {
		instanceSource = model;
		this.indexProperty = indexProperty;
	}

	@Override
	public boolean isIterable() {
		return true; // yes, we can iterate through the instances
	}

	@Override
	public List<Instance> getInstances(String type, int limit)
			throws AMException {
		throw new AMException("Not implemented");
	}

	@Override
	public List<Instance> getCandidateInstances(String searchTerm, String type)
			throws AMException {
		
		Set<String> URIs = new HashSet<String>(); 
		
		List<Instance> instances = new ArrayList<Instance>();
		
		int limit = 100;

		//TODO Figure out if we have to manage the case indexProp is null
		String queryString = "\nPREFIX pf:     <http://jena.hpl.hp.com/ARQ/property#>" +
				"\nSELECT ?s ?score{" +
				"\n (?lit ?score) pf:textMatch '\"" + searchTerm + "\"' ." +
				"\n	?s ?property ?lit." +
				"\n  FILTER (?score > " + luceneScoreThreshold +")" +
				"\n } ORDER BY DESC(?score) LIMIT " + limit;

		QuerySolutionMap map = new QuerySolutionMap();
		Property prop = instanceSource.getProperty(indexProperty);
		if(prop == null) instanceSource.createProperty(indexProperty);
		map.add("property", prop);

		ResultSet results = SPARQLUtils.sparqlQuery(instanceSource, queryString, map);

		if(!results.hasNext()) log.info("No results");

		while(results.hasNext()){
			QuerySolution soln = results.next();
			RDFNode node = soln.get("s");
			if(node == null) continue;
			URIs.add(node.toString());
		}

		Iterator<String> it = URIs.iterator();
		String uri;
		while(it.hasNext()){
			uri = it.next();

			//log.info(uri);			
						
			Resource individual = instanceSource.getResource(uri);
			List<Statement> stmts = instanceSource.listStatements(individual, (Property) null, (RDFNode) null).toList();
			
			if(individual == null) continue;

			//TODO give the individual a type
			Instance instance = new Instance(individual.getURI(), null);
			instance.setStatements(stmts);
			
			instances.add(instance);

		}	
		return instances;
	}

	@Override
	public List<Instance> getInstances() throws AMException {
		return null;
	}

	@Override
	public Instance getInstance(String uri) throws AMException {
		//if(instancesByURI == null) getInstances();
		return instancesByURI.get(uri);
	}

	/**
	 * @param type Can be null.
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

	public static String getPropertyValue(Model model, String instanceURI, String propertyURI){
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

	public static List<String> getPropertyMultiValue(OntModel model, String instanceURI, String propertyURI){
		Property property = model.getProperty(propertyURI);
		if(property == null) model.createProperty(propertyURI);
		Resource instance = model.createResource(instanceURI);
		List<Statement> list = model.listStatements(instance, property, (RDFNode)null).toList();
		ArrayList<String> ret = new ArrayList<String>();

		if(list.size() >= 1){
			for (int i = 0; i < list.size(); i++) {
				RDFNode object = list.get(i).getObject();
				if(object.isLiteral()) ret.add(object.asLiteral().getString());
				else if(object.isResource()) ret.add(object.asResource().getURI());
			}	
		}	
		return ret;
	}

}
