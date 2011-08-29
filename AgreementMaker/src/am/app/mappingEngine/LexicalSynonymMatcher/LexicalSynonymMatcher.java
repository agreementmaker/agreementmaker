package am.app.mappingEngine.LexicalSynonymMatcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import am.app.Core;
import am.app.lexicon.Lexicon;
import am.app.lexicon.LexiconSynSet;
import am.app.lexicon.subconcept.SubconceptSynonymLexicon;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.AbstractMatcherParametersPanel;
import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.mappingEngine.LexiconStore.LexiconRegistry;
import am.app.mappingEngine.similarityMatrix.ArraySimilarityMatrix;
import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.MappedNodes;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.MatcherFeature;
import am.app.mappingEngine.SimilarityMatrix;
import am.app.ontology.Node;
import am.app.ontology.Ontology;

import com.hp.hpl.jena.ontology.OntResource;

public class LexicalSynonymMatcher extends AbstractMatcher {

	private static final long serialVersionUID = -7674172048857214961L;
	
	private transient Lexicon sourceLexicon;
	private transient Lexicon targetLexicon;
	
	// The hashmap and the list of string are used to optimize the LSM when running with SCS enabled.
	private HashMap<Node,List<String>> extendedSynSets;
	private List<String> extendedSingle;
	private boolean sourceIsLarger = false;  // TODO: Figure out a better way to do this.
	
	// use this to save time.
	LexiconSynSet sourceSet;  // using this field variable gives a 3% speed boost to LSM without SCS.
	
	// Default constructor.
	public LexicalSynonymMatcher() { super(); initializeVariables(); }
	
	// Constructor that sets the parameters.
	public LexicalSynonymMatcher(LexicalSynonymMatcherParameters params) { super(params); 	initializeVariables(); }
	
	@Override
	protected void initializeVariables() {
		super.initializeVariables();
		needsParam = true;
		
		// TODO: Setup Features.
		addFeature(MatcherFeature.MAPPING_PROVENANCE);
	}
	
	@Override 
	public AbstractMatcherParametersPanel getParametersPanel() {
		if( parametersPanel == null ) { parametersPanel = new LexicalSynonymMatcherParametersPanel(); }
		return parametersPanel;
	}
	
	
	/**
	 * Before aligning, get a copy of the current ontology lexicons.
	 */
	@Override
	protected void beforeAlignOperations() throws Exception {
		super.beforeAlignOperations();
		
		sourceLexicon = Core.getLexiconStore().getLexicon(sourceOntology.getID(), LexiconRegistry.ONTOLOGY_LEXICON);			
		targetLexicon = Core.getLexiconStore().getLexicon(targetOntology.getID(), LexiconRegistry.ONTOLOGY_LEXICON);			
		//Lexicon sourceWordNetLexicon = Core.getLexiconStore().getLexicon(sourceOntology.getID(), LexiconRegistry.WORDNET_LEXICON);
		//Lexicon targetWordNetLexicon = Core.getLexiconStore().getLexicon(targetOntology.getID(), LexiconRegistry.WORDNET_LEXICON);
		
	}	
	
