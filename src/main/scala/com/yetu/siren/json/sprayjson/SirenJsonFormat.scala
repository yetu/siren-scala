/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 yetu AG
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.yetu.siren
package json
package sprayjson

import spray.json._
import com.yetu.siren.model.Action.{ Fields, Encoding }
import com.yetu.siren.model.Action.Field.Type
import scala.util.Try
import com.yetu.siren.model

/**
 * JSON serialization and deserialization of Siren entities.
 */
trait SirenJsonFormat { self: DefaultJsonProtocol ⇒

  import model._
  import Entity._
  import Property.Value
  import SprayJsonReadSupport._
  import scalaz.std.option._
  import scalaz.NonEmptyList

  /**
   * Constants for all the JSON field names used in Siren.
   */
  private object FieldNames {
    val `class` = "class"
    val `properties` = "properties"
    val `entities` = "entities"
    val `actions` = "actions"
    val `links` = "links"
    val `title` = "title"
    val `rel` = "rel"
    val `href` = "href"
    val `name` = "name"
    val `method` = "method"
    val `type` = "type"
    val `fields` = "fields"
    val `value` = "value"
  }

  /**
   * Spray-JSON format for serializing and deserializing Siren entities.
   */
  implicit val entityFormat: RootJsonFormat[RootEntity] = new RootJsonFormat[RootEntity] {

    override def read(json: JsValue): RootEntity = {
      val obj = json.asJsObject
      val classes = (obj \? FieldNames.`class`) map (_.asStringNel)
      val properties = (obj \? FieldNames.`properties`) map (_.convertTo[Properties])
      val entities = (obj \? FieldNames.`entities`) map (_.convertTo[Entities])
      val actions = (obj \? FieldNames.`actions`) map (_.convertTo[Actions])
      val links = (obj \? FieldNames.`links`) map (_.convertTo[Links])
      val title = (obj \? FieldNames.`title`) map (_.asString)
      RootEntity(classes, properties, entities, actions, links, title)
    }

    override def write(entity: RootEntity): JsValue = {
      val classes = entity.classes map (FieldNames.`class` -> _.list.toJson)
      val properties = entity.properties map (FieldNames.`properties` -> _.toJson)
      val entities = entity.entities map (FieldNames.`entities` -> _.toJson)
      val actions = entity.actions map (FieldNames.`actions` -> _.list.toJson)
      val links = entity.links map (FieldNames.`links` -> _.list.toJson)
      val title = entity.title map (FieldNames.`title` -> _.toJson)
      JsObject(collectSome(classes, properties, entities, actions, links, title))
    }
  }

  /**
   * Spray-JSON format for serializing and deserializing embedded linked sub-entities.
   */
  implicit val embeddedLinkFormat: RootJsonFormat[Entity.EmbeddedLink] = new RootJsonFormat[Entity.EmbeddedLink] {
    override def read(json: JsValue): Entity.EmbeddedLink = {
      val obj = json.asJsObject
      val rel = (obj \ FieldNames.`rel`).asStringNel
      val href = (obj \ FieldNames.`href`).asString
      val classes = (obj \? FieldNames.`class`) map (_.asStringNel)
      EmbeddedLink(rel = rel, href = href, classes = classes)
    }
    override def write(entity: Entity.EmbeddedLink): JsValue = {
      val classes = entity.classes map (FieldNames.`class` -> _.list.toJson)
      val rel = some(FieldNames.`rel` -> entity.rel.list.toJson)
      val href = some(FieldNames.`href` -> entity.href.toJson)
      JsObject(collectSome(classes, rel, href))
    }
  }

  /**
   * Spray-JSON format for serializing and deserializing embedded fully represented sub-entities.
   */
  implicit val embeddedRepresentationFormat: RootJsonFormat[Entity.EmbeddedRepresentation] =
    new RootJsonFormat[EmbeddedRepresentation] {
      override def read(json: JsValue): EmbeddedRepresentation = {
        val obj = json.asJsObject
        val rel = (obj \ FieldNames.`rel`).asStringNel
        val classes = (obj \? FieldNames.`class`) map (_.asStringNel)
        val properties = (obj \? FieldNames.`properties`) map (_.convertTo[Properties])
        val actions = (obj \? FieldNames.`actions`) map (_.convertTo[Actions])
        val links = (obj \? FieldNames.`links`) map (_.convertTo[Links])
        val title = (obj \? FieldNames.`title`) map (_.asString)
        EmbeddedRepresentation(rel, classes, properties, actions, links = links, title)
      }
      override def write(entity: EmbeddedRepresentation): JsValue = {
        val classes = entity.classes map (FieldNames.`class` -> _.list.toJson)
        val properties = entity.properties map (FieldNames.`properties` -> _.toJson)
        val actions = entity.actions map (FieldNames.`actions` -> _.list.toJson)
        val links = entity.links map (FieldNames.`links` -> _.toJson)
        val title = entity.title map (FieldNames.`title` -> _.toJson)
        val rels = some(FieldNames.`rel` -> entity.rel.list.toJson)
        JsObject(collectSome(classes, properties, actions, links, title, rels))
      }
    }

