module am.matchers {
    exports am.matcher.parametricStringMatcher;

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