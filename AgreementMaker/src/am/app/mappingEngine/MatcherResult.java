package am.app.mappingEngine;

import java.util.List;

import am.app.mappingEngine.referenceAlignment.MatchingPair;

public interface MatcherResult {
	
	public Alignment<Mapping> getAlignment();

    public Alignment<Mapping> getClassAlignmentSet();

    public Alignment<Mapping> getPropertyAlignmentSet();

    public List<MatchingPair> getInstanceAlignmentSet();

}
