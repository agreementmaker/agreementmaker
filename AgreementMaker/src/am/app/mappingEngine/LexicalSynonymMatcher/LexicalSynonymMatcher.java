package am.app.mappingEngine.LexicalSynonymMatcher;

import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import am.app.Core;
import am.app.lexicon.GeneralLexiconSynSet;
import am.app.lexicon.Lexicon;
import am.app.lexicon.LexiconSynSet;
import am.app.lexicon.subconcept.SubconceptSynonymLexicon;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.AbstractMatcherParametersPanel;
import am.app.mappingEngine.LexiconStore.LexiconRegistry;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.MatcherFeature;
import am.app.ontology.Node;

import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.Property;

public class LexicalSynonymMatcher extends AbstractMatcher {

	private static final long serialVersionUID = -7674172048857214961L;
	
	private transient Lexicon sourceLexicon;
	private transient Lexicon targetLexicon;
	
	private transient Property sourceSynonymProperty, targetSynonymProperty;
	private transient Property sourceLabelProperty, targetLabelProperty;
	private transient Property sourceDefinitionProperty, targetDefinitionProperty;

	
	private transient HashMap<Node, GeneralLexiconSynSet> sourceSynsetLookup = new HashMap<Node, GeneralLexiconSynSet>();
	private transient HashMap<Node, GeneralLexiconSynSet> targetSynsetLookup = new HashMap<Node, GeneralLexiconSynSet>();
	
	public LexicalSynonymMatcher() {
		super();
		needsParam = true;
		initializeVariables();
	}
	
	public LexicalSynonymMatcher(LexicalSynonymMatcherParameters params) {
		super(params);
		needsParam = true;
		initializeVariables();
	}
	
	@Override
	protected void initializeVariables() {
		super.initializeVariables();
		needsParam = true;
		
		// TODO: Setup Features.
		addFeature(MatcherFeature.MAPPING_PROVENANCE);
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
		
		
		
		ProvenanceStructure provNoTermSyn = synonymSimilarity( sourceSet, targetSet );
		
		if( provNoTermSyn != null && provNoTermSyn.similarity > 0.0d ) {
			if( getParam().storeProvenance ) {
				Mapping m = new Mapping(source, target, provNoTermSyn.similarity);
				m.setProvenance(provNoTermSyn.getProvenanceString());
				return m;
			} else {
				return new Mapping(source, target, provNoTermSyn.similarity);
			}
		}
		
		/*boolean tempDisabled = true;
		if( source.getLocalName().equals("MA_0002684") && target.getLocalName().equals("NCI_C32658") ) {
			tempDisabled = false;
		}*/
		
		// no matches found. Try to extend the synsets.
		if( ((LexicalSynonymMatcherParameters)getParam()).useSubconceptSynonyms ) {
			
			SubconceptSynonymLexicon sourceSCSLexicon = (SubconceptSynonymLexicon) sourceLexicon;
			SubconceptSynonymLexicon targetSCSLexicon = (SubconceptSynonymLexicon) targetLexicon;
			
			
			List<String> sourceExtendedSynonyms = sourceSCSLexicon.extendSynSet(sourceSet);
			List<String> targetExtendedSynonyms = targetSCSLexicon.extendSynSet(targetSet);
			
			if( sourceExtendedSynonyms.isEmpty() && targetExtendedSynonyms.isEmpty() ) {
				// no extra synonyms.
				return null;
			}
			
			sourceExtendedSynonyms.addAll( sourceSet.getSynonyms() );
			targetExtendedSynonyms.addAll( targetSet.getSynonyms() );
			
			ProvenanceStructure prov = computeLexicalSimilarity(sourceExtendedSynonyms, targetExtendedSynonyms, 0.9d);
			
			if( prov != null && prov.similarity > 0.0d ) {
				if( getParam().storeProvenance ) {
					Mapping m = new Mapping(source, target, prov.similarity);
					m.setProvenance(prov.getProvenanceString());
					return m;
				} else {
					return new Mapping(source, target, prov.similarity);
				}
			}
		}
		
		return null;
	}

