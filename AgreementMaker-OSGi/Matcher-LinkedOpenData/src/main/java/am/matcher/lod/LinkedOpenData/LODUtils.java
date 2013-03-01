package am.matcher.lod.LinkedOpenData;

import java.util.List;

import org.apache.log4j.Logger;

import am.app.ontology.Node;
import am.app.ontology.Ontology;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.util.LocationMapper;

public class LODUtils {
	private static LocationMapper mapper;	
	
	public static Ontology openOntology(String filename){
		return openOntology(filename, null);
	}
	
	public static LocationMapper getLocationMapper(){
		if(mapper == null){
			mapper = new LocationMapper();
			//mapper.addAltEntry("data.semanticweb.org/ns/swc/swrc",new File("LocationMappings/ns.swc.swrc.rdf").getPath());
			return mapper;
		}
		else return mapper;
	}
	
	public static Ontology openOntology(String filename, LocationMapper mapper){
		Logger log = Logger.getLogger(LODUtils.class);
		Ontology ontology = null;
		FileManager manager =FileManager.get();
		
		if(mapper != null)
			manager.setLocationMapper(mapper);
		
		log.debug("Loading model...");
		System.out.println(filename);
		Model basemodel = manager.loadModel(filename);
		log.debug("Creating ontModel...");
		OntModel model = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM, basemodel );
		
		ontology = new Ontology(model);
		
		try {//if we can't access the namespace of the ontology we can't skip nodes with others namespaces
			String ns = model.getNsPrefixMap().get("").toString();
			ontology.setURI(ns);
		}
		catch(Exception e) {
			ontology.setURI("");
		}
				
		return ontology;
	}
	
	public static String superclassesString(Node node){
		String retValue = "[";
		OntClass ontClass = null;
		if(node.getResource().canAs(OntClass.class))
			ontClass = node.getResource().as(OntClass.class);
		else{
			System.err.println("Error: node cannot be converted to ontClass");
			return "";
		}
		List<OntClass> superClasses = ontClass.listSuperClasses().toList();
		int size = superClasses.size();
		OntClass superClass;
		String localName;
		for (int i = 0; i < size; i++) {
			superClass = superClasses.get(i);
			localName = superClass.getLocalName();
			if(localName != null){
				retValue += localName;
				if(i != size - 1) retValue += ",";
			}
		}
		retValue += "]";
		return retValue;
	}

}
