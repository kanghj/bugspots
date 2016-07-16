name := "bugspots"

version := "1.0"

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  // TODO fix version numbers to fix conflicting versions warning
  "org.eclipse.jgit" % "org.eclipse.jgit" % "4.4.0.201606070830-r",
  "org.scalatest" % "scalatest_2.11" % "2.2.4" % "test",
  "junit" % "junit" % "4.10" % "test"

)