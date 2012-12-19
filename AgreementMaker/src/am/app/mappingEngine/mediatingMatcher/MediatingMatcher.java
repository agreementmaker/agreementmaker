package am.app.mappingEngine.mediatingMatcher;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import am.app.Core;
import am.app.lexicon.Lexicon;
import am.app.lexicon.LexiconBuilderParameters;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.AbstractMatcherParametersPanel;
import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.LexiconStore.LexiconRegistry;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.MatcherFactory;
import am.app.mappingEngine.MatchersRegistry;
import am.app.mappingEngine.SimilarityMatrix;
import am.app.mappingEngine.LexicalSynonymMatcher.LexicalSynonymMatcherParameters;
import am.app.mappingEngine.referenceAlignment.MatchingPair;
import am.app.mappingEngine.similarityMatrix.SparseMatrix;
import am.app.ontology.Node;
import am.app.ontology.Ontology;
import am.app.ontology.ontologyParser.OntoTreeBuilder;
import am.output.alignment.oaei.OAEIAlignmentFormat;
import am.userInterface.MatchingProgressDisplay;

public class MediatingMatcher extends AbstractMatcher {

	private static final long serialVersionUID = -4021061879846521596L;

	private Ontology mediatingOntology = null;
	private HashMap<String,List<MatchingPair>> sourceBridge;  // map a source URI to a list of matching pairs
	private HashMap<String,List<MatchingPair>> targetBridge;  // map a target URI to a list of matching pairs
	
	public MediatingMatcher() {
		super();
		
		needsParam = true;
		
		setName("Mediating Matcher");
		setCategory(MatcherCategory.LEXICAL);
	}
	
	@Override
	public AbstractMatcherParametersPanel getParametersPanel() {
		return new MediatingMatcherParametersPanel();
	}
	
