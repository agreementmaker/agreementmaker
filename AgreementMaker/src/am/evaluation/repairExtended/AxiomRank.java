package am.evaluation.repairExtended;

import java.util.ArrayList;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;

public class AxiomRank {

	//private Integer AxiomId;
	private OWLAxiom Axiom;
	private Integer Rank;
	private Double Confidence;
	
	//public AxiomRank(OWLAxiom axiom, Integer rank, Integer axiomId){
	public AxiomRank(OWLAxiom axiom, Integer rank){
		
		//AxiomId = axiomId;
		Axiom = axiom;
		Rank = rank;
		//Confidence = confidence;
	}
	
	//get set	
	/*public void setAxiomId (Integer axiomId)
    {
    	AxiomId = axiomId;           
    }
    public Integer getAxiomId()
    {
        return AxiomId;
    }*/
	
	public ArrayList<OWLClass> getAxiomClasses()
	{
		ArrayList<OWLClass> axiomClasses = new ArrayList<OWLClass>();
		axiomClasses.addAll(Axiom.getClassesInSignature());
		return axiomClasses;
	}
    
	public void setAxiom (OWLAxiom axiom)
    {
    	Axiom = axiom;           
    }
    public OWLAxiom getAxiom()
    {
        return Axiom;
    }

    public void setRank (Integer rank)
    {
    	Rank = rank;           
    }
    public Integer getRank()
    {
        return Rank;
    }
    
    public void setConfidence (Double confidence)
    {
    	Confidence = confidence;           
    }
    public Double getConfidence()
    {
        return Confidence;
    }
}