  /**
   * Spray-JSON format for serializing and deserializing sub-entities.
   */
  implicit val embeddedEntityFormat: RootJsonFormat[EmbeddedEntity] = new RootJsonFormat[EmbeddedEntity] {
    override def read(json: JsValue): EmbeddedEntity = {
      Try(json.convertTo[EmbeddedLink]) getOrElse json.convertTo[EmbeddedRepresentation]
    }
    override def write(entity: EmbeddedEntity): JsValue = entity match {
      case e: EmbeddedLink           ⇒ e.toJson
      case e: EmbeddedRepresentation ⇒ e.toJson
    }
  }

  /**
   * Spray-JSON format for serializing and deserializing links.
   */
  implicit val linksFormat: RootJsonFormat[Links] = new RootJsonFormat[Links] {
    override def read(json: JsValue) = {
      val links = json match {
        case JsArray(items) ⇒ items.toList match {
          case head :: tail ⇒ NonEmptyList.nel(head, tail)
          case Nil          ⇒ throwDesEx(s"Array of links must not be empty")
        }
        case _ ⇒ throwDesEx(s"$json is not a JSON array of Siren links")
      }
      links map (_.convertTo[Link])
    }
    override def write(links: Links) = links.list.toJson
  }

  /**
   * Spray-JSON format for serializing and deserializing actions.
   */
  implicit val actionsFormat: RootJsonFormat[Actions] = new RootJsonFormat[Actions] {
    override def read(json: JsValue) = {
      val actions = json match {
        case JsArray(items) ⇒ items.toList match {
          case head :: tail ⇒ NonEmptyList.nel(head, tail)
          case Nil          ⇒ throwDesEx("Array of actions must not be empty")
        }
        case _ ⇒ throwDesEx(s"$json is not a JSON array of Siren actions")
      }
      actions map (_.convertTo[Action])
    }
    override def write(actions: Actions) = actions.list.toJson
  }

  /**
   * Spray-JSON format for serializing and deserializing Siren properties.
   */
  implicit val propertiesFormat: RootJsonFormat[Properties] = new RootJsonFormat[Properties] {
    override def read(json: JsValue): Properties = {
      val properties = json.asJsObject.fields map {
        case (name, x) ⇒ Property(name, x.convertTo[Property.Value])
      }
      properties.toList match {
        case head :: tail ⇒ NonEmptyList.nel(head, tail)
        case Nil          ⇒ throwDesEx(s"Array of properties must not be empty")
      }
    }
    override def write(obj: Properties): JsValue = {
      val fields = obj.list map (p ⇒ p.name -> p.value.toJson)
      JsObject(fields)
    }
  }

  /**
   * Spray-JSON format for serializing and deserializing Siren property values.
   */
  implicit val propertyValueFormat: JsonFormat[Property.Value] = new JsonFormat[Property.Value] {
    override def read(json: JsValue): Value = json match {
      case JsString(s)  ⇒ Property.StringValue(s)
      case JsNumber(n)  ⇒ Property.NumberValue(n)
      case JsBoolean(b) ⇒ Property.BooleanValue(b)
      case JsNull       ⇒ Property.NullValue
      case x            ⇒ throwDesEx(s"$x is not a valid property value")
    }
    override def write(obj: Value): JsValue =
      obj match {
        case Property.StringValue(s)  ⇒ s.toJson
        case Property.NumberValue(n)  ⇒ n.toJson
        case Property.BooleanValue(b) ⇒ b.toJson
        case Property.NullValue       ⇒ JsNull
      }
  }

  /**
   * Spray-JSON format for serializing and deserializing Siren action methods
   */
  implicit val methodFormat: JsonFormat[Action.Method] = new JsonFormat[Action.Method] {
    override def read(json: JsValue): Action.Method = json match {
      case JsString(Action.Method(m)) ⇒ m
      case x                          ⇒ throwDesEx(s"$x is not a valid JSON representation of a method")
    }
    override def write(method: Action.Method): JsValue = JsString(method.name)
  }
  /**
   * Spray-JSON format for serializing and deserializing Siren action encodings
   */
  implicit val encodingFormat: JsonFormat[Action.Encoding] = new JsonFormat[Action.Encoding] {
    override def read(json: JsValue) = json match {
      case JsString(Action.Encoding(e)) ⇒ e
      case x                            ⇒ throwDesEx(s"$x is not a valid JSON representation of a Siren action encoding")
    }
    override def write(enc: Encoding): JsValue = JsString(enc.name)
  }

