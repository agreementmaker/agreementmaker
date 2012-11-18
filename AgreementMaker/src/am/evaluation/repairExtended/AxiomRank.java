package am.evaluation.repairExtended;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.openjena.atlas.logging.Log;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

import am.app.mappingEngine.referenceAlignment.MatchingPair;

public class AxiomRank {
	
	
	public List<OWLAxiom> rankByAxiomFrequency(ArrayList<OWLAxiom> axioms){
		
		List<OWLAxiom> rankedAxioms = new ArrayList<OWLAxiom>();
		Map<OWLAxiom,Integer> axiomCounts = new HashMap<OWLAxiom,Integer>();
		
		int count = 1;
		
		for(OWLAxiom axm : axioms){			
			if(axiomCounts.containsKey(axm)){
				axiomCounts.put(axm, (Integer)axiomCounts.get(axm)+1);				
			}
			else 
				axiomCounts.put(axm, count);			
		}
		
		//Sort keys by values.
		//final Map langForComp = lang;
		
		/*Collections.sort(rankedAxioms,new Comparator<OWLAxiom>(){
						public int compare(Object left, Object right){
							Integer leftKey = (Integer)left;
							Integer rightKey = (Integer)right;
		 
							String leftValue = (String)langForComp.get(leftKey);
							String rightValue = (String)langForComp.get(rightKey);
							return leftValue.compareTo(rightValue);
						}

						@Override
						public int compare(OWLAxiom o1, OWLAxiom o2) {
							// TODO Auto-generated method stub
							return 0;
						}
					});		*/
		
		
		printMap(axiomCounts);
		return rankedAxioms;
		
	}
	
	public static void printMap(Map mp) {
	    Iterator it = mp.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pairs = (Map.Entry)it.next();
	        System.out.println(pairs.getKey() + " = " + pairs.getValue());
	        it.remove(); // avoids a ConcurrentModificationException
	    }
	}
	
}
