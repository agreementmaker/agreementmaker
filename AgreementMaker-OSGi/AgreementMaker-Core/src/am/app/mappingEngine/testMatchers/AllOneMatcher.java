package am.app.mappingEngine.testMatchers;

import am.api.alignment.AlignmentContext;
import am.api.matcher.Matcher;
import am.api.matcher.MatcherProperties;
import am.api.matcher.MatcherResult;

public class AllOneMatcher implements Matcher {
    @Override
    public MatcherProperties getProperties() {
        return new MatcherProperties.Builder()
                .setMinInputMatchers(0)
                .setMaxInputMatchers(0)
                .setName("AllOne Matcher")
                .setCategory(am.api.matcher.MatcherCategory.UTILITY)
                .build();
    }

    @Override
    public MatcherResult match(AlignmentContext task) {
        return new MatcherResult.Builder()
                .setClasses((sourceEntity, targetEntity) -> 1)
                .setProperties((sourceEntity, targetEntity) -> 1)
                .setInstances((sourceEntity, targetEntity) -> 1)
                .build();
    }
}
