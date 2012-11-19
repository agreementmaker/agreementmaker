package am.evaluation.repairExtended;

import java.util.ArrayList;

import org.semanticweb.owlapi.model.OWLClass;

public class ConflictSet {

	private Integer ClassId = 0;
	private OWLClass InconsistentClass;
	private ArrayList<AxiomRank> AxiomList;
	
	public ConflictSet(OWLClass inconsistentClass, ArrayList<AxiomRank> axiomList, Integer classId){	
		
		ClassId = classId;
		InconsistentClass = inconsistentClass;
		AxiomList = removeDuplicates(axiomList);
	}
	
	public Integer getAxiomCount(){
		return AxiomList.size();
	}
	
	public void AddAxioms(ArrayList<AxiomRank> axiomList){		
		AxiomList.addAll(removeDuplicates(axiomList));		
	}
	
	private ArrayList<AxiomRank> removeDuplicates(ArrayList<AxiomRank> axiomList){
		//TODO - remove duplicates
		return axiomList;
	}
	
	//get set
	public void setClassId (Integer classId)
    {
		ClassId = classId;           
    }
    public Integer getClassId()
    {
        return ClassId;
    }
	
	public void setInconsistentClass (OWLClass inconsistentClass)
    {
		InconsistentClass = inconsistentClass;           
    }
    public OWLClass getInconsistentClass()
    {
        return InconsistentClass;
    }
	
    public void setAxiomList (ArrayList<AxiomRank> axiomList)
    {
    	AxiomList = axiomList;           
    }
    public ArrayList<AxiomRank> getAxiomList()
    {
        return AxiomList;
    }
    
}


