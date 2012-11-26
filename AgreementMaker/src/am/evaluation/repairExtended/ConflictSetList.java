package am.evaluation.repairExtended;

import java.io.File;
import java.io.IOException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.HermiT.Reasoner.ReasonerFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.RDFXMLOntologyFormat;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.RemoveAxiom;
import org.semanticweb.owlapi.reasoner.Node;

import weka.core.RelationalLocator;
import weka.filters.unsupervised.attribute.RELAGGS;

import am.batchMode.simpleBatchMode.OntologiesType;
import am.evaluation.repair.AlignmentRepairUtilities;

import com.clarkparsia.owlapi.explanation.util.OntologyUtils;
import com.hp.hpl.jena.sparql.pfunction.library.concat;

public class ConflictSetList {
	
	private static Logger log = Logger.getLogger(ConflictSetList.class);
	private AlignmentRepairUtilities util = new AlignmentRepairUtilities(log);
	
	private OWLOntology MergedOntology;
	
	private ArrayList<ConflictSet> ConflictSets = new ArrayList<ConflictSet>();
	private ArrayList<OWLAxiom> sortedAxioms = new ArrayList<OWLAxiom>();
	
	private Tree<OWLClass> classTree;
	Tree<AxiomRank> axiomTree = new Tree<AxiomRank>();
		
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
		
		sortedAxioms = (ArrayList<OWLAxiom>) entriesSortedByValues(axiomCounts);
		
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
			//System.out.println(entry.getKey() + " - (count - " + entry.getValue() + ")");
		}		
	    		
		return sortedKeys;
	}
	
	public ArrayList<OWLAxiom> computeHittingSet(){
		
		ArrayList<OWLAxiom> minimalHittingSet = new ArrayList<OWLAxiom>();
		
		/*List<ArrayList<OWLAxiom>> hittingSets = new ArrayList<ArrayList<OWLAxiom>>();
		
		Integer minimumPathRank = 0;
		Integer classCount = 0;
		
		for(ConflictSet set : ConflictSets){						
			for(AxiomRank ar : set.getAxiomList()){		
				
				if(classCount == 0){
					ArrayList<OWLAxiom> axiomlist = new ArrayList<OWLAxiom>();
					axiomlist.add(ar.getAxiom());
					hittingSets.add(axiomlist);
					continue;
				}
				
				for(ArrayList<OWLAxiom> hittingSet : hittingSets){
					
					if(!hittingSet.contains(ar.getAxiom())){				
						hittingSet.add(ar.getAxiom());
					}
				}				
			}
			
			classCount++;
		}	*/
		//return minimalHittingSet;
		
		 
		ArrayList<ArrayList<AxiomRank>> axiomLists = new ArrayList<ArrayList<AxiomRank>>();
		
		ArrayList<ArrayList<AxiomRank>> hittingSets;
		//Tree<AxiomRank> axiomTree = new Tree<AxiomRank>();
		
		for(ConflictSet set : ConflictSets){
			axiomLists.add(set.getAxiomList());			
		}
		
		Integer outerIndex = 0;
		
		//for(AxiomRank ar : axiomLists.get(index)){			
		for(int index = 0; index < axiomLists.get(outerIndex).size(); index++)	{
			
			outerIndex = 0;
			System.out.println((index + 1) + "-------------------------------------------------");
			System.out.println("level " + outerIndex + " - " + (index+1) + "/" +axiomLists.get(outerIndex).size());
			AxiomRank ar = axiomLists.get(0).get(index);
			
			axiomTree.addChild(ar,null); //adding under root
			//System.out.println(ar.getAxiom() + "-" + index);
			//System.out.println("parameters " + outerIndex+1 + "," + ar.getAxiom());
			getAxiomNextLayer(axiomLists,outerIndex + 1,ar);
		}
				
		
		//System.out.println(ar + "--");

		//classTree.print();
		
		hittingSets = axiomTree.getAllBranches();
		
		for(ArrayList<AxiomRank> br : hittingSets){
			System.out.println("hitting set - " + br);
		}
		
		return sortedAxioms;
	}
	
	private void getAxiomNextLayer(ArrayList<ArrayList<AxiomRank>> list, int outerIndex,AxiomRank parent){
			
		/*String space = "";
		for(int i=0;i<outerIndex;i++){
			space = space + "-";
		}*/
		
		try{
		//System.out.println((1) + "/" + list.get(outerIndex).size());
		
			//for (AxiomRank ar : list.get(outerIndex)){
			
				for(int index = 0; index < list.get(outerIndex).size(); index++)	{
					//System.out.println(space + (index+1) + "/" + list.get(outerIndex).size());
					
					AxiomRank ar = list.get(outerIndex).get(index);
					
					//System.out.println(ar.getAxiom());
					axiomTree.addChild(ar, parent);		
						
					//if(index < list.get(outerIndex).size())	{
					if(outerIndex + 1 < list.size()){
					    //System.out.println("parameters " + outerIndex+1 + "," + ar.getAxiom());
						//System.out.println("dive " + outerIndex);
						//TODO - fix bug
						//getAxiomNextLayer(list,outerIndex+1,ar);
					}
				}
			//}
		}
		catch(Exception ex){
			System.out.println(ex);
		}		
		//System.out.println("float " + outerIndex);
		
	}
	

	public void FixMappings(ArrayList<OWLAxiom> axioms) {
			
	}
	
	public void computeMUPS() {
				 
		ArrayList<AxiomRank> eq_axioms = new ArrayList<AxiomRank>();
		
		ArrayList<ArrayList<OWLClass>> branches;
		classTree = new Tree<OWLClass>();
		
		for(ConflictSet set : ConflictSets){
			
			classTree.addChild(set.getInconsistentClass(),null); //adding under root
				
			classTree = getSubClasses(set.getInconsistentClass());
			System.out.println(set.getInconsistentClass() + "--");
			//eq_axioms = set.getAxiomList();
			
			//for(AxiomRank axiom : eq_axioms){
				
				
				
			//}
			
		}
		
		//classTree.print();
		
		branches = classTree.getAllBranches();
		
		for(ArrayList<OWLClass> br : branches){
			//System.out.println("Branch - " + br);
		}
	}
	
	
	private Tree<OWLClass> getSubClasses(OWLClass cls){
		
		for (OWLAxiom subClassAxiom : cls.getReferencingAxioms(MergedOntology)){
		
			if(subClassAxiom.getAxiomType() == AxiomType.SUBCLASS_OF){
				
				OWLClass subClass = (OWLClass)subClassAxiom.getClassesInSignature().toArray()[1];
				
				if(subClass != cls){
					//System.out.println(cls + " subclass axiom" + subClassAxiom);
					classTree.addChild(subClass, cls);				
					classTree = getSubClasses(subClass);
				}

			}
		}
		
		return classTree;
	}
	
	public void printConflictSetList(){
		
		for(ConflictSet set : ConflictSets){
			
			log.info("--class - " + set.getInconsistentClass());
			
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
    
    public void setMergedOntology (OWLOntology mergedOntology)
    {
		MergedOntology = mergedOntology;           
    }
    public OWLOntology getMergedOntology()
    {
        return MergedOntology;
    }
}
