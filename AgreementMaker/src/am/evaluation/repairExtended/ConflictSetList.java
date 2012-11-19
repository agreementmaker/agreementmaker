package am.evaluation.repairExtended;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;

public class ConflictSetList {
	
	private static Logger log = Logger.getLogger(ConflictSetList.class);
	
	private ArrayList<ConflictSet> ConflictSets = new ArrayList<ConflictSet>();
		
	public Integer getClassCount(){
		return ConflictSets.size();
	}	
	
	public Integer getAxiomCount(){
		
		int axiomCount = 0;
		
		for(ConflictSet set : ConflictSets){
			axiomCount = axiomCount + set.getAxiomCount();
		}	
		
		return axiomCount;
	}
	
	public ArrayList<OWLClass> getClassList(){
		
		ArrayList<OWLClass> classes = new ArrayList<OWLClass>();
		
		for(ConflictSet cset : ConflictSets){
			classes.add(cset.getInconsistentClass());
		}
		
		return classes;
	}
	
	public ArrayList<AxiomRank> getAxiomRankList(){
		
		ArrayList<AxiomRank> axiomRanks = new ArrayList<AxiomRank>();
		
		for(ConflictSet cset : ConflictSets){
			axiomRanks.addAll(cset.getAxiomList());
		}
		
		return axiomRanks;
	}

	public void addDistinct(ConflictSet conflictSet) {
		
		ConflictSet set = getConflictSet(conflictSet.getInconsistentClass());
		
		if(set == null)					
			ConflictSets.add(conflictSet);			
		else 
			set.AddAxioms(conflictSet.getAxiomList());	
	}
	
	public ConflictSet getConflictSet(OWLClass cls){
		
		ConflictSet set = null;
		
		for(ConflictSet cset : ConflictSets){
			if(cset.getInconsistentClass() == cls){
				set = cset;
				break;
			}				
		}
		
		return set;		
	}
	
	//@SuppressWarnings("unchecked")
	public void rankAxioms(){
		
		int count = 1;
		Map<OWLAxiom,Integer> axiomCounts = new HashMap<OWLAxiom,Integer>();
		ArrayList<AxiomRank> axiomRanks = getAxiomRankList();
		ArrayList<OWLAxiom> axioms = new ArrayList<OWLAxiom>();
		
		for(AxiomRank ar : axiomRanks){
			
			OWLAxiom axm = ar.getAxiom();
			axioms.add(axm);
			
			if(axiomCounts.containsKey(axm)){
				axiomCounts.put(axm, (Integer)axiomCounts.get(axm)+1);				
			}
			else 
				axiomCounts.put(axm, count);	
		}
		
		/*Iterator it = axiomCounts.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pairs = (Map.Entry)it.next();
	        System.out.println(pairs.getKey() + " = " + pairs.getValue());
	        it.remove(); // avoids a ConcurrentModificationException
	    }*/
		
		ArrayList<OWLAxiom> sortedAxioms = (ArrayList<OWLAxiom>) entriesSortedByValues(axiomCounts);
		
		for(ConflictSet set : ConflictSets){
			for(AxiomRank ar : set.getAxiomList()){
				ar.setRank(sortedAxioms.indexOf(ar.getAxiom()) + 1);	
			}
		}	
	}
	
	static <K,V extends Comparable<? super V>> 
    //List<Entry<K, V>> entriesSortedByValues(Map<K,V> map) {
	List<K> entriesSortedByValues(Map<K,V> map) {

		List<Entry<K,V>> sortedEntries = new ArrayList<Entry<K,V>>(map.entrySet());
	
		Collections.sort(sortedEntries, 
				new Comparator<Entry<K,V>>() {
	        	@Override
	        	public int compare(Entry<K,V> e1, Entry<K,V> e2) {
	        		return e2.getValue().compareTo(e1.getValue());
	        	}
	    	}
		);

		List<K> sortedKeys = new ArrayList<K>();
		
		//return sortedEntries;
		for (Map.Entry<K,V> entry : sortedEntries) {
			sortedKeys.add(entry.getKey());
			System.out.println(entry.getKey() + " - (count - " + entry.getValue() + ")");
		}		
	    		
		return sortedKeys;
	}
	
	public void computeHittingSet(){
		//TODO
	}
	
	public void printConflictSetList(){
		
		for(ConflictSet set : ConflictSets){
			
			log.info("--class - (" + set.getClassId() + ") " + set.getClass());
			
			for(AxiomRank ar : set.getAxiomList()){
				log.info("-----axiom - " + ar.getAxiom() + " (rank - " + ar.getRank() + " )");
			}
		}
		
	}
	
	//get set
	public void setConflictSets (ArrayList<ConflictSet> conflictSets)
    {
		ConflictSets = conflictSets;           
    }
    public ArrayList<ConflictSet> getConflictSets()
    {
        return ConflictSets;
    }
}
