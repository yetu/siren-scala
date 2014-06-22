import scalariform.formatter.preferences._
import com.typesafe.sbt.SbtScalariform.ScalariformKeys
import com.typesafe.sbt.SbtScalariform.scalariformSettings

name := "siren-scala"

organization := "com.yetu"

scalaVersion := "2.10.4"

scalacOptions += "-feature"

libraryDependencies ++= Seq(
  "com.chuusai" %% "shapeless" % "1.2.4",
  "org.scalaz" %% "scalaz-core" % "7.0.5",
  "io.spray" %%  "spray-json" % "1.2.5",
  "org.scalatest" %% "scalatest" % "2.0" % "test"
)

scalariformSettings ++ Seq(
    ScalariformKeys.preferences := ScalariformKeys.preferences.value
      .setPreference(AlignSingleLineCaseStatements, true)
      .setPreference(DoubleIndentClassDeclaration, true)
      .setPreference(PreserveDanglingCloseParenthesis, true)
      .setPreference(PreserveSpaceBeforeArguments, true)
      .setPreference(RewriteArrowSymbols, true)
)

ScoverageSbtPlugin.instrumentSettings