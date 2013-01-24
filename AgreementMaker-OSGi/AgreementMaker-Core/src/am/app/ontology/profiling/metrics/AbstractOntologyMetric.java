package am.app.ontology.profiling.metrics;

import am.app.ontology.Ontology;

public abstract class AbstractOntologyMetric implements OntologyMetric {

	protected Ontology ontology;
	
	public AbstractOntologyMetric(Ontology o) {
		this.ontology = o;
	}

}
