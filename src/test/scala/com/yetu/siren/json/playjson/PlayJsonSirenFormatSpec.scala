package com.yetu.siren.json.playjson

import com.yetu.siren.json.JsonBaseSpec
import com.yetu.siren.model.Action
import org.scalatest.DiagrammedAssertions

class PlayJsonSirenFormatSpec extends JsonBaseSpec with DiagrammedAssertions with PlayJsonSirenFormat {

  import play.api.libs.json._

  private val propsJson = Json.parse(propsJsonString)
  private val classesJson = Json.parse(classesJsonString)
  private val linksJson = Json.parse(linksJsonString)
  private val actionJson = Json.parse(actionJsonString)
  private val embeddedLinkJson = Json.parse(embeddedLinkJsonString)
  private val embeddedRepresentationJson = Json.parse(embeddedRepresentationJsonString)
  private val entityJson = Json.parse(entityJsonString)

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

  "serialize Siren properties" in {
    assert(Json.toJson(props) == propsJson)
  }

  "serialize Siren classes" in {
    assert(Json.toJson(classes.list) == classesJson)
  }

  "serialize a Siren embedded link" in {
    assert(Json.toJson(embeddedLink) == embeddedLinkJson)
  }

  "serialize a Siren embedded representation" in {
    assert(Json.toJson(embeddedRepresentation) == embeddedRepresentationJson)
  }
  "serialize a complete Siren entity with embedded linked and fully represented sub-entities correctly" in {
    assert(Json.toJson(entity) == entityJson)
  }

}
