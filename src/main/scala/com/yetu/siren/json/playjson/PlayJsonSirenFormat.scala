package com.yetu.siren.json
package playjson

import com.yetu.siren.model
import com.yetu.siren.model.Entity.{ RootEntity, EmbeddedRepresentation, EmbeddedLink }

import scalaz.NonEmptyList

trait PlayJsonSirenFormat {

  import model._
  import scalaz.std.option._
  import play.api.libs.json._

  implicit val rootEntityWriter: Writes[Entity.RootEntity] = new Writes[Entity.RootEntity] {
    override def writes(entity: RootEntity): JsValue = {
      val classes = entity.classes map (FieldNames.`class` -> Json.toJson(_))
      val properties = entity.properties map (FieldNames.`properties` -> Json.toJson(_))
      val entities = entity.entities map (FieldNames.`entities` -> Json.toJson(_))
      val actions = entity.actions map (FieldNames.`actions` -> Json.toJson(_))
      val links = entity.links map (FieldNames.`links` -> Json.toJson(_))
      val title = entity.title map (FieldNames.`title` -> Json.toJson(_))
      JsObject(collectSome(classes, properties, entities, actions, links, title))
    }
  }

  implicit val embeddedLinkWriter: Writes[Entity.EmbeddedLink] = new Writes[Entity.EmbeddedLink] {
    override def writes(entity: EmbeddedLink): JsValue = {
      val classes = entity.classes map (FieldNames.`class` -> Json.toJson(_))
      val rel = some(FieldNames.`rel` -> Json.toJson(entity.rel))
      val href = some(FieldNames.`href` -> Json.toJson(entity.href))
      JsObject(collectSome(classes, rel, href))
    }
  }

  implicit val embeddedRepresentationWriter: Writes[Entity.EmbeddedRepresentation] =
    new Writes[Entity.EmbeddedRepresentation] {
      override def writes(entity: EmbeddedRepresentation): JsValue = {
        val classes = entity.classes map (FieldNames.`class` -> Json.toJson(_))
        val properties = entity.properties map (FieldNames.`properties` -> Json.toJson(_))
        val entities = entity.entities map (FieldNames.`entities` -> Json.toJson(_))
        val actions = entity.actions map (FieldNames.`actions` -> Json.toJson(_))
        val links = entity.links map (FieldNames.`links` -> Json.toJson(_))
        val title = entity.title map (FieldNames.`title` -> Json.toJson(_))
        val rel = some(FieldNames.`rel` -> Json.toJson(entity.rel))
        JsObject(collectSome(classes, properties, entities, actions, links, title, rel))
      }
    }

  implicit val embeddedEntityWriter: Writes[EmbeddedEntity] = new Writes[EmbeddedEntity] {
    override def writes(entity: EmbeddedEntity): JsValue = entity match {
      case e: Entity.EmbeddedLink           ⇒ embeddedLinkWriter writes e
      case e: Entity.EmbeddedRepresentation ⇒ embeddedRepresentationWriter writes e
    }
  }

  implicit val propertyValueWriter: Writes[Property.Value] = new Writes[Property.Value] {
    override def writes(value: Property.Value): JsValue = value match {
      case Property.StringValue(s)  ⇒ JsString(s)
      case Property.NumberValue(n)  ⇒ JsNumber(n)
      case Property.BooleanValue(b) ⇒ JsBoolean(b)
      case Property.NullValue       ⇒ JsNull
    }
  }

  implicit val propertiesWriter: Writes[Properties] = new Writes[Properties] {
    override def writes(properties: Properties): JsValue = {
      val fields = properties.list.map (p ⇒ p.name -> Json.toJson(p.value))
      JsObject(fields)
    }
  }

  implicit def nonEmptyListWriter[A: Writes]: Writes[NonEmptyList[A]] = Writes {
    (xs: NonEmptyList[A]) ⇒ Json.toJson(xs.list)
  }

  /**
   * Play JSON writer for Siren action methods.
   */
  implicit val methodWriter: Writes[Action.Method] = Writes {
    (method: Action.Method) ⇒ JsString(method.name)
  }

  /**
   * Play-JSON writer for Siren action encodings.
   */
  implicit val encodingWriter: Writes[Action.Encoding] = Writes {
    (encoding: Action.Encoding) ⇒ JsString(encoding.name)
  }

  /**
   * Play-JSON writer for Siren action field types.
   */
  implicit val fieldTypeWriter: Writes[Action.Field.Type] = Writes {
    (fieldType: Action.Field.Type) ⇒ JsString(fieldType.name)
  }

  /**
   * Play-JSON writer for an action field.
   */
  implicit val fieldWriter: Writes[Action.Field] = new Writes[Action.Field] {
    override def writes(field: Action.Field): JsValue = {
      val name = some(FieldNames.`name` -> JsString(field.name))
      val `type` = some(FieldNames.`type` -> Json.toJson(field.`type`))
      val value = field.value map (FieldNames.`value` -> JsString(_))
      val title = field.title map (FieldNames.`title` -> JsString(_))
      JsObject(collectSome(name, `type`, value, title))
    }
  }

  implicit val actionWriter: Writes[Action] = new Writes[Action] {
    override def writes(action: Action): JsValue = {
      val name = some(FieldNames.`name` -> JsString(action.name))
      val classes = action.classes map (FieldNames.`class` -> Json.toJson(_))
      val title = action.title map (FieldNames.`title` -> JsString(_))
      val href = some(FieldNames.`href` -> JsString(action.href))
      val method = action.method map (FieldNames.`method` -> Json.toJson(_))
      val `type` = action.`type` map (FieldNames.`type` -> Json.toJson(_))
      val fields = action.fields map (FieldNames.`fields` -> Json.toJson(_))
      JsObject(collectSome(name, classes, title, href, method, `type`, fields))
    }
  }

  /**
   * Play-JSON writer for Siren links.
   */
  implicit val linkWriter: Writes[Link] = new Writes[Link] {
    override def writes(link: Link): JsValue = {
      val rels = some(FieldNames.`rel` -> Json.toJson(link.rel.list))
      val href = some(FieldNames.`href` -> Json.toJson(link.href))
      val title = link.title map (FieldNames.`title` -> Json.toJson(_))
      JsObject(collectSome(rels, href, title))
    }
  }

}
