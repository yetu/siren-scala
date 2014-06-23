// Plugin for scoverage
addSbtPlugin("com.sksamuel.scoverage" %% "sbt-scoverage" % "0.95.7")

// Plugin for code formatting:
addSbtPlugin("com.typesafe.sbt" % "sbt-scalariform" % "1.3.0")

// Plugin for checking code style:
addSbtPlugin("org.scalastyle" %% "scalastyle-sbt-plugin" % "0.4.0")

// Plugin for releasing to sonatype nexus:
addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "0.2.1")

// Plugin for releasing signed artifacts with pgp:
addSbtPlugin("com.typesafe.sbt" % "sbt-pgp" % "0.8.3")