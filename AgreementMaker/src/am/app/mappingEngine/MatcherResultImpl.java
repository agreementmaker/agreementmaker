package am.app.mappingEngine;

import java.util.List;

import am.app.mappingEngine.referenceAlignment.MatchingPair;

public class MatcherResultImpl implements MatcherResult {

	private int sourceOntologyID;
	private int targetOntologyID;
	private Alignment<Mapping> classesAlignment;
	private Alignment<Mapping> propertiesAlignment;
	private List<MatchingPair> instancesAlignment;

	public MatcherResultImpl(Alignment<Mapping> classesAlignment, 
			Alignment<Mapping> propertiesAlignment,
			List<MatchingPair> instancesAlignment ) {
		this.classesAlignment = classesAlignment;
		this.propertiesAlignment = propertiesAlignment;
		this.instancesAlignment = instancesAlignment;
		
		sourceOntologyID = classesAlignment.getSourceOntologyID();
		targetOntologyID = classesAlignment.getTargetOntologyID();
	}
	
	@Override
	public Alignment<Mapping> getAlignment() {
		Alignment<Mapping> mergedAlignment = new Alignment<Mapping>(sourceOntologyID, targetOntologyID);
		mergedAlignment.addAll(classesAlignment);
		mergedAlignment.addAll(propertiesAlignment);
		//mergedAlignment.addAll(instancesAlignment);
		return mergedAlignment;
	}

	@Override
	public Alignment<Mapping> getClassAlignmentSet() {
		return classesAlignment;
	}

	@Override
	public Alignment<Mapping> getPropertyAlignmentSet() {
		return propertiesAlignment;
	}

	@Override
	public List<MatchingPair> getInstanceAlignmentSet() {
		return instancesAlignment;
	}

}
