resolvers += Classpaths.sbtPluginReleases

// Plugin for scoverage
addSbtPlugin("org.scoverage" %% "sbt-scoverage" % "1.0.4")

// Plugin for publishing scoverage results to coveralls
addSbtPlugin("com.sksamuel.scoverage" %% "sbt-coveralls" % "0.0.5")

// Plugin for code formatting:
addSbtPlugin("com.typesafe.sbt" % "sbt-scalariform" % "1.3.0")

// Plugin for checking code style:
addSbtPlugin("org.scalastyle" %% "scalastyle-sbt-plugin" % "0.4.0")

// Plugin for releasing to sonatype nexus:
addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "0.2.1")

// Plugin for releasing signed artifacts with pgp:
addSbtPlugin("com.typesafe.sbt" % "sbt-pgp" % "0.8.3")
