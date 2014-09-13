package com.yetu.siren.json
package playjson

import com.yetu.siren.model

trait PlayJsonSirenFormat {

  import model._
  import scalaz.std.option._
  import play.api.libs.json._

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
   * Play-JSON writer for action fields.
   */
  implicit val fieldsWriter: Writes[Action.Fields] = Writes {
    (fields: Action.Fields) ⇒ Json.toJson(fields.list)
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
      val classes = action.classes map (cs ⇒ FieldNames.`class` -> Json.toJson(cs.list))
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
