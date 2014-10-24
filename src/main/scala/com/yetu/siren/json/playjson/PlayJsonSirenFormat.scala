package com.yetu.siren.json
package playjson

trait PlayJsonSirenFormat {

  import com.yetu.siren.model._
  import Entity._
  import scalaz.std.option._
  import play.api.libs.json._

  /**
   * Play-JSON writer for a Siren root entity.
   */
  implicit val rootEntityWriter: Writes[Entity.RootEntity] = new Writes[Entity.RootEntity] {
    override def writes(entity: RootEntity): JsValue = jsObject(
      optField(FieldNames.`class`, entity.classes),
      optField(FieldNames.`properties`, entity.properties),
      optField(FieldNames.`entities`, entity.entities),
      optField(FieldNames.`actions`, entity.actions),
      optField(FieldNames.`links`, entity.links),
      optField(FieldNames.`title`, entity.title)
    )
  }

  /**
   * Play-JSON writer for a Siren embedded link.
   */
  implicit val embeddedLinkWriter: Writes[Entity.EmbeddedLink] = new Writes[Entity.EmbeddedLink] {
    override def writes(entity: EmbeddedLink): JsValue = jsObject(
      optField(FieldNames.`class`, entity.classes),
      field(FieldNames.`rel`, entity.rel),
      field(FieldNames.`href`, entity.href)
    )
  }

  /**
   * Play-JSON writer for a Siren embedded representation.
   */
  implicit val embeddedRepresentationWriter: Writes[Entity.EmbeddedRepresentation] =
    new Writes[Entity.EmbeddedRepresentation] {
      override def writes(entity: EmbeddedRepresentation): JsValue = jsObject(
        optField(FieldNames.`class`, entity.classes),
        optField(FieldNames.`properties`, entity.properties),
        optField(FieldNames.`entities`, entity.entities),
        optField(FieldNames.`actions`, entity.actions),
        optField(FieldNames.`links`, entity.links),
        optField(FieldNames.`title`, entity.title),
        field(FieldNames.`rel`, entity.rel)
      )
    }

  /**
   * Play-JSON writer for a Siren embedded entity.
   */
  implicit val embeddedEntityWriter: Writes[EmbeddedEntity] = new Writes[EmbeddedEntity] {
    override def writes(entity: EmbeddedEntity): JsValue = entity match {
      case e: Entity.EmbeddedLink           ⇒ embeddedLinkWriter writes e
      case e: Entity.EmbeddedRepresentation ⇒ embeddedRepresentationWriter writes e
    }
  }

  /**
   * Play-JSON writer for a Siren property value.
   */
  implicit val propertyValueWriter: Writes[Property.Value] = new Writes[Property.Value] {
    override def writes(value: Property.Value): JsValue = value match {
      case Property.StringValue(s)  ⇒ JsString(s)
      case Property.NumberValue(n)  ⇒ JsNumber(n)
      case Property.BooleanValue(b) ⇒ JsBoolean(b)
      case Property.NullValue       ⇒ JsNull
    }
  }

  /**
   * Play-JSON writer for Siren properties.
   */
  implicit val propertiesWriter: Writes[Properties] = new Writes[Properties] {
    override def writes(properties: Properties): JsValue = {
      val fields = properties.map (p ⇒ p.name -> Json.toJson(p.value))
      JsObject(fields)
    }
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
    override def writes(f: Action.Field): JsValue = jsObject(
      field(FieldNames.`name`, f.name),
      field(FieldNames.`type`, f.`type`),
      optField(FieldNames.`value`, f.value),
      optField(FieldNames.`title`, f.title)
    )
  }

  /**
   * Play-JSON writer for a Siren action.
   */
  implicit val actionWriter: Writes[Action] = new Writes[Action] {
    override def writes(action: Action): JsValue = jsObject(
      field(FieldNames.`name`, action.name),
      optField(FieldNames.`class`, action.classes),
      optField(FieldNames.`title`, action.title),
      field(FieldNames.`href`, action.href),
      optField(FieldNames.`method`, action.method),
      optField(FieldNames.`type`, action.`type`),
      optField(FieldNames.`fields`, action.fields)
    )
  }

  /**
   * Play-JSON writer for a Siren link.
   */
  implicit val linkWriter: Writes[Link] = new Writes[Link] {
    override def writes(link: Link): JsValue = jsObject(
      field(FieldNames.`rel`, link.rel),
      field(FieldNames.`href`, link.href),
      optField(FieldNames.`title`, link.title)
    )
  }

  private def jsonField[A: Writes](name: String)(value: A) = name -> Json.toJson(value)
  private def optField[A: Writes](name: String, value: Option[A]): Option[(String, JsValue)] =
    value map jsonField(name)
  private def field[A: Writes](name: String, value: A) = some(name -> Json.toJson(value))
  private def jsObject(fields: Option[(String, JsValue)]*) = JsObject(collectSome(fields: _*))

}

object PlayJsonSirenFormat extends PlayJsonSirenFormat
