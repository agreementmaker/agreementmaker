package am.app.mappingEngine.matchersCombinationML;

import java.util.ArrayList;
import java.util.HashMap;

import sun.security.x509.AlgIdDSA;

import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.Mapping;
import am.app.ontology.Ontology;

public class OntologyTriple {
	
	private Ontology ontology1;
	private Ontology ontology2;
	private Alignment<Mapping> referenceAlignment;
	private ArrayList<AbstractMatcher> listOfMatchers;
	private HashMap<AbstractMatcher, Alignment<Mapping>> alignmentObtained;
	//TODO check if we need to store Alignment<Mapping> or the whole result class???
	
	
	
	public Alignment<Mapping> getAlignmentObtained(AbstractMatcher currentMatcher)
	{
	
		return alignmentObtained.get(currentMatcher);
	}
	
	public void setAlignmentObtained(AbstractMatcher currentMatcher, Alignment<Mapping> resultObtained)
	{
		alignmentObtained.put(currentMatcher, resultObtained);
	}
	
	public ArrayList<AbstractMatcher> getListOfMatchers() {
		return listOfMatchers;
	}


	public void setListOfMatchers(ArrayList<AbstractMatcher> listOfMatchers) {
		this.listOfMatchers = listOfMatchers;
	}
	
	
	
	
	public OntologyTriple(Ontology ontology1, Ontology ontology2,
			Alignment referenceAlignment) {
		super();
		this.ontology1 = ontology1;
		this.ontology2 = ontology2;
		this.referenceAlignment = referenceAlignment;
	}


	public OntologyTriple() {
		this.alignmentObtained =new HashMap<AbstractMatcher, Alignment<Mapping>>();
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


	public Alignment<Mapping> getReferenceAlignment() {
		return referenceAlignment;
	}


	public void setReferenceAlignment(Alignment<Mapping> referenceAlignment) {
		this.referenceAlignment = referenceAlignment;
	}
	

}
