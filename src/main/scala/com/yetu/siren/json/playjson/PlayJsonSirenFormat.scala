package com.yetu.siren.json
package playjson

import com.yetu.siren.model.Property.Value

trait PlayJsonSirenFormat {

  import com.yetu.siren.model._
  import Entity._
  import play.api.libs.functional.syntax._
  import play.api.libs.json._

  import collection.immutable.{ Seq ⇒ ImmutableSeq }

  /**
   * Play-JSON format for a Siren root entity.
   */
  implicit lazy val rootEntityFormat: Format[Entity.RootEntity] = (
    (JsPath \ FieldNames.`class`).formatNullable[ImmutableSeq[String]] and
    (JsPath \ FieldNames.`properties`).formatNullable[Properties] and
    (JsPath \ FieldNames.`entities`).formatNullable[ImmutableSeq[EmbeddedEntity]] and
    (JsPath \ FieldNames.`actions`).formatNullable[ImmutableSeq[Action]] and
    (JsPath \ FieldNames.`links`).formatNullable[ImmutableSeq[Link]] and
    (JsPath \ FieldNames.`title`).formatNullable[String]
  )(Entity.RootEntity.apply, unlift(Entity.RootEntity.unapply))

  /**
   * Play-JSON writer for a Siren embedded entity.
   */
  implicit lazy val embeddedEntityFormat: Format[EmbeddedEntity] = new Format[EmbeddedEntity] {
    override def writes(entity: EmbeddedEntity): JsValue = entity match {
      case e: Entity.EmbeddedLink           ⇒ embeddedLinkWriter writes e
      case e: Entity.EmbeddedRepresentation ⇒ embeddedRepresentationWriter writes e
    }

    override def reads(json: JsValue): JsResult[EmbeddedEntity] =
      embeddedLinkReads.reads(json) orElse embeddedRepresentationReads.reads(json)
  }

  // use separate Reads and Writes for sub-types of EmbeddedEntity to avoid ambigious implicits
  // due to the subtyping (Writes has a different variance than both Reads and Format):

  /**
   * Play-JSON writer for a Siren embedded link.
   */
  implicit lazy val embeddedLinkWriter: Writes[Entity.EmbeddedLink] = (
    (JsPath \ FieldNames.`rel`).write[ImmutableSeq[String]] and
    (JsPath \ FieldNames.`href`).write[String] and
    (JsPath \ FieldNames.`class`).writeNullable[ImmutableSeq[String]]
  )(unlift(Entity.EmbeddedLink.unapply))
  /**
   * Play-JSON reads for a Siren embedded link.
   */
  implicit lazy val embeddedLinkReads: Reads[Entity.EmbeddedLink] = (
    (JsPath \ FieldNames.`rel`).read[ImmutableSeq[String]] and
    (JsPath \ FieldNames.`href`).read[String] and
    (JsPath \ FieldNames.`class`).readNullable[ImmutableSeq[String]]
  )(Entity.EmbeddedLink.apply _)

  /**
   * Play-JSON writer for a Siren embedded representation.
   */
  implicit lazy val embeddedRepresentationWriter: Writes[Entity.EmbeddedRepresentation] = (
    (JsPath \ FieldNames.`rel`).write[ImmutableSeq[String]] and
    (JsPath \ FieldNames.`class`).writeNullable[ImmutableSeq[String]] and
    (JsPath \ FieldNames.`properties`).writeNullable[Properties] and
    (JsPath \ FieldNames.`entities`).writeNullable[ImmutableSeq[EmbeddedEntity]] and
    (JsPath \ FieldNames.`actions`).writeNullable[ImmutableSeq[Action]] and
    (JsPath \ FieldNames.`links`).writeNullable[ImmutableSeq[Link]] and
    (JsPath \ FieldNames.`title`).writeNullable[String]
  )(unlift(EmbeddedRepresentation.unapply))

  /**
   * Play-JSON reads for a Siren embedded representation.
   */
  implicit lazy val embeddedRepresentationReads: Reads[Entity.EmbeddedRepresentation] = (
    (JsPath \ FieldNames.`rel`).read[ImmutableSeq[String]] and
    (JsPath \ FieldNames.`class`).readNullable[ImmutableSeq[String]] and
    (JsPath \ FieldNames.`properties`).readNullable[Properties] and
    (JsPath \ FieldNames.`entities`).readNullable[ImmutableSeq[EmbeddedEntity]] and
    (JsPath \ FieldNames.`actions`).readNullable[ImmutableSeq[Action]] and
    (JsPath \ FieldNames.`links`).readNullable[ImmutableSeq[Link]] and
    (JsPath \ FieldNames.`title`).readNullable[String]
  )(EmbeddedRepresentation.apply _)

