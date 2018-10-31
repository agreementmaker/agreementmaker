module am.extension.batchmode {
    exports am.extension.batchmode.api;
    exports am.extension.batchmode.simpleBatchMode;
    exports am.extension.batchmode.conflictResolution;
    exports am.extension.batchmode.matchingTask;

    requires am.core;
    requires am.matcher.oaei;
    requires java.xml;
    requires java.xml.bind;
    requires log4j;
}