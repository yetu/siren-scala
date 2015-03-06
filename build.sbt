import scalariform.formatter.preferences._
import com.typesafe.sbt.SbtScalariform.ScalariformKeys
import com.typesafe.sbt.SbtScalariform.scalariformSettings
import xerial.sbt.Sonatype.SonatypeKeys._
import CoverallsPlugin.CoverallsKeys._

sonatypeSettings

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

coverallsTokenFile := ".coveralls.token"

ScoverageSbtPlugin.ScoverageKeys.coverageMinimum := 80

ScoverageSbtPlugin.ScoverageKeys.coverageFailOnMinimum := true

publishMavenStyle := true

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}

publishArtifact in Test := false

pomExtra := (
  <url>https://github.com/yetu/siren-scala</url>
    <licenses>
      <license>
        <name>MIT</name>
        <url>http://www.opensource.org/licenses/MIT</url>
        <distribution>repo</distribution>
      </license>
    </licenses>
    <scm>
      <url>git@github.com:yetu/siren-scala.git</url>
      <connection>scm:git:git@github.com:yetu/siren-scala.git</connection>
    </scm>
    <developers>
      <developer>
        <id>zmeda</id>
        <name>Boris Malenšek</name>
        <url>https://github.com/zmeda</url>
      </developer>
      <developer>
        <id>dwestheide</id>
        <name>Daniel Westheide</name>
        <url>http://danielwestheide.com/</url>
      </developer>
    </developers>)
