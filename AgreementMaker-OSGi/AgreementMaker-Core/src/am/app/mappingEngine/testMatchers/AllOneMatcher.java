package am.app.mappingEngine.testMatchers;

import am.api.matching.Matcher;
import am.api.matching.MatcherProperties;
import am.api.matching.MatcherResult;
import am.api.task.MatchingTask;
import am.app.mappingEngine.AbstractMatcher;

public class AllOneMatcher extends AbstractMatcher implements Matcher {
    @Override
    public MatcherProperties getProperties() {
        return new MatcherProperties.Builder()
                .setMinInputMatchers(0)
                .setMaxInputMatchers(0)
                .setName("AllOne Matcher")
                .setCategory(am.api.matching.MatcherCategory.UTILITY)
                .build();
    }

    @Override
    public MatcherResult match(MatchingTask task) {
        return new MatcherResult.Builder()
                .setClasses((sourceEntity, targetEntity) -> 1)
                .setProperties((sourceEntity, targetEntity) -> 1)
                .setInstances((sourceEntity, targetEntity) -> 1)
                .build();
    }
}
