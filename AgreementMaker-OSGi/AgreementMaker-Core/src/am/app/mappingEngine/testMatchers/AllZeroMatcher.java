package am.app.mappingEngine.testMatchers;

import am.api.matching.Matcher;
import am.api.matching.MatcherProperties;
import am.api.matching.MatcherResult;
import am.api.task.MatchingTask;
import am.app.mappingEngine.AbstractMatcher;

public class AllZeroMatcher extends AbstractMatcher implements Matcher {
    @Override
    public MatcherProperties getProperties() {
        return new MatcherProperties.Builder()
                .setMinInputMatchers(0)
                .setMaxInputMatchers(Integer.MAX_VALUE)
                .setCategory(am.api.matching.MatcherCategory.UTILITY)
                .setName("AllZero Matcher")
                .build();
    }

    @Override
    public MatcherResult match(MatchingTask task) {
        return new MatcherResult.Builder()
                .setClasses((sourceEntity, targetEntity) -> 0)
                .setProperties((sourceEntity, targetEntity) -> 0)
                .setInstances((sourceEntity, targetEntity) -> 0)
                .build();
    }
}
