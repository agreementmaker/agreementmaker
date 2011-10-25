package am.app.ontology.profiling.manual;

import java.util.List;

import com.hp.hpl.jena.rdf.model.Property;

import am.app.ontology.profiling.OntologyProfilerParameters;

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
