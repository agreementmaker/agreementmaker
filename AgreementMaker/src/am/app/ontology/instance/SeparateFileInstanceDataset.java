package am.app.ontology.instance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import am.AMException;
import am.app.mappingEngine.instance.EntityTypeMapper;
import am.utility.URIConstants;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

/**
 * An instance dataset that was loaded from a separate file.
 */
public class SeparateFileInstanceDataset implements InstanceDataset {

	private OntModel instanceSource;
	
	private HashMap<String, Instance> instancesByURI;
	
	/**
	 * FIXME: These string values are duplicated from the NYTConstants class in
	 * the LinkedOpenData bundle. They should be removed from here.
	 */
	private static final String hasArticleURI = "http://data.nytimes.com/elements/hasArticle";
	private static final String orgKeywordsURI = "http://data.nytimes.com/elements/organizationKeywords";
	private static final String peopleKeywordsURI = "http://data.nytimes.com/elements/peopleKeywords";
	private static final String desKeywordsURI = "http://data.nytimes.com/elements/descriptionKeywords";
	private static final String variantsNumberURI = "http://data.nytimes.com/elements/number_of_variants";
	private static final String titleURI = "http://data.nytimes.com/elements/title";
	
	/** FIXME: Remove the specific properties in this general class. */
	private String[][] propertiesWhiteList = { 
									  {hasArticleURI, "article", "m"},
									  {orgKeywordsURI, "organizationKeywords", "sep"},
									  {peopleKeywordsURI, "peopleKeywords", "sep"},
									  {desKeywordsURI, "descriptionKeywords", "sep"},
									  {variantsNumberURI, "variantsNumber", "s"},
									  {titleURI, "title", "s"},
									};
	
	public SeparateFileInstanceDataset(OntModel model) {
		instanceSource = model;
	}
	
	@Override
	public boolean isIterable() {
		return true; // yes, we can iterate through the instances
	}

	@Override public long size() { return instanceSource.size(); }
	
	@Override
	public List<Instance> getInstances(String type, int limit)
			throws AMException {
		throw new AMException("Not implemented");
	}

	@Override
	public List<Instance> getCandidateInstances(String searchTerm, String type)
			throws AMException {
		throw new AMException("This method is not implemented.");
	}

	/**
	 * This is a bit of voodoo magic. -- Cosmin.
	 * 
	 * @author Federico Caimi
	 */
	@Override
	public Iterator<Instance> getInstances() throws AMException {
		List<Instance> instanceList = new ArrayList<Instance>();
		instancesByURI = new HashMap<String, Instance>();
	
		OntModel model = (OntModel) instanceSource;
		List<Statement> statements = getIndividualsStatements(model, null);
		
		String uri;
		String label;
		String comment;
		Instance instance;
		for(Statement statement: statements){
			uri = statement.getSubject().getURI();
			
			label = getPropertyValue(model, uri, RDFS.label.getURI());
			if(label.isEmpty()) label = getPropertyValue(model, uri, URIConstants.SKOS_PREFLABEL);
			
			comment = getPropertyValue(model, uri, RDFS.comment.getURI());
			if(label.isEmpty()) comment = getPropertyValue(model, uri, URIConstants.SKOS_DEFINITION);
			
			String type = getPropertyValue(model, uri, RDF.type.getURI());
			
			instance = new Instance(uri, EntityTypeMapper.getEnumEntityType(type));	
			
			instance.setStatements(model.listStatements(model.getResource(uri), (Property) null, (RDFNode) null).toList());
						
			instanceList.add(instance);
			instancesByURI.put(instance.getUri(), instance);
			
			if(!label.isEmpty()) instance.setProperty("label", label);
			if(!comment.isEmpty()) instance.setProperty("comment", comment);
			
			String[] uriLabelPair;
			String value;
			for (int i = 0; i < propertiesWhiteList.length; i++) {
				uriLabelPair = propertiesWhiteList[i];
				
				if(uriLabelPair[2] == "m"){
					Set<String> values = getPropertyMultiValue(model, uri, uriLabelPair[0]);
					instance.setProperty(uriLabelPair[1], values);
				}
				else{
					value = getPropertyValue(model, uri, uriLabelPair[0]);
					if(!value.isEmpty()){
						if(uriLabelPair[2].equals("s")) instance.setProperty(uriLabelPair[1], value);
						if(uriLabelPair[2].equals("sep")){
							String[] values = value.split("\\|");
							
							
							Set<String> strings = new HashSet<String>();
							for (int j = 0; j < values.length; j++) {
								strings.add(values[j]);
							}
							
							instance.setProperty(uriLabelPair[1], strings);
						}
					}
				}
				
			}
		}
		
		return instanceList.iterator();
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
	
	public static Set<String> getPropertyMultiValue(OntModel model, String instanceURI, String propertyURI){
		Property property = model.getProperty(propertyURI);
		if(property == null) model.createProperty(propertyURI);
		Resource instance = model.createResource(instanceURI);
		List<Statement> list = model.listStatements(instance, property, (RDFNode)null).toList();
		Set<String> ret = new HashSet<String>();
		
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
