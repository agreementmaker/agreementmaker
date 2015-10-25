#!/bin/bash -e

MVN_REPO=$HOME/.m2/repository
GROUP_ID=$MVN_REPO/edu/uic/cs/advis/am

pax-run.sh --clean \
--org.ops4j.pax.runner.platform.workingDirectory="target/runner" \
--org.ops4j.pax.runner.platform.vmOptions="-agentlib:jdwp=transport=dt_socket,server=y,address=8778,suspend=n" \
file:$GROUP_ID/AgreementMaker-API/0.0.1-SNAPSHOT/AgreementMaker-API-0.0.1-SNAPSHOT.jar \
file:$GROUP_ID/AgreementMaker-Core/0.3.0-SNAPSHOT/AgreementMaker-Core-0.3.0-SNAPSHOT.jar \
file:$GROUP_ID/AgreementMaker-UI/1.0.0-SNAPSHOT/AgreementMaker-UI-1.0.0-SNAPSHOT.jar \
file:$GROUP_ID/AgreementMaker-UserFeedback/1.0.0-SNAPSHOT/AgreementMaker-UserFeedback-1.0.0-SNAPSHOT.jar \
file:$GROUP_ID/Matcher-BaseSimilarity/1.0.0-SNAPSHOT/Matcher-BaseSimilarity-1.0.0-SNAPSHOT.jar \
file:$GROUP_ID/Matcher-IMEI2013/1.0.0-SNAPSHOT/Matcher-IMEI2013-1.0.0-SNAPSHOT.jar \
file:$GROUP_ID/Matcher-LinkedOpenData/1.0.0-SNAPSHOT/Matcher-LinkedOpenData-1.0.0-SNAPSHOT.jar \
file:$GROUP_ID/Matcher-OAEI/1.0.0-SNAPSHOT/Matcher-OAEI-1.0.0-SNAPSHOT.jar \
file:$GROUP_ID/Matcher-AdvancedSimilarity/1.0.0-SNAPSHOT/Matcher-AdvancedSimilarity-1.0.0-SNAPSHOT.jar \
file:$GROUP_ID/Matcher-PRA/1.0.0-SNAPSHOT/Matcher-PRA-1.0.0-SNAPSHOT.jar \
file:$GROUP_ID/AgreementMaker-BatchMode/1.0.0-SNAPSHOT/AgreementMaker-BatchMode-1.0.0-SNAPSHOT.jar \
file:$GROUP_ID/AgreementMaker-Matchers/1.0.0-SNAPSHOT/AgreementMaker-Matchers-1.0.0-SNAPSHOT.jar \
file:$GROUP_ID/AgreementMaker-UIGlue/1.0.0-SNAPSHOT/AgreementMaker-UIGlue-1.0.0-SNAPSHOT.jar \
file:$GROUP_ID/AgreementMaker-CollaborationClient/1.0.0-SNAPSHOT/AgreementMaker-CollaborationClient-1.0.0-SNAPSHOT.jar

