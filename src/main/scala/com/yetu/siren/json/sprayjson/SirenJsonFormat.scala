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
import com.yetu.siren.model.Action._
import com.yetu.siren.model.Action.Field.Type
import scala.util.Try

/**
 * JSON serialization and deserialization of Siren entities.
 */
trait SirenJsonFormat { self: DefaultJsonProtocol ⇒

  import model._
  import Entity._
  import Property.Value
  import SprayJsonReadSupport._

  /**
   * Spray-JSON format for serializing and deserializing Siren entities.
   */
  implicit val entityFormat: RootJsonFormat[RootEntity] = new RootJsonFormat[RootEntity] {

    override def read(json: JsValue): RootEntity = {
      val obj = json.asJsObject
      val classes = (obj \? FieldNames.`class`) map (_.asStringSeq)
      val properties = (obj \? FieldNames.`properties`) map (_.convertTo[Properties])
      val entities = (obj \? FieldNames.`entities`) map (_.convertTo[Seq[EmbeddedEntity]])
      val actions = (obj \? FieldNames.`actions`) map (_.convertTo[Seq[Action]])
      val links = (obj \? FieldNames.`links`) map (_.convertTo[Seq[Link]])
      val title = (obj \? FieldNames.`title`) map (_.asString)
      RootEntity(classes, properties, entities, actions, links, title)
    }

    override def write(entity: RootEntity): JsValue = {
      val classes = entity.classes map (FieldNames.`class` -> _.toJson)
      val properties = entity.properties map (FieldNames.`properties` -> _.toJson)
      val entities = entity.entities map (FieldNames.`entities` -> _.toJson)
      val actions = entity.actions map (FieldNames.`actions` -> _.toJson)
      val links = entity.links map (FieldNames.`links` -> _.toJson)
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
      val rel = (obj \ FieldNames.`rel`).asStringSeq
      val href = (obj \ FieldNames.`href`).asString
      val classes = (obj \? FieldNames.`class`) map (_.asStringSeq)
      EmbeddedLink(rel = rel, href = href, classes = classes)
    }
    override def write(entity: Entity.EmbeddedLink): JsValue = {
      val classes = entity.classes map (FieldNames.`class` -> _.toJson)
      val rel = Some(FieldNames.`rel` -> entity.rel.toJson)
      val href = Some(FieldNames.`href` -> entity.href.toJson)
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
        val rel = (obj \ FieldNames.`rel`).asStringSeq
        val classes = (obj \? FieldNames.`class`) map (_.asStringSeq)
        val properties = (obj \? FieldNames.`properties`) map (_.convertTo[Properties])
        val entities = (obj \? FieldNames.`entities`) map (_.convertTo[Seq[EmbeddedEntity]])
        val actions = (obj \? FieldNames.`actions`) map (_.convertTo[Seq[Action]])
        val links = (obj \? FieldNames.`links`) map (_.convertTo[Seq[Link]])
        val title = (obj \? FieldNames.`title`) map (_.asString)
        EmbeddedRepresentation(rel, classes, properties, entities, actions, links, title)
      }
      override def write(entity: EmbeddedRepresentation): JsValue = {
        val classes = entity.classes map (FieldNames.`class` -> _.toJson)
        val properties = entity.properties map (FieldNames.`properties` -> _.toJson)
        val entities = entity.entities map (FieldNames.`entities` -> _.toJson)
        val actions = entity.actions map (FieldNames.`actions` -> _.toJson)
        val links = entity.links map (FieldNames.`links` -> _.toJson)
        val title = entity.title map (FieldNames.`title` -> _.toJson)
        val rel = Some(FieldNames.`rel` -> entity.rel.toJson)
        JsObject(collectSome(classes, properties, entities, actions, links, title, rel))
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
   * Spray-JSON format for serializing and deserializing Siren properties.
   */
  implicit val propertiesFormat: RootJsonFormat[Properties] = new RootJsonFormat[Properties] {
    override def read(json: JsValue): Properties = {
      val properties = json.asJsObject.fields map {
        case (name, x) ⇒ Property(name, x.convertTo[Property.Value])
      }
      properties.toList
    }
    override def write(obj: Properties): JsValue = {
      val fields = obj map (p ⇒ p.name -> p.value.toJson)
      JsObject(fields: _*)
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
   * Spray-JSON format for serializing and deserializing Siren actions.
   */
  implicit val actionFormat: RootJsonFormat[Action] = new RootJsonFormat[Action] {
    override def read(json: JsValue): Action = {
      val obj = json.asJsObject
      val name = (obj \ FieldNames.`name`).asString
      val href = (obj \ FieldNames.`href`).asString
      val classes = (obj \? FieldNames.`class`) map (_.asStringSeq)
      val title = (obj \? FieldNames.`title`) map (_.asString)
      val method = (obj \? FieldNames.`method`) map (_.convertTo[Action.Method])
      val `type` = (obj \? FieldNames.`type`) map (_.convertTo[Action.Encoding])
      val fields = (obj \? FieldNames.`fields`) map (_.convertTo[Seq[Action.Field]])
      Action(name, href, classes, title, method, `type`, fields)
    }
    override def write(action: Action): JsValue = {
      val name = Some(FieldNames.`name` -> action.name.toJson)
      val classes = action.classes map (FieldNames.`class` -> _.toJson)
      val title = action.title map (FieldNames.`title` -> _.toJson)
      val href = Some(FieldNames.`href` -> action.href.toJson)
      val method = action.method map (FieldNames.`method` -> _.toJson)
      val `type` = action.`type` map (FieldNames.`type` -> _.toJson)
      val fields = action.fields map (FieldNames.`fields` -> _.toJson)
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
      val name = Some(FieldNames.`name` -> field.name.toJson)
      val `type` = Some(FieldNames.`type` -> field.`type`.name.toJson)
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
      val rels = (obj \ FieldNames.`rel`).asStringSeq
      val title = (obj \? FieldNames.`title`) map (_.asString)
      Link(rels, href, title)
    }
    override def write(link: Link): JsValue = {
      val rels = Some(FieldNames.`rel` -> link.rel.toJson)
      val href = Some(FieldNames.`href` -> link.href.toJson)
      val title = link.title map (FieldNames.`title` -> _.toJson)
      JsObject(collectSome(rels, href, title))
    }
  }

}
