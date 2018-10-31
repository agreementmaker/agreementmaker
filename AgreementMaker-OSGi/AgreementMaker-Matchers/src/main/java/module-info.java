module am.matchers {
    exports am.matcher.parametricStringMatcher;
    exports am.matcher.Combination;
    exports am.matcher.IterativeInstanceStructuralMatcher;
    exports am.matcher.LexicalSynonymMatcher;
    exports am.matcher.groupFinder;
    exports am.matcher.multiWords;
    exports am.matcher.FilterMatcher;
    exports am.matcher.mediatingMatcher;
    exports am.matcher.boosting;
    exports am.matcher.LexicalMatcherJWNL;
    exports am.matcher.dsi;
    exports am.matcher.ssc;

    requires java.desktop;
    requires java.prefs;
    requires java.sql;
    requires am.core;

    requires jena.core;
    requires jwnl;
    requires am.app.similarity;
    requires am.app.wordnet;

    requires simmetrics;
    requires simpack;
    requires secondstring;
}