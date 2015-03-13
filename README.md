# Description

[![Build Status](https://travis-ci.org/yetu/siren-scala.svg?branch=master)](https://travis-ci.org/yetu/siren-scala)
[![Coverage Status](https://img.shields.io/coveralls/yetu/siren-scala.svg)](https://coveralls.io/r/yetu/siren-scala?branch=master)
[ ![Download](https://api.bintray.com/packages/yetu/maven/siren-scala/images/download.svg) ](https://bintray.com/yetu/maven/siren-scala/_latestVersion)

A Scala library for producing Siren entities from your domain model objects and serializing them to 
`application/vnd.siren+json`. See the [Siren Spec](http://sirenspec.org)

This library is still very much a work-in-progress, so expect its API to change.

Currently the library supports Scala 2.10 and 2.11 versions.

#Setup

In order to use _siren-scala_ library you must add resolver to _bintray_ repository and library dependency.

##Resolvers

    resolvers += "yetu Bintray Repo" at "http://dl.bintray.com/yetu/maven/"
    
##Library dependency

    libraryDependencies += "com.yetu" %% "siren-scala" % "0.5.0"

#Usage

_siren-scala_ provides a rich model of the types described in the Siren specification. The top-level
type is `Entity.RootEntity`, which represents a Siren root entity. 

The whole Siren model available with the following import: 

    import com.yetu.siren.model._

Moreover, the library provides serialization of Siren root entities to JSON using either 
Spray-JSON or Play-JSON. Note that you need to explicitly add a dependency to either 
spray-json or play-json in your project, as siren-scala doesn't pull them into your
project transitively.

Siren-Scala supports both converting from its model types to a JSON AST and the other way round,
so that it can be used for implementing both a web API using Siren as well as a client for a 
Siren-based web API.

## Spray-JSON                                                                                                                
                                                                                                                
    import com.yetu.siren.json.sprayjson.SirenJsonProtocol._
    import spray.json._
    
    val rootEntity: Entity.RootEntity = ...
    rootEntity.toJson
    
    val json: JsValue = ???
    json.convertTo[Entity.RootEntity]
    
## Play-JSON

    import com.yetu.siren.json.playjson.PlayJsonSirenFormat._
    import play.api.libs.json._
    
    val rootEntity: Entity.RootEntity = ...
    Json.toJson(rootEntity)
    
    val json: JsValue = ???
    Json.fromJson[Entity.RootEntity](json)

## Creating Siren root entities

In order to enable you to create Siren representations for your resources, _siren-scala_ provides a
type class, `SirenRootEntityWriter`. Provide an instance of this type class for your case class, and 
you will be able to easily convert instances of that case class to Siren root entities:
 
    import com.yetu.siren
    import siren.Siren
    
    case class Person(name: String, age: Int)
    
    implicit val personSirenWriter = new SirenRootEntityWriter[Person] {
      override def toSiren(order: Order) = {
        ???      
      }
    }
    
    val personEntity: Entity.RootEntity = Siren.asRootEntity(Person("Bob", 31))

For a complete usage example, please see the [ExampleSpec.scala](src/test/scala/com/yetu/siren/ExampleSpec.scala).

# Contributors

Boris Malenšek, Daniel Westheide
