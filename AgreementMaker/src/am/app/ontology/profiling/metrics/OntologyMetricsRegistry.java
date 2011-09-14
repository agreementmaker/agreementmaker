package am.app.ontology.profiling.metrics;

import am.app.ontology.profiling.metrics.multipleinheritance.MultipleInheritanceMetric;

/**
 * Keep track of ontology metrics.
 * 
 * @author cosmin
 *
 */
public enum OntologyMetricsRegistry {

	InheritanceCount	("Inheritance Count", MultipleInheritanceMetric.class ),
	;
	
	private String name;
	private Class<? extends OntologyMetric> className;
	
	/* Constructor */
	OntologyMetricsRegistry( String n, Class<? extends OntologyMetric> matcherClass ) { 
		name = n; className = matcherClass;
	}
	
	/* Getters and Setters */
	public String getMetricName() { return name; }
	public Class<? extends OntologyMetric> getMetricClass() { return className; }
	
	@Override
	public String toString() {
		return name;
	}	
}
