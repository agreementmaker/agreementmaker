module am.core {
    requires java.desktop;
    requires java.sql;
    requires java.prefs;

    requires jena.core;
    requires jena.iri;
    requires jena.sdb;
    requires jena.tdb;
    requires jena.arq;

    requires log4j;
    requires dom4j;
    requires weka.stable;
    requires org.semanticweb.hermit;
    requires owlapi.distribution;
    requires jwnl;
    requires httpcore;
    requires httpclient.osgi;
    requires commons.io;
    requires commons.lang;
    requires commons.compress;
    requires jopt.simple;
    requires secondstring;
    requires jackson.core;
    requires jackson.databind;
    requires jackson.annotations;
    requires colt;
    requires jaws;
    requires sesame.repository.api;
    requires sesame.repository.sail;
    requires sesame.model;
    requires sesame.rio.api;
    requires sesame.sail.memory;
    requires cluster.gvm;

    requires am.api;
    requires am.app.similarity;
    requires am.app.wordnet;
}