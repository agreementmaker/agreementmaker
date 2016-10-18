package am.api.alignment;

import am.api.matching.MatcherResult;

import java.util.concurrent.Future;

public interface Selector {
    Future<SelectorResult> select(MatcherResult matchingResult);
}
