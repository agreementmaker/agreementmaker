package am.api.selector;

import am.api.matcher.MatcherResult;
import am.api.alignment.AlignmentContext;

public interface Selector {
    SelectorResult select(AlignmentContext task, MatcherResult matchingResult);
}
