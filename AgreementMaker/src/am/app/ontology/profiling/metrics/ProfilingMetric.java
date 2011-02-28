package am.app.ontology.profiling.metrics;

import am.app.ontology.Ontology;

public abstract class ProfilingMetric {

	protected Ontology ontology;
	
	public ProfilingMetric(Ontology o) {
		this.ontology = o;
	}
	
	public abstract void runMetric();

}
