package am.api.alignment;

import am.api.selector.SelectorResult;
import am.api.matcher.MatcherResult;

public interface AlignmentResult {
    AlignmentContext getContext();
    MatcherResult getMatcherResult();
    SelectorResult getSelectorResult();
}
