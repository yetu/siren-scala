package com.yetu.siren.json.playjson

import com.yetu.siren.json.JsonBaseSpec
import org.scalactic.TypeCheckedTripleEquals
import org.scalatest.{ Inside, DiagrammedAssertions }
import play.api.libs.json._

class PlayJsonSirenFormatSpec extends JsonBaseSpec[JsValue]
    with DiagrammedAssertions with Inside with TypeCheckedTripleEquals with PlayJsonSirenFormat {

  import com.yetu.siren.model._
  import scala.collection.immutable.{ Seq ⇒ ImmutableSeq }

  override protected def parseJson(jsonString: String) = Json.parse(jsonString)

  "PlayJsonSirenFormat" must {
    "serialize a Siren link" in {
      assert(Json.toJson(links) === linksJson)
    }

    "serialize a Siren action method" in {
      val method: Action.Method = Action.Method.GET
      assert(Json.toJson(method) === JsString("GET"))
    }

    "serialize a Siren action" in {
      assert(Json.toJson(action) === actionJson)
    }

    "serialize Siren properties" in {
      assert(Json.toJson(props) === propsJson)
    }

    "serialize Siren classes" in {
      assert(Json.toJson(classes) === classesJson)
    }

    "serialize a Siren embedded link" in {
      assert(Json.toJson(embeddedLink) === embeddedLinkJson)
    }

    "serialize a Siren embedded representation" in {
      assert(Json.toJson(embeddedRepresentation) === embeddedRepresentationJson)
    }

    "serialize a complete Siren entity with embedded linked and fully represented sub-entities correctly" in {
      assert(Json.toJson(entity) === entityJson)
    }

    "deserialize Siren properties" in {
      assert(Json.fromJson[Properties](propsJson) === JsSuccess(props))
    }

    "deserialize Siren properties with json Array" in {
      assert(Json.fromJson[Properties](propsWithArrayJson) === JsSuccess(propsFromArray))
    }

    "deserialize Siren properties with complex json Array" in {
      assert(Json.fromJson[Properties](propsWithComplexArrayJson) === JsSuccess(propsFromComplexArray))
    }

    "deserialize Siren properties with json object" in {
      assert(Json.fromJson[Properties](propsWithJsonObjectJson) === JsSuccess(propsWithJsonObject))
    }

    "deserialize Siren classes" in {
      assert(Json.fromJson[ImmutableSeq[String]](classesJson) === JsSuccess(classes))
    }

    "deserialize a Siren embedded link" in {
      assert(Json.fromJson[Entity.EmbeddedLink](embeddedLinkJson) === JsSuccess(embeddedLink))
    }

    "deserialize a Siren embedded representation" in {
      assert {
        Json.fromJson[Entity.EmbeddedRepresentation](embeddedRepresentationJson) ===
          JsSuccess(embeddedRepresentation)
      }
    }

    "deserialize a Siren action method" in {
      assert(Json.fromJson[Action.Method](JsString("GET")) === JsSuccess(Action.Method.GET))
    }

    "deserialize a Siren action" in {
      assert(Json.fromJson[Action](actionJson) === JsSuccess(action))
    }

    "deserialize Siren links" in {
      assert(Json.fromJson[ImmutableSeq[Link]](linksJson) === JsSuccess(links))
    }

    "deserialize a complete Siren entity with embedded linked and fully represented sub-entities" in {
      assert(Json.fromJson[Entity.RootEntity](entityJson) === JsSuccess(entity))
    }

    "fail to deserialize invalid Siren action method" in {
      assert(Json.fromJson[Action.Method](JsString("foo")).isError)
      assert(Json.fromJson[Action.Method](JsNumber(23)).isError)
    }

    "fail to deserialize invalid Siren embedded representation collecting all errors" in {
      // properties allow json object so the only error is wrong type in actions
      val result = Json.fromJson[Entity.EmbeddedRepresentation](invalidEmbeddedRepresentationJson)
      inside(result) {
        case JsError(errors) ⇒
          assert(errors.size === 1)
          assert(errors.exists(_._1 === (JsPath \ "actions")(0) \ "name"))
      }
    }

  }

}
