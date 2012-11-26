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
	private Tree<AxiomRank> axiomTree = new Tree<AxiomRank>();
	private ArrayList<ArrayList<AxiomRank>> axiomLists;
		
	public Integer getClassCount(){
		return ConflictSets.size();
	}	
	
	public ConflictSet getSet(OWLClass cls){
		
		ConflictSet reqdSet = null;
		
		for(ConflictSet cset : ConflictSets){
			if(cset.getInconsistentClass() == cls)
				reqdSet = cset;
		}
		
		return reqdSet;
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
	
	//TODO Very slow, have to improve performance of finding hitting sets
	public ArrayList<OWLAxiom> computeHittingSet(ConflictSetList mups){
		
		axiomLists = new ArrayList<ArrayList<AxiomRank>>();
		
		for(ConflictSet set : mups.getConflictSets()){
			axiomLists.add(set.getAxiomList());			
		}
		
		Integer outerIndex = 0;
		Integer index = 0;
		
		while((axiomLists.get(outerIndex).size() - 1) > index){
			
			System.out.println(index + "/" + outerIndex);
			
			AxiomRank ar = axiomLists.get(outerIndex).get(index);
			
			axiomTree.addChild(ar,null); //adding under root

			if(((axiomLists.size() - 1) > outerIndex))
				getAxiomNextLayer((outerIndex + 1),ar);
			
			index++;
		}
		
		axiomTree.print();
		
		return new ArrayList<OWLAxiom>();
	}
	
	private void getAxiomNextLayer(int outerIndex,AxiomRank parent){
		
		Integer index = 0;
		
		while((axiomLists.get(outerIndex).size() - 1) > index){
			
			//System.out.println(index + "/" + outerIndex);
			
			AxiomRank ar = axiomLists.get(outerIndex).get(index);
			
			axiomTree.addChild(ar, parent); //adding under root

			if(((axiomLists.size() - 1) > outerIndex))
				getAxiomNextLayer((outerIndex + 1),ar);
			
			index++;
		}
	}
	

	public void FixMappings(ArrayList<OWLAxiom> axioms) {
			
	}
	
	public ConflictSetList computeMUPS() {
		
		ArrayList<ArrayList<OWLClass>> branches;
		ConflictSetList mups = new ConflictSetList();
		classTree = new Tree<OWLClass>();
		
		for(ConflictSet set : ConflictSets){
			
			classTree.addChild(set.getInconsistentClass(),null); //adding under root
				
			classTree = getSubClasses(set.getInconsistentClass());
			//System.out.println(set.getInconsistentClass() + "--");
		}
		
		//classTree.print();
		
		branches = classTree.getAllBranches(getEqAxiomClasses());
		//System.out.println("branch size - " + branches.size());
		
		for(ArrayList<OWLClass> br : branches){
			for(OWLClass cls : br ){
				//System.out.println("Branch : Class - " + cls);	
				ConflictSet cset = getSet(cls);
				
				if(cset != null)
					mups.addDistinct(cset);
			}
		}
		
		return mups;
	}
	
	
	private Tree<OWLClass> getSubClasses(OWLClass cls){
		
		for (OWLAxiom subClassAxiom : cls.getReferencingAxioms(MergedOntology)){
		
			if(subClassAxiom.getAxiomType() == AxiomType.SUBCLASS_OF){
				
				OWLClass subClass = (OWLClass)subClassAxiom.getClassesInSignature().toArray()[1];

				if(subClass == cls)
					continue;
				else
				{
					//System.out.println(cls + " subclass axiom" + subClassAxiom);
					classTree.addChild(subClass, cls);				
					classTree = getSubClasses(subClass);
				}
			}
		}
		
		return classTree;
	}
	
	public ArrayList<OWLClass> getEqAxiomClasses(){
		
		ArrayList<OWLClass> eqClasses = new ArrayList<OWLClass>();
		
		for(ConflictSet cset : ConflictSets){
			for(AxiomRank ar : cset.getAxiomList()){
				eqClasses.addAll(ar.getAxiomClasses());
			}
		}
		
		//removing duplicates
		HashSet<OWLClass> hs = new HashSet<OWLClass>();
		hs.addAll(eqClasses);
		eqClasses.clear();
		eqClasses.addAll(hs);
		
		return eqClasses;
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
