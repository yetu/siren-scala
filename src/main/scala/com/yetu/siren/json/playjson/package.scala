package com.yetu.siren.json

import play.api.data.validation.ValidationError
import play.api.libs.json.{ JsError, JsSuccess, JsResult }

package object playjson {

  implicit class Option2JsResult[A](val opt: Option[A]) extends AnyVal {
    def asJsResult(msg: String): JsResult[A] = opt match {
      case Some(a) ⇒ JsSuccess(a)
      case None    ⇒ JsError(ValidationError(msg))
    }
  }

}