	@Override
	protected void beforeAlignOperations() throws Exception {
		super.beforeAlignOperations();
		
		MediatingMatcherParameters p = (MediatingMatcherParameters) param;

		if( !(p.loadSourceBridge && p.loadTargetBridge) ) { // we need to compute one of the bridges, so load the mediating ontology 
			for( MatchingProgressDisplay mpd : progressDisplays ) mpd.appendToReport("Loading mediating ontology ...");
			try {
				mediatingOntology = OntoTreeBuilder.loadOWLOntology( p.mediatingOntology );
			} catch( Exception e ) {
				e.printStackTrace();
			}
			
			if( mediatingOntology == null ) {
				System.err.println("The mediating ontology (" + p.mediatingOntology + ") was not found.  Cannot match using a mediating ontology.");
				alignClass = false;
				alignProp = false;
				classesAlignmentSet = new Alignment<Mapping>(sourceOntology.getID(), targetOntology.getID());
				classesMatrix = new SparseMatrix(sourceOntology,targetOntology, alignType.aligningClasses);
				propertiesAlignmentSet = new Alignment<Mapping>(sourceOntology.getID(), targetOntology.getID());
				propertiesMatrix = new SparseMatrix(sourceOntology,targetOntology, alignType.aligningProperties);
				return;
			}
			
			for( MatchingProgressDisplay mpd : progressDisplays ) mpd.appendToReport(" Done.\n");
		}
		
		if( p.loadSourceBridge ) {
			try {
				for( MatchingProgressDisplay mpd : progressDisplays ) mpd.appendToReport("Loading source bridge ...");
				OAEIAlignmentFormat format = new OAEIAlignmentFormat();
				sourceBridge = format.readAlignment( new FileReader(new File(p.sourceBridge)) );
				for( MatchingProgressDisplay mpd : progressDisplays ) mpd.appendToReport(" Done.\n");
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			// the source bridge does not exist, we must create it.
			for( MatchingProgressDisplay mpd : progressDisplays ) mpd.appendToReport("Matching bridge ontology to source ontology ...");
			
			// build the lexicons for the bridge to source
			LexiconBuilderParameters lexParam = new LexiconBuilderParameters();
			lexParam.sourceOntology = mediatingOntology;
			lexParam.targetOntology = sourceOntology;
			
			// FIXME: This is a HACK! Fix it!
			lexParam.sourceOntology.setSourceOrTarget(Ontology.SOURCE);
			lexParam.targetOntology.setSourceOrTarget(Ontology.TARGET);
			
			lexParam.sourceUseLocalname = false;
			lexParam.targetUseLocalname = false;
			lexParam.sourceUseSCSLexicon = false;
			lexParam.targetUseSCSLexicon = false;
			
			List<String> synonymProperties = new ArrayList<String>();
			synonymProperties.add("label");
			synonymProperties.add("hasExactSynonym");
			//synonymProperties.add("hasRelatedSynonym");
			
			List<String> definitionProperties = new ArrayList<String>();
			
			lexParam.detectStandardProperties(lexParam.sourceOntology, synonymProperties, definitionProperties);
			
			synonymProperties = new ArrayList<String>();
			synonymProperties.add("label");
			//synonymProperties.add("hasExactSynonym");
			synonymProperties.add("synonym");
			
			lexParam.detectStandardProperties(lexParam.targetOntology, synonymProperties, definitionProperties);
			
			LexiconBuilderParameters oldParam = Core.getLexiconStore().getParameters();
			Core.getLexiconStore().setParameters(lexParam);

			Lexicon sourceOntLexicon = Core.getLexiconStore().build( LexiconRegistry.ONTOLOGY_LEXICON, lexParam.sourceOntology);
			Core.getLexiconStore().registerLexicon(sourceOntLexicon);
			Core.getLexiconStore().build(LexiconRegistry.WORDNET_LEXICON, lexParam.sourceOntology);  // don't need to save it since it augments the ontology lexicon
			//Core.getLexiconStore().unregisterLexicon(sourceOntLexicon);
			
			
			Lexicon targetOntLexicon = Core.getLexiconStore().build( LexiconRegistry.ONTOLOGY_LEXICON, lexParam.targetOntology);
			Core.getLexiconStore().registerLexicon(targetOntLexicon);
			Core.getLexiconStore().build(LexiconRegistry.WORDNET_LEXICON, lexParam.targetOntology);  // don't need to save it since it augments the ontology lexicon
			//Core.getLexiconStore().unregisterLexicon(targetOntLexicon);
			
			AbstractMatcher lsm = MatcherFactory.getMatcherInstance(MatchersRegistry.LSM, 0); 
			
			LexicalSynonymMatcherParameters lsmParam = new LexicalSynonymMatcherParameters(getParam().threshold, getMaxSourceAlign(), getMaxTargetAlign());
			lsmParam.sourceLexicon = sourceOntLexicon;
			lsmParam.targetLexicon = targetOntLexicon;
			lsmParam.useSynonymTerms = false;
			
			lsm.setParam(lsmParam);
			lsm.setSourceOntology(lexParam.sourceOntology);
			lsm.setTargetOntology(lexParam.targetOntology);
			lsm.match();
			
			sourceBridge = OAEIAlignmentFormat.convertAlignment(lsm.getAlignment());
			
			Core.getLexiconStore().setParameters(oldParam); // restore the old parameters
			
			for( MatchingProgressDisplay mpd : progressDisplays ) mpd.appendToReport(" Done.\n");
		}
		
		if( p.loadTargetBridge ) {
			for( MatchingProgressDisplay mpd : progressDisplays ) mpd.appendToReport("Loading target bridge ...");
			OAEIAlignmentFormat format = new OAEIAlignmentFormat();
			targetBridge = format.readAlignment( new FileReader(new File(p.targetBridge)) );
			for( MatchingProgressDisplay mpd : progressDisplays ) mpd.appendToReport(" Done.\n");
		} else {
			// the target bridge does not exist, we must create it.
			for( MatchingProgressDisplay mpd : progressDisplays ) mpd.appendToReport("Matching bridge ontology to target ontology ...");
			
			// build the lexicons for the bridge to source
			LexiconBuilderParameters lexParam = new LexiconBuilderParameters();
			lexParam.sourceOntology = mediatingOntology;
			lexParam.targetOntology = targetOntology;
			
			// FIXME: This is a HACK! Fix it!
			lexParam.sourceOntology.setSourceOrTarget(Ontology.SOURCE);
			lexParam.targetOntology.setSourceOrTarget(Ontology.TARGET);
			
			lexParam.sourceUseLocalname = false;
			lexParam.targetUseLocalname = false;
			lexParam.sourceUseSCSLexicon = false;
			lexParam.targetUseSCSLexicon = false;
			
			List<String> synonymProperties = new ArrayList<String>();
			synonymProperties.add("label");
			synonymProperties.add("hasExactSynonym");
			//synonymProperties.add("hasRelatedSynonym");
			
			List<String> definitionProperties = new ArrayList<String>();
			
			lexParam.detectStandardProperties(lexParam.sourceOntology, synonymProperties, definitionProperties);
			
			synonymProperties = new ArrayList<String>();
			synonymProperties.add("label");
			//synonymProperties.add("hasExactSynonym");
			synonymProperties.add("synonym");
			
			lexParam.detectStandardProperties(lexParam.targetOntology, synonymProperties, definitionProperties);
			
			LexiconBuilderParameters oldParam = Core.getLexiconStore().getParameters();
			Core.getLexiconStore().setParameters(lexParam);

			Lexicon sourceOntLexicon = Core.getLexiconStore().build( LexiconRegistry.ONTOLOGY_LEXICON, lexParam.sourceOntology);
			Core.getLexiconStore().registerLexicon(sourceOntLexicon);
			Core.getLexiconStore().build(LexiconRegistry.WORDNET_LEXICON, lexParam.sourceOntology);  // don't need to save it since it augments the ontology lexicon
			//Core.getLexiconStore().unregisterLexicon(sourceOntLexicon);
			
			Lexicon targetOntLexicon = Core.getLexiconStore().build( LexiconRegistry.ONTOLOGY_LEXICON, lexParam.targetOntology);
			Core.getLexiconStore().registerLexicon(targetOntLexicon);
			Core.getLexiconStore().build(LexiconRegistry.WORDNET_LEXICON, lexParam.targetOntology);  // don't need to save it since it augments the ontology lexicon
			//Core.getLexiconStore().unregisterLexicon(targetOntLexicon);
			
			
			AbstractMatcher lsm = MatcherFactory.getMatcherInstance(MatchersRegistry.LSM, 0);
					
			LexicalSynonymMatcherParameters lsmParam = new LexicalSynonymMatcherParameters(getParam().threshold, getMaxSourceAlign(), getMaxTargetAlign());
			lsmParam.sourceLexicon = sourceOntLexicon;
			lsmParam.targetLexicon = targetOntLexicon;
			lsmParam.useSynonymTerms = false;
			
			lsm.setParam(lsmParam);
			lsm.setSourceOntology(lexParam.sourceOntology);
			lsm.setTargetOntology(lexParam.targetOntology);
			lsm.match();
			
			targetBridge = OAEIAlignmentFormat.convertAlignment(lsm.getAlignment());
			
			Core.getLexiconStore().setParameters(oldParam);  // restore the old parameters
			
			for( MatchingProgressDisplay mpd : progressDisplays ) mpd.appendToReport(" Done.\n");
		}
		
		
		// FIXME: This is a HACK! Fix it!
		sourceOntology.setSourceOrTarget(Ontology.SOURCE);
		targetOntology.setSourceOrTarget(Ontology.TARGET);
		
	};
	
	@Override
	protected Mapping alignTwoNodes(Node source, Node target,
			alignType typeOfNodes, SimilarityMatrix matrix) throws Exception {
		
		
		List<MatchingPair> sourceBridgeMapping = sourceBridge.get(source.getUri());
		List<MatchingPair> targetBridgeMapping = targetBridge.get(target.getUri());
		
		/*for( Entry<String,List<MatchingPair>> currentEntry : sourceBridge.entrySet() ) {
			System.out.println("Source:" + currentEntry.getKey());
		}
		
		for( Entry<String,List<MatchingPair>> currentEntry : targetBridge.entrySet() ) {
			System.out.println("Target:" + currentEntry.getKey());
		}*/
		
		if( sourceBridgeMapping == null || targetBridgeMapping == null ) { return null; }
		
		for( MatchingPair sourcePair : sourceBridgeMapping ) {
			for( MatchingPair targetPair : targetBridgeMapping ) {
				if( sourcePair.sourceURI.equals(targetPair.sourceURI) ) {
					return new Mapping(source, target, sourcePair.similarity * targetPair.similarity);
				}
			}
		}
		
		
		return null;
	}
	
}
