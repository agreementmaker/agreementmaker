package am.evaluation.repairExtended;

import java.util.ArrayList;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;

/**
 * @author Pavan
 *
 *	AxiomRank class is used to hold the axiom and its corresponding rank, confidence level.
 */
public class AxiomRank {

	//private Integer AxiomId;
	private OWLAxiom Axiom;
	private Integer Rank;
	//private Double Confidence;
	
	public AxiomRank(OWLAxiom axiom, Integer rank){
		Axiom = axiom;
		Rank = rank;		
	}
	
	/*public AxiomRank(OWLAxiom axiom, Integer rank, Integer axiomId){
		AxiomId = axiomId;
		Confidence = confidence;
	}*/	
	
	//Getters, setters	
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
    
    /*public void setConfidence (Double confidence)
    {
    	Confidence = confidence;           
    }
    public Double getConfidence()
    {
        return Confidence;
    }*/
}
