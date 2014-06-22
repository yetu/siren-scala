# Description

A Scala library for producing Siren entities from your domain model objects and serializing them to 
`application/vnd.siren+json`. See the [Siren Spec](http://sirenspec.org)

This library is still very much a work-in-progress, so expect its API to change.

#Usage

We haven't provided any binary releases of _siren-scala_ yet, so at the moment, you need to add a
source dependency to use it.

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