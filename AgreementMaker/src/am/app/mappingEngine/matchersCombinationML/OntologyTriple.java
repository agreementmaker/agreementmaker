package am.app.mappingEngine.matchersCombinationML;

import am.app.mappingEngine.Alignment;
import am.app.ontology.Ontology;

public class OntologyTriple {
	
	private Ontology ontology1;
	private Ontology ontology2;
	private Alignment referenceAlignment;
	
	
	public OntologyTriple(Ontology ontology1, Ontology ontology2,
			Alignment referenceAlignment) {
		super();
		this.ontology1 = ontology1;
		this.ontology2 = ontology2;
		this.referenceAlignment = referenceAlignment;
	}


	public Ontology getOntology1() {
		return ontology1;
	}


	public void setOntology1(Ontology ontology1) {
		this.ontology1 = ontology1;
	}


	public Ontology getOntology2() {
		return ontology2;
	}


	public void setOntology2(Ontology ontology2) {
		this.ontology2 = ontology2;
	}


	public Alignment getReferenceAlignment() {
		return referenceAlignment;
	}


	public void setReferenceAlignment(Alignment referenceAlignment) {
		this.referenceAlignment = referenceAlignment;
	}
	
	
	
	

}
