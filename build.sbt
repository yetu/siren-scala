import scoverage.ScoverageSbtPlugin

import scalariform.formatter.preferences._
import com.typesafe.sbt.SbtScalariform.ScalariformKeys
import com.typesafe.sbt.SbtScalariform.scalariformSettings
import bintray.Keys._

name := "siren-scala"

organization := "com.yetu"

scalaVersion := "2.11.6"

crossScalaVersions := Seq("2.11.6", "2.10.5")

scalacOptions ++= Seq("-feature", "-unchecked", "-deprecation")

resolvers += "spray" at "http://repo.spray.io/"

resolvers += "Typesafe Repo" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies ++= Seq(
  "io.spray" %%  "spray-json" % "1.3.1" % "provided",
  "com.typesafe.play" %% "play-json" % "2.3.8" % "provided",
  "org.scalatest" %% "scalatest" % "2.2.4" % "test"
)

scalariformSettings ++ Seq(
    ScalariformKeys.preferences := ScalariformKeys.preferences.value
      .setPreference(AlignSingleLineCaseStatements, true)
      .setPreference(DoubleIndentClassDeclaration, true)
      .setPreference(PreserveDanglingCloseParenthesis, true)
      .setPreference(PreserveSpaceBeforeArguments, true)
      .setPreference(RewriteArrowSymbols, true)
)

CoverallsPlugin.coverallsSettings

ScoverageSbtPlugin.ScoverageKeys.coverageMinimum := 80

ScoverageSbtPlugin.ScoverageKeys.coverageFailOnMinimum := true

// settings for bintray publishing

bintrayPublishSettings

repository in bintray := "maven"

licenses += ("MIT", url("http://opensource.org/licenses/MIT"))

packageLabels in bintray := Seq("siren-scala", "yetu")

bintrayOrganization in bintray := Some("yetu")

publishMavenStyle := true

publishArtifact in Test := false
