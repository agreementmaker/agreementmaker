package am.extension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import am.app.Core;
import am.app.lexicon.Lexicon;
import am.app.lexicon.LexiconSynSet;
import am.app.lexicon.subconcept.STLexicon;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.LexiconStore.LexiconRegistry;
import am.app.mappingEngine.SimilarityMatrix;
import am.app.ontology.Node;

import com.hp.hpl.jena.ontology.OntResource;

/**
 * 
 * @author cosmin
 *
 */
public class MyTestMatcher extends AbstractMatcher {

	@Override
	protected void beforeAlignOperations() throws Exception {
		super.beforeAlignOperations();
		Logger log = Logger.getLogger(this.getClass());
		//log.info("Classes with more than one child:");
		
	}
	
	@Override
	protected SimilarityMatrix alignClasses(List<Node> sourceClassList,
			List<Node> targetClassList) throws Exception {
	
		
		System.out.println("Source ontology:");
		Lexicon sourceLexicon = 
				Core.getLexiconStore().getLexicon( Core.getInstance().getSourceOntology().getID(), LexiconRegistry.ONTOLOGY_LEXICON);
		
		printProfile( sourceClassList, sourceLexicon );
		
		//Utility.displayConfirmPane("Continue?", "Continue?");
		
		System.out.println("Target ontology:");
		Lexicon targetLexicon = 
				Core.getLexiconStore().getLexicon( Core.getInstance().getTargetOntology().getID(), LexiconRegistry.ONTOLOGY_LEXICON);
		
		printProfile( targetClassList, targetLexicon );
		
		return null;
	}

	private void printProfile( List<Node> sourceClassList,
			Lexicon sourceLexicon) {
		
		int totalSynonyms = 0;
		int maxSynonyms = 0;
		ArrayList<Double> totalSynonymList = new ArrayList<Double>();
		
		for( Node currentClass : sourceClassList ) {
			
			OntResource currentOR = currentClass.getResource().as(OntResource.class);
			LexiconSynSet currentSet = sourceLexicon.getSynSet(currentOR);
			
			
			int currentSynonyms = 0;
			
			if( currentSet == null ) {
				//System.out.println( currentClass.getIndex() + ",\t0");
				continue;
			}
			
			// normal lexicon call.
			currentSynonyms += currentSet.getSynonyms().size();
			for( LexiconSynSet synSet : currentSet.getRelatedSynSets() ) {
				currentSynonyms += synSet.getSynonyms().size();
			}
			
			// st lexicon
			if( sourceLexicon instanceof STLexicon ) {
				STLexicon stLexicon = (STLexicon) sourceLexicon;
				
				Set<String> currentExtension = stLexicon.extendSynSet(currentSet);
				//extendedSynSets.put(currentClass, currentExtension);
				currentSynonyms += currentExtension.size();
			}
					
			totalSynonyms += currentSynonyms;
			totalSynonymList.add(new Double(currentSynonyms));
			if( currentSynonyms > maxSynonyms ) maxSynonyms = currentSynonyms;
			
			//System.out.println( currentClass.getIndex() + ",\t" + currentSynonyms);
		}
		
		Collections.sort(totalSynonymList);

		double median;
		
		if (totalSynonymList.size() % 2 == 1)
			median = totalSynonymList.get((totalSynonymList.size()+1)/2-1).doubleValue();
		else
		    {
			double lower = totalSynonymList.get(totalSynonymList.size()/2-1);
			double upper = totalSynonymList.get(totalSynonymList.size()/2);
		 
			median = (lower + upper) / 2.0d;
		}	
		
		double avgSynonyms = (double)totalSynonyms / (double)sourceClassList.size();
		
		System.out.println("Average # of synonyms: " + avgSynonyms);
		System.out.println("Median # of synonyms: " + median);
		System.out.println("Max # of synonyms: " + maxSynonyms);
	}
	
}
