package am.app.mappingEngine.LexicalSynonymMatcher;

import java.util.HashMap;
import java.util.List;

import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.Property;
import am.app.Core;
import am.app.lexicon.GeneralLexiconSynSet;
import am.app.lexicon.Lexicon;
import am.app.lexicon.LexiconSynSet;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.Alignment;
import am.app.ontology.Node;

public class LexicalSynonymMatcher extends AbstractMatcher {
	
	
	Lexicon sourceLexicon;
	Lexicon targetLexicon;
	
	Property sourceSynonymProperty, targetSynonymProperty;
	Property sourceLabelProperty, targetLabelProperty;
	Property sourceDefinitionProperty, targetDefinitionProperty;

	
	HashMap<Node, GeneralLexiconSynSet> sourceSynsetLookup = new HashMap<Node, GeneralLexiconSynSet>();
	HashMap<Node, GeneralLexiconSynSet> targetSynsetLookup = new HashMap<Node, GeneralLexiconSynSet>();
	
	
/**
 * PRE PROCESSING STEP
 * 
 * 1) Build the Ontology Lexicons.
 */
	int alignmentsfound = 0;
	
	@Override
	protected void beforeAlignOperations() throws Exception {
		super.beforeAlignOperations();
		
		sourceLexicon = Core.getLexiconStore().getSourceOntLexicon(sourceOntology);			
		targetLexicon = Core.getLexiconStore().getTargetOntLexicon(targetOntology);			
		Lexicon sourceWordNetLexicon = Core.getLexiconStore().getSourceWNLexicon(sourceOntology, sourceLexicon);
		Lexicon targetWordNetLexicon = Core.getLexiconStore().getTargetWNLexicon(targetOntology, targetLexicon);
		
		//sourceLexicon.print( System.out );
		//targetLexicon.print( System.out );
		
		//if( !Utility.displayConfirmPane("waiting for continue", "continue?") ) cancel(true);
		
	}	
	
/**
 * MATCHING WITH SYNONYMS
 */
	
	@Override
	protected Alignment alignTwoNodes(Node source, Node target,
			alignType typeOfNodes) throws Exception {


		
		OntResource sourceOR = source.getResource().as(OntResource.class);
		OntResource targetOR = target.getResource().as(OntResource.class);
		
		LexiconSynSet sourceSet = sourceLexicon.getSynSet(sourceOR);
		LexiconSynSet targetSet = targetLexicon.getSynSet(targetOR);
		
		double synonymSimilarity = synonymSimilarity( sourceSet, targetSet );
		
		if( synonymSimilarity > 0.0d ) {
			alignmentsfound++;
			return new Alignment(source, target, synonymSimilarity);
		}
		return null;
	}

	private double synonymSimilarity(LexiconSynSet sourceSet, LexiconSynSet targetSet) {
	
		double greatestLexicalSimilarity = 0.0d;
		if( sourceSet != null && targetSet != null ) {
			try {
				greatestLexicalSimilarity = computeLexicalSimilarity(sourceSet, targetSet);
				
				List<LexiconSynSet> sourceRelatedSets = sourceSet.getRelatedSynSets();
				List<LexiconSynSet> targetRelatedSets = targetSet.getRelatedSynSets();
				
				for( LexiconSynSet sourceRelatedSet : sourceRelatedSets ) {
					for( LexiconSynSet targetRelatedSet : targetRelatedSets ) {
						if( sourceRelatedSet != null && targetRelatedSet != null ) {
							double computedLexicalSimilarity = computeLexicalSimilarity(sourceRelatedSet, targetRelatedSet);
							if( computedLexicalSimilarity > greatestLexicalSimilarity ) greatestLexicalSimilarity = computedLexicalSimilarity;
						}
					}
				}
			} catch( NullPointerException e ) {
				e.printStackTrace();
			}
		}
			
		return greatestLexicalSimilarity;
	}

	private double computeLexicalSimilarity(LexiconSynSet sourceLexicon2,
			LexiconSynSet targetLexicon2) throws NullPointerException {


		if( sourceLexicon2 == null ) throw new NullPointerException("Source lexicon is null.");
		if( targetLexicon2 == null ) throw new NullPointerException("Target lexicon is null.");
		
		
		double greatestWordSimilarity = 0.0d;

		
		boolean breakout = false;
		List<String> sourceSyns = sourceLexicon2.getSynonyms();
		for( int i = 0; i < sourceSyns.size(); i++ ) {
			String sourceSynonym = sourceSyns.get(i);
			List<String> targetSyns = targetLexicon2.getSynonyms();
			for( int j = 0; j < targetSyns.size(); j++ ) {
				String targetSynonym = targetSyns.get(j);
				
				if( sourceSynonym.equalsIgnoreCase(targetSynonym) ) {
					greatestWordSimilarity = 1.0d;
					breakout = true;
					break;
				}
			}
			if( breakout ) break;
		}
		
		return greatestWordSimilarity;
	}
	
}
