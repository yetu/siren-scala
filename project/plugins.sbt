resolvers += Classpaths.sbtPluginReleases

resolvers += Resolver.url(
"bintray-sbt-plugin-releases",
    url("http://dl.bintray.com/content/sbt/sbt-plugin-releases"))(
      Resolver.ivyStylePatterns)

// Plugin for bintray releasing:
addSbtPlugin("me.lessis" % "bintray-sbt" % "0.2.0")

// Plugin for scoverage:
addSbtPlugin("org.scoverage" %% "sbt-scoverage" % "1.0.4")

// Plugin for publishing scoverage results to coveralls:
addSbtPlugin("com.sksamuel.scoverage" %% "sbt-coveralls" % "0.0.5")

// Plugin for code formatting:
addSbtPlugin("com.typesafe.sbt" % "sbt-scalariform" % "1.3.0")

// Plugin for checking code style:
addSbtPlugin("org.scalastyle" %% "scalastyle-sbt-plugin" % "0.4.0")
