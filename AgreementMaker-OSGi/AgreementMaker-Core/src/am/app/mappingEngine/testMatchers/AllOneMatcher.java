package am.app.mappingEngine.testMatchers;

import am.api.matching.Matcher;
import am.api.matching.MatcherProperties;
import am.api.matching.MatcherResult;
import am.api.task.MatchingTask;
import am.app.mappingEngine.AbstractMatcher;
import am.ds.matching.MatcherResultImpl;

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
        return new MatcherResultImpl(
                (sourceEntity, targetEntity) -> 1,
                (sourceEntity, targetEntity) -> 1,
                (sourceEntity, targetEntity) -> 1);
    }
}
