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

import spray.json._

private[sprayjson] object SprayJsonReadSupport {

  type Seq[+A] = scala.collection.immutable.Seq[A]
  val Seq = scala.collection.immutable.Seq

  implicit class RichJsObject(val obj: JsObject) extends AnyVal {
    def fieldOpt(fieldName: String): Option[JsValue] = obj.getFields(fieldName).toList match {
      case Seq(value) ⇒ Some(value)
      case _          ⇒ None
    }
    def field(fieldName: String): JsValue = obj.getFields(fieldName).toList match {
      case Seq(value) ⇒ value
      case x          ⇒ throwDesEx(s"No value for fieldName $fieldName in $obj: $x")
    }
    def \(fieldName: String): JsValue = field(fieldName)
    def \?(fieldName: String): Option[JsValue] = fieldOpt(fieldName)
  }

  implicit class RichJsValue(val v: JsValue) extends AnyVal {
    def asString: String = v match {
      case JsString(s) ⇒ s
      case x           ⇒ throwDesEx(s"$x is not a JSON string")
    }
    def asStringSeq: Seq[String] = v match {
      case JsArray(elements) ⇒
        elements map {
          case JsString(s) ⇒ s
          case x           ⇒ throwDesEx(s"$x is not a JSON string")
        }
      case x ⇒ throwDesEx(s"$x is not a JSON array")
    }
  }

  /**
   * Throws a Spray [[DeserializationException]] with the given message.
   */
  private[json] def throwDesEx(msg: String) = throw new DeserializationException(msg)

}
