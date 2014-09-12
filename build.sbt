import scalariform.formatter.preferences._
import com.typesafe.sbt.SbtScalariform.ScalariformKeys
import com.typesafe.sbt.SbtScalariform.scalariformSettings
import xerial.sbt.Sonatype.SonatypeKeys._
import CoverallsPlugin.CoverallsKeys._

sonatypeSettings

name := "siren-scala"

organization := "com.yetu"

scalaVersion := "2.11.2"

crossScalaVersions := Seq("2.11.2", "2.10.4")

scalacOptions ++= Seq("-feature", "-unchecked", "-deprecation")

resolvers += "spray" at "http://repo.spray.io/"

resolvers += "Typesafe Repo" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies ++= Seq(
  "org.scalaz" %% "scalaz-core" % "7.1.0",
  "io.spray" %%  "spray-json" % "1.2.6" % "provided",
  "com.typesafe.play" %% "play-json" % "2.3.4" % "provided",
  "org.scalatest" %% "scalatest" % "2.2.1" % "test"
)

scalariformSettings ++ Seq(
    ScalariformKeys.preferences := ScalariformKeys.preferences.value
      .setPreference(AlignSingleLineCaseStatements, true)
      .setPreference(DoubleIndentClassDeclaration, true)
      .setPreference(PreserveDanglingCloseParenthesis, true)
      .setPreference(PreserveSpaceBeforeArguments, true)
      .setPreference(RewriteArrowSymbols, true)
)

instrumentSettings

CoverallsPlugin.coverallsSettings

coverallsTokenFile := ".coveralls.token"

ScoverageKeys.minimumCoverage := 80

ScoverageKeys.failOnMinimumCoverage := true

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
    </developers>)