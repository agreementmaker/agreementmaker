module am.matcher.oaei {
    exports am.matcher.oaei.oaei2009;
    exports am.matcher.oaei.oaei2010;
    exports am.matcher.oaei.oaei2011;

    requires java.desktop;
    requires am.core;
    requires am.matchers;
    requires am.app.similarity;
    requires am.matcher.bsm;
    requires am.matcher.asm;
    requires am.matcher.pra;
    requires jena.core;
}