	/**
	 * Given the synsets associated with two concepts, calculate the "synonym similarity" between the concepts.
	 * 
	 * This method checks all related synsets in addition to any other synsets.
	 * 
	 * @return Currently we return the greatest similarity we find.
	 */
	private ProvenanceStructure synonymSimilarity(LexiconSynSet sourceSet, LexiconSynSet targetSet) {
	
		ProvenanceStructure prov = null; // keep track of the provenance info
		
		if( sourceSet != null && targetSet != null ) {
			try {
				prov = computeLexicalSimilarity(sourceSet, targetSet);
				if( prov != null ) prov.setSynSets(sourceSet, targetSet);
				
				if( prov != null && prov.similarity == 1.0d ) return prov; // we can't get higher than 1.0;
				
				List<LexiconSynSet> sourceRelatedSets = sourceSet.getRelatedSynSets();
				List<LexiconSynSet> targetRelatedSets = targetSet.getRelatedSynSets();
				
				for( LexiconSynSet sourceRelatedSet : sourceRelatedSets ) {
					for( LexiconSynSet targetRelatedSet : targetRelatedSets ) {
						if( sourceRelatedSet != null && targetRelatedSet != null ) {
							ProvenanceStructure currentProv = computeLexicalSimilarity(sourceRelatedSet, targetRelatedSet);
							if( currentProv != null ) currentProv.setSynSets(sourceRelatedSet, targetRelatedSet);
							
							if( currentProv != null && 
								prov != null &&
								currentProv.similarity > prov.similarity ) { prov = currentProv; } // keep track of the highest provenance
							if( prov != null && prov.similarity == 1.0d ) return prov; // we can't get higher than 1.0;
						}
					}
				}
			} catch( NullPointerException e ) {
				e.printStackTrace();
			}
		} else {
			Logger log = Logger.getLogger(this.getClass());
			log.error("LSM needs to be fixed. "  + sourceSet + " " + targetSet);
		}
			
		return prov;
	}

	/**
	 * Compute the lexical similarity betwen
	 * @param sourceLexicon2
	 * @param targetLexicon2
	 * @return
	 * @throws NullPointerException
	 */
	private ProvenanceStructure computeLexicalSimilarity(LexiconSynSet sourceLexicon2,
			LexiconSynSet targetLexicon2) throws NullPointerException {


		if( sourceLexicon2 == null ) throw new NullPointerException("Source lexicon is null.");
		if( targetLexicon2 == null ) throw new NullPointerException("Target lexicon is null.");
		
		List<String> sourceSyns = sourceLexicon2.getSynonyms();
		List<String> targetSyns = targetLexicon2.getSynonyms();

		return computeLexicalSimilarity(sourceSyns, targetSyns, 1.0d);
	}
	
	
	private ProvenanceStructure computeLexicalSimilarity( List<String> sourceSyns, List<String> targetSyns, double greatest ) {
		
		for( int i = 0; i < sourceSyns.size(); i++ ) {
			String sourceSynonym = sourceSyns.get(i);

			for( int j = 0; j < targetSyns.size(); j++ ) {
				String targetSynonym = targetSyns.get(j);
				
				if( sourceSynonym.equalsIgnoreCase(targetSynonym) ) {
					return new ProvenanceStructure(greatest, sourceSynonym, targetSynonym);
				}
			}
	
		}
		
		return null;
		
	}
	
	/**
	 * This class is just an encapsulating class that contains provenance information 
	 * for when a mapping is created.
	 * 
	 * TODO: Make Provenance a system wide thing??? - Cosmin.
	 */
	private class ProvenanceStructure {
		public double similarity;
		public String sourceSynonym;
		public String targetSynonym;
		public LexiconSynSet sourceSynSet;
		public LexiconSynSet targetSynSet;
		
		public ProvenanceStructure(double similarity, String sourceSynonym, String targetSynonym) {
			this.similarity = similarity;
			this.sourceSynonym = sourceSynonym;
			this.targetSynonym = targetSynonym;
		}
		
		public void setSynSets( LexiconSynSet sourceSynSet, LexiconSynSet targetSynSet ) {
			this.sourceSynSet = sourceSynSet;
			this.targetSynSet = targetSynSet;
		}
		
		public String getProvenanceString() {
			return "\"" + sourceSynonym + "\" (" + sourceSynSet + ") = \"" + targetSynonym + "\" (" + targetSynSet + ")";
		}
	}
}
