package am.extension.batchmode.api;

import am.app.mappingEngine.MatchingAlgorithm;

public interface BatchModeMatcherProvider {
    public MatchingAlgorithm getMatcher();
}
