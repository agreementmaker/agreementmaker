package am.app.mappingEngine.LinkedOpenData;

import java.io.File;

import org.apache.log4j.Logger;

import am.app.ontology.Ontology;

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
		Ontology ontology = new Ontology();
		FileManager manager =FileManager.get();
		
		if(mapper != null)
			manager.setLocationMapper(mapper);
		
		log.debug("Loading model...");
		System.out.println(filename);
		Model basemodel = manager.loadModel(filename);
		log.debug("Creating ontModel...");
		OntModel model = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM, basemodel );
		
		ontology.setModel(model);
		
		try {//if we can't access the namespace of the ontology we can't skip nodes with others namespaces
			String ns = model.getNsPrefixMap().get("").toString();
			ontology.setURI(ns);
		}
		catch(Exception e) {
			ontology.setURI("");
		}
				
		return ontology;
	}

}
