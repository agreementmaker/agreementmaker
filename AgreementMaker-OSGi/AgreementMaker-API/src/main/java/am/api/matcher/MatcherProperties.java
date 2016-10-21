package am.api.matcher;

import org.inferred.freebuilder.FreeBuilder;

@FreeBuilder
public interface MatcherProperties {
    /**
     * The minimum number of input matchers that a matcher can accept.
     * Can be used to force input matchers to be configured.
     * Value should not be greater than {@link #getMaxInputMatchers()}.
     *
     * @return Value from 0 to {@link Integer#MAX_VALUE}.
     */
    int getMinInputMatchers();

    /**
     * The maximum number of input matchers that a matcher can accept.
     * Can be used to force no input matchers.
     * Value should not be less than {@link #getMinInputMatchers()}.
     *
     * @return Value from 0 to {@link Integer#MAX_VALUE}.
     */
    int getMaxInputMatchers();

    /**
     * Used to categorize matchers in the UI.
     */
    MatcherCategory getCategory();

    /**
     * @return The display name of this matcher.
     */
    String getName();

    class Builder extends MatcherProperties_Builder {}
}
