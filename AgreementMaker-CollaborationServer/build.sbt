name := "AgreementMaker-CollaborationServer"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
  "edu.uic.cs.advis.am" % "AgreementMaker-Core" % "0.3.0-SNAPSHOT",
  "edu.uic.cs.advis.am" % "AgreementMaker-Matchers" % "1.0.0-SNAPSHOT",
  "edu.uic.cs.advis.am" % "AgreementMaker-UserFeedback" % "1.0.0-SNAPSHOT",
  "edu.uic.cs.advis.am" % "Matcher-AdvancedSimilarity" % "1.0.0-SNAPSHOT",
  "edu.uic.cs.advis.am" % "Matcher-BaseSimilarity" % "1.0.0-SNAPSHOT",
  "edu.uic.cs.advis.am" % "Matcher-OAEI" % "1.0.0-SNAPSHOT",
  "edu.uic.cs.advis.am" % "Matcher-PRA" % "1.0.0-SNAPSHOT",
  "edu.uic.cs.advis.am" % "AgreementMaker-CollaborationClient" % "1.0.0-SNAPSHOT",
  "org.osgi" % "org.osgi.core" % "4.3.1",
  "org.osgi" % "org.osgi.compendium" % "4.3.1"  
  )

play.Project.playJavaSettings