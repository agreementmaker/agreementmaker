module am.extension.collaborationclient {
    exports am.extension.collaborationClient;
    exports am.extension.collaborationClient.api;
    exports am.extension.collaborationClient.restful;

    requires java.desktop;
    requires am.core;
    requires am.matcher.oaei;
    requires log4j;
    requires com.fasterxml.jackson.databind;
}