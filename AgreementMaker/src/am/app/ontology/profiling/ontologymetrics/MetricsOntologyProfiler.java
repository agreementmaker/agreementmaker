package am.app.ontology.profiling.ontologymetrics;

import java.util.Iterator;

import am.app.ontology.Node;
import am.app.ontology.Ontology;
import am.app.ontology.profiling.MatcherStack;
import am.app.ontology.profiling.OntologyProfiler;
import am.app.ontology.profiling.OntologyProfilerPanel;
import am.app.ontology.profiling.OntologyProfilerParameters;
import am.app.ontology.profiling.ProfilerRegistry;
import am.utility.Pair;


public class MetricsOntologyProfiler implements OntologyProfiler{

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
	public OntologyProfilerPanel getProfilerPanel(boolean initial) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean needsParams(boolean initial) {
		return false;
	}

	@Override
	public void setInitialParams(OntologyProfilerParameters param) { }

	@Override
	public void setMatchTimeParams(OntologyProfilerParameters param) { }

	@Override
	public void setName(ProfilerRegistry name) {
		this.name = name;
	}

	@Override
	public MatcherStack getMatcherStack() {
		// TODO Auto-generated method stub
		return null;
	}
}
