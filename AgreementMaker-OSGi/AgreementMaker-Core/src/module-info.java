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

    exports am;
    exports am.app;
    exports am.app.mappingEngine;
    exports am.app.mappingEngine.manualMatcher;
    exports am.app.mappingEngine.oneToOneSelection;
    exports am.app.mappingEngine.qualityEvaluation;
    exports am.app.mappingEngine.referenceAlignment;
    exports am.app.mappingEngine.StringUtil;
    exports am.app.mappingEngine.similarityMatrix;
    exports am.app.mappingEngine.utility;
    exports am.app.lexicon;
    exports am.app.lexicon.subconcept;
    exports am.app.ontology;
    exports am.app.ontology.instance;
    exports am.app.ontology.instance.endpoint;
    exports am.app.ontology.profiling;
    exports am.app.ontology.profiling.manual;
    exports am.app.ontology.profiling.metrics;
    exports am.app.triplestore.jenatdb;
    exports am.app.osgi;
    exports am.utility;
    exports am.utility.numeric;
    exports am.utility.messagesending;
    exports am.parsing;
    exports am.app.ontology.hierarchy;
    exports am.app.ontology.ontologyParser;
    exports am.tools.LexiconLookup;
    exports am.tools.seals;
    exports am.tools.ThresholdAnalysis;
    exports am.evaluation.clustering;
    exports am.evaluation.clustering.localByThreshold;
    exports am.evaluation.clustering.gvm;
    exports am.evaluation.disagreement;
    exports am.evaluation.disagreement.variance;
    exports am.output.console;
    exports am.output.alignment.oaei;
    exports am.app.mappingEngine.abstractMatcherNew;
}