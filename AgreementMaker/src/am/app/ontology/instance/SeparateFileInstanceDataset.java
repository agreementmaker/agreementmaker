package am.app.ontology.instance;

import java.util.ArrayList;
import java.util.List;

import am.AMException;
import am.app.mappingEngine.instanceMatcher.NYTConstants;
import am.utility.URIConstants;

import com.hp.hpl.jena.ontology.OntModel;
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
	
	public SeparateFileInstanceDataset(OntModel model) {
		instanceSource = model;
	}
	
	@Override
	public boolean isIterable() {
		return true; // yes, we can iterate through the instances
	}

	@Override
	public List<Instance> getInstances(String type, int limit)
			throws AMException {
		List<Instance> instanceList = new ArrayList<Instance>();
		
		OntModel model = (OntModel) instanceSource;
		List<Statement> statements = getIndividualsStatements(model, type);
		
		String uri;
		String label;
		String comment;
		String article;
		Instance instance;
		String orgKeywords;
		String peopleKeywords;
		String desKeywords;
		for(Statement statement: statements){
			uri = statement.getSubject().getURI();
			
			label = getPropertyValue(model, uri, RDFS.label.getURI());
			if(label.isEmpty()) label = getPropertyValue(model, uri, URIConstants.SKOS_PREFLABEL);
			
			comment = getPropertyValue(model, uri, RDFS.comment.getURI());
			if(label.isEmpty()) comment = getPropertyValue(model, uri, URIConstants.SKOS_DEFINITION);
			
			article = getPropertyValue(model, uri, NYTConstants.hasArticleURI);
			orgKeywords = getPropertyValue(model, uri, NYTConstants.orgKeywordsURI);
			peopleKeywords = getPropertyValue(model, uri, NYTConstants.peopleKeywordsURI);
			desKeywords = getPropertyValue(model, uri, NYTConstants.desKeywordsURI);
								
			instance = new Instance(uri, type);	
			instanceList.add(instance);
			if(!label.isEmpty()) instance.setProperty("label", label);
			if(!comment.isEmpty()) instance.setProperty("comment", comment);
			if(!article.isEmpty()) instance.setProperty("article", article);
			if(!orgKeywords.isEmpty()) instance.setProperty("organizationKeywords", orgKeywords);
			if(!peopleKeywords.isEmpty()) instance.setProperty("peopleKeywords", peopleKeywords);
			if(!desKeywords.isEmpty()) instance.setProperty("descriptionKeywords", orgKeywords);
			
		}
		
		return instanceList;
	}

	@Override
	public List<Instance> getCandidateInstances(String searchTerm, String type)
			throws AMException {
		throw new AMException("This method is not implemented.");
	}

	@Override
	public List<Instance> getInstances() throws AMException {
		List<Instance> instanceList = new ArrayList<Instance>();
		
		OntModel model = (OntModel) instanceSource;
		List<Statement> statements = getIndividualsStatements(model, null);

		String uri;
		String label;
		String comment;
		List<String> article;
		Instance instance;
		String orgKeywords;
		String peopleKeywords;
		String desKeywords;
		String type = null;
		for(Statement statement: statements){
			uri = statement.getSubject().getURI();

			label = getPropertyValue(model, uri, RDFS.label.getURI());
			if(label.isEmpty()) label = getPropertyValue(model, uri, URIConstants.SKOS_PREFLABEL);

			comment = getPropertyValue(model, uri, RDFS.comment.getURI());
			if(label.isEmpty()) comment = getPropertyValue(model, uri, URIConstants.SKOS_DEFINITION);

			type = getPropertyValue(model, uri, RDF.type.getURI());

			article = getPropertyMultiValue(model, uri, NYTConstants.hasArticleURI);
			orgKeywords = getPropertyValue(model, uri, NYTConstants.orgKeywordsURI);
			peopleKeywords = getPropertyValue(model, uri, NYTConstants.peopleKeywordsURI);
			desKeywords = getPropertyValue(model, uri, NYTConstants.desKeywordsURI);

			instance = new Instance(uri, type);	
			instanceList.add(instance);
			if(!label.isEmpty()) instance.setProperty("label", label);
			if(!type.isEmpty()) instance.setProperty("label", label);
			if(!comment.isEmpty()) instance.setProperty("comment", comment);
			if(article.size() > 0) instance.setProperty("article", article);
			if(!orgKeywords.isEmpty()) instance.setProperty("organizationKeywords", orgKeywords);
			if(!peopleKeywords.isEmpty()) instance.setProperty("peopleKeywords", peopleKeywords);
			if(!desKeywords.isEmpty()) instance.setProperty("descriptionKeywords", orgKeywords);

		}
		
		return instanceList;
	}
	
	@Override
	public Instance getInstance(String uri) throws AMException {
		
		OntModel model = instanceSource;
		
		String label = getPropertyValue(model, uri, RDFS.label.getURI());
		if(label.isEmpty()) label = getPropertyValue(model, uri, URIConstants.SKOS_PREFLABEL);

		String comment = getPropertyValue(model, uri, RDFS.comment.getURI());
		if(label.isEmpty()) comment = getPropertyValue(model, uri, URIConstants.SKOS_DEFINITION);

		String type = getPropertyValue(model, uri, RDF.type.getURI());

		List<String> article = getPropertyMultiValue(model, uri, NYTConstants.hasArticleURI);
		String orgKeywords = getPropertyValue(model, uri, NYTConstants.orgKeywordsURI);
		String peopleKeywords = getPropertyValue(model, uri, NYTConstants.peopleKeywordsURI);
		String desKeywords = getPropertyValue(model, uri, NYTConstants.desKeywordsURI);

		Instance instance = new Instance(uri, type);	

		if(!label.isEmpty()) instance.setProperty("label", label);
		if(!type.isEmpty()) instance.setProperty("label", label);
		if(!comment.isEmpty()) instance.setProperty("comment", comment);
		if(article.size() > 0) instance.setProperty("article", article);
		if(!orgKeywords.isEmpty()) instance.setProperty("organizationKeywords", orgKeywords);
		if(!peopleKeywords.isEmpty()) instance.setProperty("peopleKeywords", peopleKeywords);
		if(!desKeywords.isEmpty()) instance.setProperty("descriptionKeywords", orgKeywords);
		
		return instance;

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
