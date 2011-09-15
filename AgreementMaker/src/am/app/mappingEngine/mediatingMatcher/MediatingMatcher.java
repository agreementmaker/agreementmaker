package am.app.mappingEngine.mediatingMatcher;

import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.List;

import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.AbstractMatcherParametersPanel;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.referenceAlignment.MatchingPair;
import am.app.ontology.Node;
import am.app.ontology.Ontology;
import am.output.alignment.oaei.OAEIAlignmentFormat;

public class MediatingMatcher extends AbstractMatcher {

	private static final long serialVersionUID = -4021061879846521596L;

	private Ontology mediatingOntology;
	private HashMap<String,List<MatchingPair>> sourceBridge;
	private HashMap<String,List<MatchingPair>> targetBridge;
	
	public MediatingMatcher() {
		super();
		
		needsParam = true;
	}
	
	@Override
	public AbstractMatcherParametersPanel getParametersPanel() {
		return new MediatingMatcherParametersPanel();
	}
	
	@Override
	protected void beforeAlignOperations() throws Exception {
		super.beforeAlignOperations();
		
		MediatingMatcherParameters p = (MediatingMatcherParameters) param;
		
		progressDisplay.appendToReport("Loading mediating ontology ...");
		//mediatingOntology = OntoTreeBuilder.loadOWLOntology( p.mediatingOntology );
		progressDisplay.appendToReport(" Done.\n");
		
		if( p.loadSourceBridge ) {
			try {
				progressDisplay.appendToReport("Loading source bridge ...");
				OAEIAlignmentFormat format = new OAEIAlignmentFormat();
				sourceBridge = format.readAlignment( new FileReader(new File(p.sourceBridge)) );
				progressDisplay.appendToReport(" Done.\n");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		if( p.loadTargetBridge ) {
			progressDisplay.appendToReport("Loading target bridge ...");
			OAEIAlignmentFormat format = new OAEIAlignmentFormat();
			targetBridge = format.readAlignment( new FileReader(new File(p.targetBridge)) );
			progressDisplay.appendToReport(" Done.\n");
		}
		
	};
	
	@Override
	protected Mapping alignTwoNodes(Node source, Node target,
			alignType typeOfNodes) throws Exception {
		
		
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
					return new Mapping(source, target, 0.85d);
				}
			}
		}
		
		
		return null;
	}
	
}
