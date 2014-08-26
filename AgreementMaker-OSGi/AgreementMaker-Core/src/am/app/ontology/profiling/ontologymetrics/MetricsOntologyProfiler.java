package am.app.ontology.profiling.ontologymetrics;

import java.util.Iterator;
import java.util.List;

import am.app.ontology.Node;
import am.app.ontology.Ontology;
import am.app.ontology.profiling.MatcherStack;
import am.app.ontology.profiling.OntologyProfiler;
import am.app.ontology.profiling.OntologyProfilerPanel;
import am.app.ontology.profiling.OntologyProfilerParameters;
import am.app.ontology.profiling.ProfilerRegistry;
import am.utility.Pair;

/**
 * Work in progress.  Not done yet. -- Cosmin Sept 14, 2011.
 *
 */
public class MetricsOntologyProfiler implements OntologyProfiler {

	private ProfilerRegistry name;
	
	private Ontology sourceOntology;
	private Ontology targetOntology;
	
	OntologyMetrics sourceMetrics;
	OntologyMetrics targetMetrics;
	
	public MetricsOntologyProfiler(Ontology sourceOntology, Ontology targetOntology){
		this.sourceOntology = sourceOntology;
		this.targetOntology = targetOntology;
		
		OntologyEvaluation evaluation = new OntologyEvaluation(sourceOntology,targetOntology);
		sourceMetrics = evaluation.evaluateOntology(sourceOntology);
		targetMetrics = evaluation.evaluateOntology(targetOntology);
		
		System.out.println(sourceMetrics);
		System.out.println(targetMetrics);
	}
	
	@Override
	public Iterator<Pair<String, String>> getAnnotationIterator(Node n1, Node n2) {
		return null;
	}

	@Override
	public ProfilerRegistry getName() {
		return name;
	}

	@Override
	public OntologyProfilerPanel getProfilerPanel(ParamType type) {
		throw new RuntimeException("This profiling algorithm does not provide a parameters panel for " + type.name());
	}

	@Override
	public boolean needsParams(ParamType type) {
		return false; // needs no configuration
	}

	@Override
	public void setParams(ParamType type, OntologyProfilerParameters param) {
		throw new AssertionError("This algorithm does not need any parameters.");
	}
	
	@Override
	public void setName(ProfilerRegistry name) {
		this.name = name;
	}

	@Override
	public MatcherStack getMatcherStack() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getAnnotations(Node node) {
		// TODO Auto-generated method stub
		return null;
	}
}