  /**
   * Spray-JSON format for serializing and deserializing Siren action field types.
   */
  implicit val fieldTypeFormat: JsonFormat[Action.Field.Type] = new JsonFormat[Action.Field.Type] {
    override def read(json: JsValue): Type = json match {
      case JsString(Action.Field.Type(t)) ⇒ t
      case x                              ⇒ throwDesEx(s"$x is not a valid JSON representation of an action field type")
    }
    override def write(t: Type): JsValue = JsString(t.name)
  }

  /**
   * Spray-JSON format for action fields.
   */
  implicit val fieldsFormat: RootJsonFormat[Fields] = new RootJsonFormat[Fields] {
    override def read(json: JsValue) = {
      val fields = json match {
        case JsArray(items) ⇒ items.toList match {
          case head :: tail ⇒ NonEmptyList.nel(head, tail)
          case Nil          ⇒ throwDesEx(s"Array of links must not be empty")
        }
        case _ ⇒ throwDesEx(s"$json is not a JSON array of Siren links")
      }
      fields map (_.convertTo[Action.Field])
    }
    override def write(fields: Fields) = fields.list.toJson
  }

  /**
   * Spray-JSON format for serializing and deserializing Siren actions.
   */
  implicit val actionFormat: RootJsonFormat[Action] = new RootJsonFormat[Action] {
    override def read(json: JsValue): Action = {
      val obj = json.asJsObject
      val name = (obj \ FieldNames.`name`).asString
      val href = (obj \ FieldNames.`href`).asString
      val classes = (obj \? FieldNames.`class`) map (_.asStringNel)
      val title = (obj \? FieldNames.`title`) map (_.asString)
      val method = (obj \? FieldNames.`method`) map (_.convertTo[Action.Method])
      val `type` = (obj \? FieldNames.`type`) map (_.convertTo[Action.Encoding])
      val fields = (obj \? FieldNames.`fields`) map (_.convertTo[Fields])
      Action(name, href, classes, title, method, `type`, fields)
    }
    override def write(action: Action): JsValue = {
      val name = some(FieldNames.`name` -> action.name.toJson)
      val classes = action.classes map (FieldNames.`class` -> _.list.toJson)
      val title = action.title map (FieldNames.`title` -> _.toJson)
      val href = some(FieldNames.`href` -> action.href.toJson)
      val method = action.method map (FieldNames.`method` -> _.toJson)
      val `type` = action.`type` map (FieldNames.`type` -> _.toJson)
      val fields = action.fields map (FieldNames.`fields` -> _.list.toJson)
      JsObject(collectSome(name, classes, title, href, method, `type`, fields))
    }
  }

  /**
   * Spray-JSON format for serializing and deserializing fields of a Siren action.
   */
  implicit val fieldFormat: RootJsonFormat[Action.Field] = new RootJsonFormat[Action.Field] {
    override def read(json: JsValue): Action.Field = {
      val obj = json.asJsObject
      val name = (obj \ FieldNames.`name`).asString
      val `type` = (obj \ FieldNames.`type`).convertTo[Action.Field.Type]
      val value = (obj \? FieldNames.`value`) map (_.asString)
      val title = (obj \? FieldNames.`title`) map (_.asString)
      Action.Field(name, `type`, value, title)
    }
    override def write(field: Action.Field): JsValue = {
      val name = some(FieldNames.`name` -> field.name.toJson)
      val `type` = some(FieldNames.`type` -> field.`type`.name.toJson)
      val value = field.value map (FieldNames.`value` -> _.toJson)
      val title = field.title map (FieldNames.`title` -> _.toJson)
      JsObject(collectSome(name, `type`, value, title))
    }
  }

  /**
   * Spray-JSON format for serializing and deserializing Siren links.
   */
  implicit val linkFormat: RootJsonFormat[Link] = new RootJsonFormat[Link] {
    override def read(json: JsValue): Link = {
      val obj = json.asJsObject
      val href = (obj \ FieldNames.`href`).asString
      val rels = (obj \ FieldNames.`rel`).asStringNel
      val title = (obj \? FieldNames.`title`) map (_.asString)
      Link(rels, href, title)
    }
    override def write(link: Link): JsValue = {
      val rels = some(FieldNames.`rel` -> link.rel.list.toJson)
      val href = some(FieldNames.`href` -> link.href.toJson)
      val title = link.title map (FieldNames.`title` -> _.toJson)
      JsObject(collectSome(rels, href, title))
    }
  }

  /**
   * Collects only those of the given options that are defined, removing the others.
   * @param opts one or more options of type A
   * @tparam A the type of the given options
   */
  private def collectSome[A](opts: Option[A]*): List[A] =
    (opts collect { case Some(field) ⇒ field }).toList

}
