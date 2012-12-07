package am.evaluation.repairExtended;

import java.util.ArrayList;

import org.semanticweb.owlapi.model.OWLClass;
/**
 * @author Pavan
 *
 *	ConflictSset class is used to hold a OWLClass and the corresponding OWLAxioms
 */
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
		AxiomList.addAll(axiomList);
		
		AxiomList = removeDuplicates(AxiomList);
	}
	
	private ArrayList<AxiomRank> removeDuplicates(ArrayList<AxiomRank> axiomList){
		
		ArrayList<AxiomRank> distinctList = new ArrayList<AxiomRank>();
		
		for(AxiomRank p : axiomList){
			if(!distinctList.contains(p))
				distinctList.add(p);
		}
		
		return axiomList;
	}
	
	//getter setter
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


