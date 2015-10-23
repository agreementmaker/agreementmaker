package am.extension.batchmode.internal.providers;

import am.app.mappingEngine.MatchingAlgorithm;
import am.extension.batchmode.api.BatchModeMatcherProvider;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class MatcherProviderFromClasspath extends ProviderFromClasspath implements BatchModeMatcherProvider {
    public MatcherProviderFromClasspath(String canonicalClassName) {
        super(canonicalClassName);
    }

    @Override
    @JsonIgnore
    public MatchingAlgorithm getMatcher() {
        return (MatchingAlgorithm) super.getObject();
    }
}
