package am.app.mappingEngine.testMatchers;

import am.api.alignment.AlignmentContext;
import am.api.matcher.Matcher;
import am.api.matcher.MatcherProperties;
import am.api.matcher.MatcherResult;

public class AllZeroMatcher implements Matcher {
    @Override
    public MatcherProperties getProperties() {
        return new MatcherProperties.Builder()
                .setMinInputMatchers(0)
                .setMaxInputMatchers(Integer.MAX_VALUE)
                .setCategory(am.api.matcher.MatcherCategory.UTILITY)
                .setName("AllZero Matcher")
                .build();
    }

    @Override
    public MatcherResult match(AlignmentContext task) {
        return new MatcherResult.Builder()
                .setClasses((sourceEntity, targetEntity) -> 0)
                .setProperties((sourceEntity, targetEntity) -> 0)
                .setInstances((sourceEntity, targetEntity) -> 0)
                .build();
    }
}
