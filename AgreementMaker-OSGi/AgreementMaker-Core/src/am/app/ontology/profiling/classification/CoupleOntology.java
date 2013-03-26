package am.app.ontology.profiling.classification;

import am.app.ontology.Ontology;
import am.app.ontology.profiling.ontologymetrics.CoupleOntologyMetrics;
import am.app.ontology.profiling.ontologymetrics.OntologyEvaluation;
import am.app.ontology.profiling.ontologymetrics.OntologyMetrics;

public class CoupleOntology {
	private Ontology onto1;
	private Ontology onto2;
	
	public CoupleOntology(Ontology onto1, Ontology onto2){
		this.onto1 = onto1;
		this.onto2 = onto2;
	}
	
	public CoupleOntologyMetrics getAllMetrics(){
		OntologyEvaluation e1 = new OntologyEvaluation();
		OntologyEvaluation e2 = new OntologyEvaluation();
		OntologyMetrics om1 = e1.evaluateOntology(onto1);
		OntologyMetrics om2 = e2.evaluateOntology(onto2);
		return new CoupleOntologyMetrics(om1, om2);
	}
	
	public Ontology getOnto1() {
		return onto1;
	}
	public void setOnto1(Ontology onto1) {
		this.onto1 = onto1;
	}
	public Ontology getOnto2() {
		return onto2;
	}
	public void setOnto2(Ontology onto2) {
		this.onto2 = onto2;
	}
	
	
	
	
	
	
}