	/**
	 * Method updated with ST optimizations. - Cosmin.
	 */
	@Override
	protected SimilarityMatrix alignNodesOneByOne(ArrayList<Node> sourceList,
			ArrayList<Node> targetList, alignType typeOfNodes) throws Exception {

		if(param.completionMode && inputMatchers != null && inputMatchers.size() > 0){ 
    		//run in optimized mode by mapping only concepts that have not been mapped in the input matcher
    		if(typeOfNodes.equals(alignType.aligningClasses)){
    			return alignUnmappedNodes(sourceList, targetList, inputMatchers.get(0).getClassesMatrix(), inputMatchers.get(0).getClassAlignmentSet(), alignType.aligningClasses);
    		}
    		else{
    			return alignUnmappedNodes(sourceList, targetList, inputMatchers.get(0).getPropertiesMatrix(), inputMatchers.get(0).getPropertyAlignmentSet(), alignType.aligningProperties);
    		}
		}
    	
    	else{
    		//run as a generic matcher who maps all concepts by doing a quadratic number of comparisons
	    	SimilarityMatrix matrix = new ArraySimilarityMatrix(sourceList.size(), targetList.size(), typeOfNodes, relation);
			
			// SCS optimizations (only for classes at the moment)
			if( ((LexicalSynonymMatcherParameters)getParam()).useSynonymTerms && typeOfNodes == alignType.aligningClasses ) {
				// choose the smaller ontology.
				List<Node> smallerList = null, largerList = null;
				SubconceptSynonymLexicon smallerLexicon = null, largerLexicon = null;
				
				if( sourceList.size() > targetList.size() ) {
					smallerList = targetList;
					smallerLexicon = (SubconceptSynonymLexicon) targetLexicon;
					largerList = sourceList;
					largerLexicon = (SubconceptSynonymLexicon) sourceLexicon;
					sourceIsLarger = true;
				} else {
					smallerList = sourceList;
					smallerLexicon = (SubconceptSynonymLexicon) sourceLexicon;
					largerList = targetList;
					largerLexicon = (SubconceptSynonymLexicon) targetLexicon;
					sourceIsLarger = false;
				}
				
				// create the hashmap of the smaller ontology
				extendedSynSets = new HashMap<Node,List<String>>();
				
				for( Node currentClass : smallerList ) {
					OntResource currentOR = currentClass.getResource().as(OntResource.class);
					LexiconSynSet currentSet = smallerLexicon.getSynSet(currentOR);
					if( currentSet == null ) continue;
					List<String> currentExtension = smallerLexicon.extendSynSet(currentSet);
					currentExtension.addAll(currentSet.getSynonyms());
					extendedSynSets.put(currentClass, currentExtension);
				}
				
				
				// iterate through the larger ontology
				for( int i = 0; i < largerList.size(); i++ ) {
					Node larger = largerList.get(i);
					
					OntResource largerOR = larger.getResource().as(OntResource.class);
					LexiconSynSet largerSynSet = largerLexicon.getSynSet(largerOR);
					if( largerSynSet != null ) { 
						extendedSingle = largerLexicon.extendSynSet( largerSynSet );
						extendedSingle.addAll(largerSynSet.getSynonyms());
					}
					else 
						extendedSingle = null;
					
					if( sourceIsLarger ) { sourceSet = largerSynSet; }
					
					for( int j = 0; j < smallerList.size(); j++ ) {
						Node smaller = smallerList.get(j);
						
						if( !this.isCancelled() ) {
							Mapping alignment = null;
							if( sourceIsLarger ) {
								alignment = alignTwoNodes(larger, smaller, typeOfNodes);
								matrix.set(i,j,alignment);
							}
							else {
								// source set needs to be set
								OntResource smallerOR = smaller.getResource().as(OntResource.class);
								sourceSet = smallerLexicon.getSynSet(smallerOR);
								
								alignment = alignTwoNodes(smaller, larger, typeOfNodes);
								matrix.set(j,i,alignment);
							}
							
							if( isProgressDisplayed() ) {
								stepDone(); // we have completed one step
								if( alignment != null && alignment.getSimilarity() >= param.threshold ) tentativealignments++; // keep track of possible alignments for progress display
							}
						}
					}
					if( isProgressDisplayed() ) { updateProgress(); }
					System.out.println(System.currentTimeMillis());
				}
			}
			else { // normal algorithm no SCS optimizations				
				for(int i = 0; i < sourceList.size(); i++) {
					Node source = sourceList.get(i);
					OntResource sourceOR = source.getResource().as(OntResource.class);
					sourceSet = sourceLexicon.getSynSet(sourceOR);
					
					for(int j = 0; j < targetList.size(); j++) {
						Node target = targetList.get(j);
						
						if( !this.isCancelled() ) { 
							Mapping alignment = alignTwoNodes(source, target, typeOfNodes);
						
							matrix.set(i,j,alignment);
							if( isProgressDisplayed() ) {
								stepDone(); // we have completed one step
								if( alignment != null && alignment.getSimilarity() >= param.threshold ) tentativealignments++; // keep track of possible alignments for progress display
							}
						}
						else { 
							return matrix; 
						}
						
					}
					if( isProgressDisplayed() ) updateProgress(); // update the progress dialog, to keep the user informed.
				}
			}
		

			return matrix;
    	}
		
	}
	
	
	/**
	 * TODO: Update method to deal with SCS optimizations. - Cosmin.
	 */
/*	@Override
	protected SimilarityMatrix alignUnmappedNodes(ArrayList<Node> sourceList,
			ArrayList<Node> targetList, SimilarityMatrix inputMatrix,
			Alignment<Mapping> inputAlignmentSet, alignType typeOfNodes)
			throws Exception {

		MappedNodes mappedNodes = new MappedNodes(sourceList, targetList, inputAlignmentSet, param.maxSourceAlign, param.maxTargetAlign);
    	SimilarityMatrix matrix = new ArraySimilarityMatrix(sourceList.size(), targetList.size(), typeOfNodes, relation);
		Node source;
		Node target;
		Mapping alignment; 
		Mapping inputAlignment;
		for(int i = 0; i < sourceList.size(); i++) {
			source = sourceList.get(i);
			for(int j = 0; j < targetList.size(); j++) {
				target = targetList.get(j);
				
				if( !this.isCancelled() ) {
					//if both nodes have not been mapped yet enough times
					//we map them regularly
					if(!mappedNodes.isSourceMapped(source) && !mappedNodes.isTargetMapped(target)){
						alignment = alignTwoNodes(source, target, typeOfNodes); 
					}
					//else we take the alignment that was computed from the previous matcher
					else{
						inputAlignment = inputMatrix.get(i, j);
						alignment = new Mapping(inputAlignment.getEntity1(), inputAlignment.getEntity2(), inputAlignment.getSimilarity(), inputAlignment.getRelation());
					}
					matrix.set(i,j,alignment);
					if( isProgressDisplayed() ) stepDone(); // we have completed one step
				}
				else { return matrix; }
			}
			if( isProgressDisplayed() ) updateProgress(); // update the progress dialog, to keep the user informed.
		}
		return matrix;

	}*/
/**
 * MATCHING WITH SYNONYMS
 */
	
	@Override
	protected Mapping alignTwoNodes(Node source, Node target,
			alignType typeOfNodes) throws Exception {

		
		
		OntResource targetOR = target.getResource().as(OntResource.class);
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
		
		if( ((LexicalSynonymMatcherParameters)param).useSynonymTerms ) { // using subconcept synonyms	
			
			List<String> sourceExtendedSynonyms, targetExtendedSynonyms;
			
			if( sourceIsLarger ) {
				sourceExtendedSynonyms = extendedSingle;
				targetExtendedSynonyms = extendedSynSets.get(target);
			} else {
				sourceExtendedSynonyms = extendedSynSets.get(source);
				targetExtendedSynonyms = extendedSingle;
			}
			
			if( sourceExtendedSynonyms == null || targetExtendedSynonyms == null || sourceExtendedSynonyms.isEmpty() || targetExtendedSynonyms.isEmpty() ) {
				// no extra synonyms.
				return null;
			}
			
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
