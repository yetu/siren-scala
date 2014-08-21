# Description

[![Build Status](https://travis-ci.org/yetu/siren-scala.svg?branch=master)](https://travis-ci.org/yetu/siren-scala)
[![Coverage Status](https://img.shields.io/coveralls/yetu/siren-scala.svg)](https://coveralls.io/r/yetu/siren-scala?branch=master)

A Scala library for producing Siren entities from your domain model objects and serializing them to 
`application/vnd.siren+json`. See the [Siren Spec](http://sirenspec.org)

This library is still very much a work-in-progress, so expect its API to change.

Currently the library supports Scala 2.10 and 2.11 versions.

#Setup

In order to use _siren-scala_ library you must add resolver to _sonatype_ server and library dependency.

##Resolvers

    resolvers += "Sonatype Releases" at "https://oss.sonatype.org/content/repositories/releases/"
    
##Library dependency

    libraryDependencies += "com.yetu" %% "siren-scala" % "0.2.1"

#Usage

_siren-scala_ provides a rich model of the types described in the Siren specification. The top-level
type is `Entity.RootEntity`, which represents a Siren root entity. 

The whole Siren model available with the following import: 

    import com.yetu.siren.model._

Moreover, the library provides serialization of Siren root entities to JSON using Spray-JSON:

    import com.yetu.siren.json.sprayjson.SirenJsonProtocol._
    import spray.json._
    
    val rootEntity: Entity.RootEntity = ...
    rootEntity.toJson

In order to enable you to create Siren representations for your resources, _siren-scala_ provides a
type class, `SirenRootEntityWriter`. Provide an instance of this type class for your case class, and 
you will be able to easily convert instances of that case class to Siren root entities:
 
    import com.yetu.siren
    import siren.Siren
    
    case class Person(name: String, age: Int)
    
    implicit val personSirenWriter = new SirenRootEntityWriter[Person] {
      override def toSiren(order: Order)(implicit ctx: SirenContext) = {
        ???      
      }
    }
    
    val personEntity: Entity.RootEntity = Siren.asRootEntity(Person("Bob", 31))

For a complete usage example, please see the [ExampleSpec.scala](src/test/scala/com/yetu/siren/ExampleSpec.scala).

# Contributors

Boris Malenšek, Daniel Westheide