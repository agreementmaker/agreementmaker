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

	private OWLAxiom Axiom;
	private Integer Rank;
	
	public AxiomRank(OWLAxiom axiom, Integer rank){
		Axiom = axiom;
		Rank = rank;		
	}
	
	//Get ArrayList of all OWLClass(s) in axiom signature
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
}
