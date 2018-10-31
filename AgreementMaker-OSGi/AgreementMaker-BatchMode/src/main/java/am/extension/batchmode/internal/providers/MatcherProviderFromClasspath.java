package am.extension.batchmode.internal.providers;

import am.app.mappingEngine.AbstractMatcher;
import am.extension.batchmode.api.BatchModeMatcherProvider;

public class MatcherProviderFromClasspath extends ProviderFromClasspath implements BatchModeMatcherProvider {
    public MatcherProviderFromClasspath(String canonicalClassName) {
        super(canonicalClassName);
    }

    @Override
    public AbstractMatcher getMatcher() {
        return (AbstractMatcher) super.getObject();
    }
}
