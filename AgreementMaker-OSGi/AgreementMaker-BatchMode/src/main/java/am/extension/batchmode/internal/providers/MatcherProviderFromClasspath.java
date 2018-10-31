package am.extension.batchmode.internal.providers;

import am.app.mappingEngine.AbstractMatcher;
import am.extension.batchmode.api.BatchModeMatcherProvider;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class MatcherProviderFromClasspath extends ProviderFromClasspath implements BatchModeMatcherProvider {
    public MatcherProviderFromClasspath(@JsonProperty("canonicalClassName") String canonicalClassName) {
        super(canonicalClassName);
    }

    @Override
    @JsonIgnore
    public AbstractMatcher getMatcher() {
        return (AbstractMatcher) super.getObject();
    }
}
