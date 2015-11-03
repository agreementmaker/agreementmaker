package am.extension.batchmode.api;

import am.app.mappingEngine.AbstractMatcher;
import am.extension.batchmode.internal.providers.MatcherProviderFromClasspath;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(as = MatcherProviderFromClasspath.class)
public interface BatchModeMatcherProvider {
    public AbstractMatcher getMatcher();
}
