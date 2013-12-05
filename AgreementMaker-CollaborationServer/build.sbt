name := "AgreementMaker-CollaborationServer"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
  "edu.uic.cs.advis.am" % "AgreementMaker-Core" % "0.3.0-SNAPSHOT"
)

play.Project.playJavaSettings

