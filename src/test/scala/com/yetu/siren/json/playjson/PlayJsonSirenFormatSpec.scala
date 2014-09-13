package com.yetu.siren.json.playjson

import com.yetu.siren.json.JsonBaseSpec
import com.yetu.siren.model.Action
import org.scalatest.DiagrammedAssertions

class PlayJsonSirenFormatSpec extends JsonBaseSpec with DiagrammedAssertions with PlayJsonSirenFormat {

  import play.api.libs.json._

  private val linksJson = Json.parse(linksJsonString)
  private val actionJson = Json.parse(actionJsonString)

  "serialize a Siren link" in {
    assert(Json.toJson(links.list) == linksJson)
  }

  "serialize a Siren action method" in {
    val method: Action.Method = Action.Method.GET
    assert(Json.toJson(method) == JsString("GET"))
  }

  "serialize a Siren action" in {
    assert(Json.toJson(action) == actionJson)
  }

}
