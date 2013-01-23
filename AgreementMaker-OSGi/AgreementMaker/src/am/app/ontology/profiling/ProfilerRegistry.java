package am.app.ontology.profiling;

import am.app.ontology.profiling.manual.ManualOntologyProfiler;
import am.app.ontology.profiling.ontologymetrics.MetricsOntologyProfiler;


/**
 * A registry to keep track of ontology profiling algorithms.
 * 
 * @author cosmin
 *
 */
public enum ProfilerRegistry {

	ManualProfiler	("Manual Ontology Profiler", ManualOntologyProfiler.class),
	MetricsProfiler	("Metrics Ontology Profiler", MetricsOntologyProfiler.class);
	
	private String name;
	private Class<? extends OntologyProfiler> className;
	
	/* Constructor */
	ProfilerRegistry( String n, Class<? extends OntologyProfiler> matcherClass ) { 
		name = n; className = matcherClass;
	}
	
	/* Getters and Setters */
	public String getProfilerName() { return name; }
	public Class<? extends OntologyProfiler> getProfilerClass() { return className; }
	
	@Override
	public String toString() {
		return name;
	}
}
