package am.app.mappingEngine.LexicalSynonymMatcher;

import java.util.HashMap;
import java.util.List;

import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.Property;
import am.app.Core;
import am.app.lexicon.GeneralLexiconSynSet;
import am.app.lexicon.Lexicon;
import am.app.lexicon.LexiconSynSet;
import am.app.lexicon.subconcept.SubconceptSynonymLexicon;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.AbstractMatcherParametersPanel;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.LexiconStore.LexiconRegistry;
import am.app.ontology.Node;

public class LexicalSynonymMatcher extends AbstractMatcher {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7674172048857214961L;
	
	private transient Lexicon sourceLexicon;
	private transient Lexicon targetLexicon;
	
	private transient Property sourceSynonymProperty, targetSynonymProperty;
	private transient Property sourceLabelProperty, targetLabelProperty;
	private transient Property sourceDefinitionProperty, targetDefinitionProperty;

	
	private transient HashMap<Node, GeneralLexiconSynSet> sourceSynsetLookup = new HashMap<Node, GeneralLexiconSynSet>();
	private transient HashMap<Node, GeneralLexiconSynSet> targetSynsetLookup = new HashMap<Node, GeneralLexiconSynSet>();
	
	
	@Override
	protected void initializeVariables() {
		super.initializeVariables();
		needsParam = true;
		
		// TODO: Setup Features.
	}
	
	@Override
	public AbstractMatcherParametersPanel getParametersPanel() {
		if( parametersPanel == null ) {
			parametersPanel = new LexicalSynonymMatcherParametersPanel();
		}
		return parametersPanel;
	}
	
/**
 * PRE PROCESSING STEP
 * 
 * 1) Build the Ontology Lexicons.
 */
	
	@Override
	protected void beforeAlignOperations() throws Exception {
		super.beforeAlignOperations();
		
		sourceLexicon = Core.getLexiconStore().getLexicon(sourceOntology.getID(), LexiconRegistry.ONTOLOGY_LEXICON);			
		targetLexicon = Core.getLexiconStore().getLexicon(targetOntology.getID(), LexiconRegistry.ONTOLOGY_LEXICON);			
		//Lexicon sourceWordNetLexicon = Core.getLexiconStore().getLexicon(sourceOntology.getID(), LexiconRegistry.WORDNET_LEXICON);
		//Lexicon targetWordNetLexicon = Core.getLexiconStore().getLexicon(targetOntology.getID(), LexiconRegistry.WORDNET_LEXICON);
		
		//sourceLexicon.print( System.out );
		//targetLexicon.print( System.out );
		
		//if( !Utility.displayConfirmPane("waiting for continue", "continue?") ) cancel(true);
		
	}	
	
/**
 * MATCHING WITH SYNONYMS
 */
	
	@Override
	protected Mapping alignTwoNodes(Node source, Node target,
			alignType typeOfNodes) throws Exception {


		
		OntResource sourceOR = source.getResource().as(OntResource.class);
		OntResource targetOR = target.getResource().as(OntResource.class);
		
		LexiconSynSet sourceSet = sourceLexicon.getSynSet(sourceOR);
		LexiconSynSet targetSet = targetLexicon.getSynSet(targetOR);
		
		if( sourceSet == null || targetSet == null ) return null; // one or both of the concepts do not have a synset.
		
		double synonymSimilarity = synonymSimilarity( sourceSet, targetSet );
		
		if( synonymSimilarity > 0.0d ) {
			//alignmentsfound++;
			return new Mapping(source, target, synonymSimilarity);
		}
		
		// no matches found. Try to extend the synsets.
		if( ((LexicalSynonymMatcherParameters)getParam()).useSubconceptSynonyms ) {
			
			SubconceptSynonymLexicon sourceSCSLexicon = (SubconceptSynonymLexicon) sourceLexicon;
			SubconceptSynonymLexicon targetSCSLexicon = (SubconceptSynonymLexicon) targetLexicon;
			
			
			List<String> sourceExtendedSynonyms = sourceSCSLexicon.extendSynSet(sourceSet);
			List<String> targetExtendedSynonyms = targetSCSLexicon.extendSynSet(targetSet);
			
			sourceExtendedSynonyms.addAll( sourceSet.getSynonyms() );
			targetExtendedSynonyms.addAll( targetSet.getSynonyms() );
			
			double extendedSimilarity = computeLexicalSimilarity(sourceExtendedSynonyms, targetExtendedSynonyms);
			
			if( extendedSimilarity > 0.0d ) {
				return new Mapping(source, target, extendedSimilarity );
			}
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
		
		List<String> sourceSyns = sourceLexicon2.getSynonyms();
		List<String> targetSyns = targetLexicon2.getSynonyms();

		return computeLexicalSimilarity(sourceSyns, targetSyns);
	}
	
	
	private double computeLexicalSimilarity( List<String> sourceSyns, List<String> targetSyns ) {
		
		double greatestWordSimilarity = 0.0d;

		boolean breakout = false;
	//	List<String> sourceSyns = sourceLexicon2.getSynonyms();
		for( int i = 0; i < sourceSyns.size(); i++ ) {
			String sourceSynonym = sourceSyns.get(i);
//			List<String> targetSyns = targetLexicon2.getSynonyms();
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
