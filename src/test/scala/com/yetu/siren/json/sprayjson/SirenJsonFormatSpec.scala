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

import org.scalatest.MustMatchers
import spray.json._

class SirenJsonFormatSpec extends JsonBaseSpec[JsValue] with MustMatchers with SirenJsonFormat with DefaultJsonProtocol {

  import com.yetu.siren.model._

  override protected def parseJson(jsonString: String) = jsonString.parseJson

  "SirenJsonFormat" must {
    "serialize Siren properties" in {
      props.toJson mustEqual propsJson
    }
    "deserialize Siren properties" in {
      propsJson.convertTo[Properties] mustEqual props
    }
    "serialize Siren classes" in {
      classes.list.toJson mustEqual classesJson
    }
    "deserialize Siren classes" in {
      classesJson.convertTo[List[String]] mustEqual classes.list
    }
    "serialize a Siren embedded link" in {
      embeddedLink.toJson mustEqual embeddedLinkJson
    }
    "deserialize a Siren embedded link" in {
      embeddedLinkJson.convertTo[Entity.EmbeddedLink] mustEqual embeddedLink
    }
    "serialize a Siren embedded representation" in {
      embeddedRepresentation.toJson mustEqual embeddedRepresentationJson
    }
    "deserialize a Siren embedded representation" in {
      embeddedRepresentationJson.convertTo[Entity.EmbeddedRepresentation] mustEqual embeddedRepresentation
    }
    "serialize a Siren action method" in {
      val method: Action.Method = Action.Method.GET
      method.toJson mustEqual JsString("GET")
    }
    "deserialize a Siren action method" in {
      JsString("GET").convertTo[Action.Method] mustEqual Action.Method.GET
    }
    "fail deserializing a JSON string to a Siren action method if the string is not valid" in {
      intercept[DeserializationException] {
        JsString("foo").convertTo[Action.Method]
      }
    }
    "fail deserializing JSON values to a Siren action method if that are not a JSON string representation of a method" in {
      intercept[DeserializationException] {
        JsNumber(23).convertTo[Action.Method]
      }
    }
    "serialize a Siren action" in {
      action.toJson mustEqual actionJson
    }
    "deserialize a Siren action" in {
      actionJson.convertTo[Action] mustEqual action
    }
    "serialize a Siren link" in {
      links.list.toJson mustEqual linksJson
    }
    "deserialize a Siren link" in {
      linksJson.convertTo[Seq[Link]] mustEqual links.list
    }
    "throw DeserializionException" when {
      "deserialize an empty array of Siren links" in {
        intercept[DeserializationException] { "[null]".parseJson.convertTo[Links] }
      }
      "deserialize non-array json" in {
        intercept[DeserializationException] { """{ "foo": "bar" }""".parseJson.convertTo[Links] }
      }
      "deserialize wrong type of Siren properties" in {
        intercept[DeserializationException] { """{ "xyz" : { "foo": "bar" } }""".parseJson.convertTo[Properties] }
      }
    }
    "serialize a complete Siren entity with embedded linked and fully represented sub-entities correctly" in {
      entity.toJson mustEqual entityJson
    }
    "deserialize a complete Siren entity with embedded linked and fully represented sub-entities" in {
      entityJson.convertTo[Entity.RootEntity] mustEqual entity
    }
  }

}
