package am.app.ontology.profiling.metrics;

import am.utility.numeric.AvgMinMaxNumber;


public interface OntologyMetric {

	public void runMetric();
	
	public boolean hasSingleValueResult();
	public AvgMinMaxNumber getSingleValueResult();

}
