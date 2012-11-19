package am.evaluation.repairExtended;

import org.semanticweb.owlapi.model.OWLAxiom;

public class AxiomRank {

	private OWLAxiom Axiom;
	private Integer Rank;
	
	public AxiomRank(OWLAxiom axiom, Integer rank){
		
		Axiom = axiom;
		Rank = rank;
	}
	
	//get set
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
