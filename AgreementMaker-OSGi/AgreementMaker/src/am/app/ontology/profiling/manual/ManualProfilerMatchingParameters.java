package am.app.ontology.profiling.manual;

import java.util.List;

import am.app.ontology.profiling.OntologyProfilerParameters;

import com.hp.hpl.jena.rdf.model.Property;

public class ManualProfilerMatchingParameters extends OntologyProfilerParameters {

	public boolean matchSourceClassLocalname = true;
	public boolean matchTargetClassLocalname = true;
	
	public boolean matchSourcePropertyLocalname = true;
	public boolean matchTargetPropertyLocalname = true;
	
	public List<Property> sourceClassAnnotations;
	public List<Property> targetClassAnnotations;
	
	public List<Property> sourcePropertyAnnotations;
	public List<Property> targetPropertyAnnotations;
	
}
