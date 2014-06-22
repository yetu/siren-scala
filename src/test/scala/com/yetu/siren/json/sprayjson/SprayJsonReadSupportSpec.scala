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

import org.scalatest.{ WordSpec, MustMatchers }
import scalaz.NonEmptyList
import spray.json._

class SprayJsonReadSupportSpec extends WordSpec with MustMatchers {

  import SprayJsonReadSupport._

  "SprayJsonReadSupport" must {
    "read existing JSON field" in {
      val json = JsObject("foo" -> JsString("bar"))
      (json \? "foo") mustEqual Some(JsString("bar"))
    }
    "read non-existing JSON field as None" in {
      val json = JsObject()
      (json \? "foo") mustEqual None
    }
    "read an existing JSON string field" in {
      val json = JsObject("foo" -> JsString("bar"))
      (json \ "foo").asString mustEqual "bar"
    }
    "read an existing JSON array of strings" in {
      val json = JsObject("foo" -> JsArray(JsString("bar"), JsString("baz")))
      (json \ "foo").asStringSeq mustEqual Seq("bar", "baz")
    }
    "read an existing, non-empty JSON array as NonEmptyList" in {
      val json = JsObject("foo" -> JsArray(JsString("bar"), JsString("baz")))
      (json \ "foo").asStringNel mustEqual NonEmptyList("bar", "baz")
    }
    "fail if existing JSON field is read as string but not a string" in {
      val json = JsObject("foo" -> JsNumber(23))
      intercept[DeserializationException] {
        (json \ "foo").asString
      }
    }
    "fail if existing JSON field is read as array of strings, but has wrong types in it" in {
      val json = JsObject("foo" -> JsArray(JsString("bar"), JsNumber(5)))
      intercept[DeserializationException] {
        (json \ "foo").asStringSeq
      }
    }
    "fail if existing JSON field is read as non-empty list of strings, but has wrong types in it" in {
      val json = JsObject("foo" -> JsArray(JsNumber(5)))
      intercept[DeserializationException] {
        (json \ "foo").asStringNel
      }
    }
    "fail if existing JSON field is read as non-empty list, but JSON array is empty" in {
      val json = JsObject("foo" -> JsArray())
      intercept[DeserializationException] {
        (json \ "foo").asStringNel
      }
    }
  }

}