  /**
   * Play-JSON writer for a Siren property value.
   */
  implicit val propertyValueFormat: Format[Property.Value] = new Format[Property.Value] {
    override def writes(value: Property.Value): JsValue = value match {
      case Property.StringValue(s)  ⇒ JsString(s)
      case Property.NumberValue(n)  ⇒ JsNumber(n)
      case Property.BooleanValue(b) ⇒ JsBoolean(b)
      case Property.JsObjectValue(o) ⇒ JsObject(o.map {
        case (k, v) ⇒ (k, writes(v))
      })
      case Property.JsArrayValue(a) ⇒ JsArray(a.map(v ⇒ writes(v)))
      case Property.NullValue       ⇒ JsNull
    }

    override def reads(json: JsValue): JsResult[Property.Value] = json match {
      case JsString(s)  ⇒ JsSuccess(Property.StringValue(s))
      case JsNumber(n)  ⇒ JsSuccess(Property.NumberValue(n))
      case JsBoolean(b) ⇒ JsSuccess(Property.BooleanValue(b))
      case JsObject(obj) ⇒
        val propsObjValue: Seq[(String, Value)] = obj.map(seq ⇒ {
          val (key, jsValue) = seq
          val read = reads(jsValue).getOrElse(Property.NullValue)
          (key, read)
        })
        JsSuccess(Property.JsObjectValue(propsObjValue))
      case JsArray(arr) ⇒
        val propsArrayValue: Seq[Value] = arr.map(jsValue ⇒ {
          reads(jsValue).getOrElse(Property.NullValue)
        })
        JsSuccess(Property.JsArrayValue(propsArrayValue))
      case JsNull ⇒ JsSuccess(Property.NullValue)
      case _      ⇒ JsError("error.expected.sirenpropertyvalue")
    }
  }

  /**
   * Play-JSON format for Siren properties.
   */
  implicit val propertiesWriter: Format[Properties] = new Format[Properties] {
    override def writes(properties: Properties): JsValue = {
      val fields = properties.map(p ⇒ p.name -> Json.toJson(p.value))
      JsObject(fields)
    }

    override def reads(json: JsValue): JsResult[Properties] = json match {
      case JsObject(fields) ⇒
        fields.foldLeft[JsResult[Properties]](JsSuccess(Vector.empty)) {
          case (acc, (name, jsValue)) ⇒ (acc, jsValue.validate[Property.Value]) match {
            case (JsSuccess(props, _), JsSuccess(value, _)) ⇒
              JsSuccess(props :+ Property(name, value))
            case (JsSuccess(_, _), JsError(errors)) ⇒
              JsError(Seq(JsPath \ name -> errors.flatMap(_._2)))
            case (e: JsError, s: JsSuccess[_]) ⇒
              e
            case (e: JsError, JsError(errors)) ⇒
              e ++ JsError(Seq(JsPath \ name -> errors.flatMap(_._2)))
          }
        }
      case _ ⇒ JsError("error.expected.jsobject")
    }
  }

  /**
   * Play JSON format for Siren action methods.
   */
  implicit val methodFormat: Format[Action.Method] = new Format[Action.Method] {
    override def writes(method: Action.Method): JsValue = JsString(method.name)

    override def reads(json: JsValue): JsResult[Action.Method] =
      json.asOpt[String] flatMap Action.Method.forName asJsResult "error.expected.method"
  }

  /**
   * Play-JSON format for Siren action encodings.
   */
  implicit val encodingFormat: Format[Action.Encoding] = new Format[Action.Encoding] {
    override def writes(encoding: Action.Encoding): JsValue = JsString(encoding.name)

    override def reads(json: JsValue): JsResult[Action.Encoding] =
      json.asOpt[String] flatMap Action.Encoding.forName asJsResult "error.expected.encoding"
  }

  /**
   * Play-JSON format for Siren action field types.
   */
  implicit val fieldTypeFormat: Format[Action.Field.Type] = new Format[Action.Field.Type] {
    override def writes(fieldType: Action.Field.Type): JsValue = JsString(fieldType.name)

    override def reads(json: JsValue): JsResult[Action.Field.Type] =
      json.asOpt[String] flatMap Action.Field.Type.forName asJsResult "error.expected.fieldtype"
  }

  /**
   * Play-JSON format for an action field.
   */
  implicit val fieldFormat: Format[Action.Field] = (
    (JsPath \ FieldNames.`name`).format[String] and
    (JsPath \ FieldNames.`type`).format[Action.Field.Type] and
    (JsPath \ FieldNames.`value`).formatNullable[String] and
    (JsPath \ FieldNames.`title`).formatNullable[String]
  )(Action.Field.apply, unlift(Action.Field.unapply))

  /**
   * Play-JSON format for a Siren action.
   */
  implicit val actionFormat: Format[Action] = (
    (JsPath \ FieldNames.`name`).format[String] and
    (JsPath \ FieldNames.`href`).format[String] and
    (JsPath \ FieldNames.`class`).formatNullable[ImmutableSeq[String]] and
    (JsPath \ FieldNames.`title`).formatNullable[String] and
    (JsPath \ FieldNames.`method`).formatNullable[Action.Method] and
    (JsPath \ FieldNames.`type`).formatNullable[Action.Encoding] and
    (JsPath \ FieldNames.`fields`).formatNullable[ImmutableSeq[Action.Field]]
  )(Action.apply, unlift(Action.unapply))

  /**
   * Play-JSON format for a Siren link.
   */
  implicit val linkFormat: Format[Link] = (
    (JsPath \ FieldNames.`rel`).format[ImmutableSeq[String]] and
    (JsPath \ FieldNames.`href`).format[String] and
    (JsPath \ FieldNames.`title`).formatNullable[String]
  )(Link.apply, unlift(Link.unapply))

}

object PlayJsonSirenFormat extends PlayJsonSirenFormat
