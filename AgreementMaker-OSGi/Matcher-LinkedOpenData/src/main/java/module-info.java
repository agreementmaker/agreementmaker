module am.matcher.lod {
    exports am.matcher.lod.LinkedOpenData;
    exports am.matcher.lod.hierarchy;
    exports am.matcher.lod.instanceMatcher;
    exports am.matcher.lod.instanceMatchers;
    exports am.matcher.lod.instanceMatchers.combination;
    exports am.matcher.lod.instanceMatchers.genericInstanceMatcher;
    exports am.matcher.lod.instanceMatchers.labelInstanceMatcher;
    exports am.matcher.lod.instanceMatchers.statementsInstanceMatcher;
    exports am.matcher.lod.instanceMatchers.tokenInstanceMatcher;
    exports am.matcher.lod.wikipedia;

    requires am.core;
    requires am.ui;
    requires am.app.similarity;
    requires am.matcher.oaei;
    requires am.matcher.asm;
    requires log4j;
    requires simpack;
    requires jena.core;
    requires secondstring;
    requires jaws;
    requires jackson.core;
    requires jackson.databind;
    requires java.xml;
    requires am.app.wordnet;
}