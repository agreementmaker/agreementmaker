package am.app.ontology.profiling.manual;

import java.util.List;

import com.hp.hpl.jena.rdf.model.Property;

import am.app.ontology.profiling.OntologyProfilerParameters;

public class ManualProfilerMatchingParameters extends OntologyProfilerParameters {

	boolean matchSourceClassLocalname = true;
	boolean matchTargetClassLocalname = true;
	
	boolean matchSourcePropertyLocalname = true;
	boolean matchTargetPropertyLocalname = true;
	
	List<Property> sourceClassAnnotations;
	List<Property> targetClassAnnotations;
	
	List<Property> sourcePropertyAnnotations;
	List<Property> targetPropertyAnnotations;
	
}
