package am.matcher.MachineLearning;

/**
 * Data structure to store the source, target and reference Alignment as a triple
 */
import java.util.ArrayList;
import java.util.HashMap;

import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.Mapping;
import am.app.ontology.Ontology;

public class OntologyTriple {
	
	private Ontology ontology1;
	private Ontology ontology2;
	private Alignment<Mapping> referenceAlignment;
	private ArrayList<AbstractMatcher> listOfMatchers;
	//private HashMap<AbstractMatcher, Alignment<Mapping>> alignmentObtained;
	private HashMap<String, Alignment<Mapping>> alignmentObtained;
	//TODO check if we need to store Alignment<Mapping> or the whole result class???
	
	
	public boolean containsMatcher(String currentMatcher)
	{
		return alignmentObtained.containsKey(currentMatcher);
	}
	public Alignment<Mapping> getAlignmentObtained(String currentMatcher)
	{
	
		return alignmentObtained.get(currentMatcher);
	}
	
	public void setAlignmentObtained(String currentMatcher, Alignment<Mapping> resultObtained)
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
		this.alignmentObtained =new HashMap<String, Alignment<Mapping>>();
	}

	public OntologyTriple(Ontology ontology1, Ontology ontology2
			) {
		super();
		this.ontology1 = ontology1;
		this.ontology2 = ontology2;
		
		this.alignmentObtained =new HashMap<String, Alignment<Mapping>>();
	}

	public OntologyTriple() {
		this.alignmentObtained =new HashMap<String, Alignment<Mapping>>();
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
