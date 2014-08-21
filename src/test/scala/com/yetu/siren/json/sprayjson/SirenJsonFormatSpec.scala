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

import org.scalatest.{ MustMatchers, WordSpec }
import spray.json._

class SirenJsonFormatSpec extends WordSpec with MustMatchers with SirenJsonFormat with DefaultJsonProtocol {

  import scalaz.std.option._
  import scalaz.syntax.nel._
  import scalaz.NonEmptyList
  import com.yetu.siren.model._

  private val propsJson =
    """
      {
        "orderNumber": 42,
        "itemCount": 3,
        "status": "pending",
        "foo": false,
        "bar": null
      }
    """.stripMargin.parseJson
  private val props = NonEmptyList(
    Property("orderNumber", Property.NumberValue(42)),
    Property("itemCount", Property.NumberValue(3)),
    Property("status", Property.StringValue("pending")),
    Property("foo", Property.BooleanValue(value = false)),
    Property("bar", Property.NullValue))

  private val properties = NonEmptyList(
    Property("orderNumber", Property.NumberValue(42)),
    Property("itemCount", Property.NumberValue(3)),
    Property("status", Property.StringValue("pending")))

  private val classesJson =
    """[ "info", "customer" ]""".stripMargin.parseJson
  private val classes = NonEmptyList("info", "customer")

  private val embeddedLinkJson =
    """
      {
        "class": [ "items", "collection" ],
        "rel": [ "http://x.io/rels/order-items" ],
        "href": "http://api.x.io/orders/42/items"
      }
    """.stripMargin.parseJson
  private val embeddedLink = Entity.EmbeddedLink(
    rel = "http://x.io/rels/order-items".wrapNel,
    href = "http://api.x.io/orders/42/items",
    classes = some(NonEmptyList("items", "collection"))
  )

  private val embeddedRepresentationJson =
    """
      {
        "class": [ "info", "customer" ],
        "rel": [ "http://x.io/rels/customer" ],
        "properties": {
          "customerId": "pj123",
          "name": "Peter Joseph"
        },
        "entities": [
          {
            "class": [ "company" ],
            "rel": [ "http://x.io/rels/company" ],
            "href": "http://api.x.io/customer/pj123/company"
          }
        ],
        "actions": [
          {
            "name": "set-name",
            "title": "Set Customer's Name",
            "method": "POST",
            "href": "http://api.x.io/customer/pj123/name",
            "type": "application/json",
            "fields": [
              { "name": "name", "type": "text" }
            ]
          }
        ],
        "links": [
          { "rel": [ "self" ], "href": "http://api.x.io/customers/pj123" }
        ]
      }
    """.stripMargin.parseJson
  private val embeddedRepresentation = Entity.EmbeddedRepresentation(
    classes = some(NonEmptyList("info", "customer")),
    rel = "http://x.io/rels/customer".wrapNel,
    properties = some(NonEmptyList(
      Property("customerId", Property.StringValue("pj123")),
      Property("name", Property.StringValue("Peter Joseph")))),
    entities = some(List(
      Entity.EmbeddedLink(
        classes = some("company".wrapNel),
        rel = "http://x.io/rels/company".wrapNel,
        href = "http://api.x.io/customer/pj123/company"
      )
    )),
    actions = some(NonEmptyList(
      Action(
        name = "set-name",
        href = "http://api.x.io/customer/pj123/name",
        title = some("Set Customer's Name"),
        method = some(Action.Method.POST),
        `type` = some(Action.Encoding.`application/json`),
        fields = some(NonEmptyList(
          Action.Field(name = "name", `type` = Action.Field.Type.`text`)
        ))
      )
    )),
    links = some(Link(href = "http://api.x.io/customers/pj123", rel = "self".wrapNel).wrapNel)
  )

  private val actionJson =
    """
      {
        "name": "add-item",
        "title": "Add Item",
        "method": "POST",
        "href": "http://api.x.io/orders/42/items",
        "type": "application/x-www-form-urlencoded",
        "fields": [
          { "name": "orderNumber", "type": "hidden", "value": "42" },
          { "name": "productCode", "type": "text" },
          { "name": "quantity", "type": "number" }
        ]
      }
    """.stripMargin.parseJson

  private val linksJson =
    """
      [
        { "rel": [ "self" ], "href": "http://api.x.io/orders/42" },
        { "rel": [ "previous" ], "href": "http://api.x.io/orders/41" },
        { "rel": [ "next" ], "href": "http://api.x.io/orders/43" }
      ]
    """.stripMargin.parseJson
  private val links = NonEmptyList(
    Link(href = "http://api.x.io/orders/42", rel = "self".wrapNel),
    Link(href = "http://api.x.io/orders/41", rel = "previous".wrapNel),
    Link(href = "http://api.x.io/orders/43", rel = "next".wrapNel)
  )

  private val action = Action(
    name = "add-item",
    href = "http://api.x.io/orders/42/items",
    title = some("Add Item"),
    method = some(Action.Method.POST),
    `type` = some(Action.Encoding.`application/x-www-form-urlencoded`),
    fields = some(NonEmptyList(
      Action.Field(name = "orderNumber", `type` = Action.Field.Type.`hidden`, value = some("42")),
      Action.Field(name = "productCode", `type` = Action.Field.Type.`text`),
      Action.Field(name = "quantity", `type` = Action.Field.Type.`number`)))
  )

  private val entity = Entity.RootEntity(
    classes = some("order".wrapNel),
    properties = some(properties),
    entities = some(List(embeddedLink, embeddedRepresentation)),
    actions = some(action.wrapNel),
    links = some(links)
  )

  val entityJson =
    """
          {
            "class": [ "order" ],
            "properties": {
                "orderNumber": 42,
                "itemCount": 3,
                "status": "pending"
            },
            "entities": [
              {
                "class": [ "items", "collection" ],
                "rel": [ "http://x.io/rels/order-items" ],
                "href": "http://api.x.io/orders/42/items"
              },
              {
                "class": [ "info", "customer" ],
                "rel": [ "http://x.io/rels/customer" ],
                "properties": {
                  "customerId": "pj123",
                  "name": "Peter Joseph"
                },
                "entities": [
                  {
                    "class": [ "company" ],
                    "rel": [ "http://x.io/rels/company" ],
                    "href": "http://api.x.io/customer/pj123/company"
                  }
                ],
                "actions": [
                  {
                    "name": "set-name",
                    "title": "Set Customer's Name",
                    "method": "POST",
                    "href": "http://api.x.io/customer/pj123/name",
                    "type": "application/json",
                    "fields": [
                      { "name": "name", "type": "text" }
                    ]
                  }
                ],
                "links": [
                  { "rel": [ "self" ], "href": "http://api.x.io/customers/pj123" }
                ]
              }
            ],
            "actions": [
              {
                "name": "add-item",
                "title": "Add Item",
                "method": "POST",
                "href": "http://api.x.io/orders/42/items",
                "type": "application/x-www-form-urlencoded",
                "fields": [
                  { "name": "orderNumber", "type": "hidden", "value": "42" },
                  { "name": "productCode", "type": "text" },
                  { "name": "quantity", "type": "number" }
                ]
              }
            ],
            "links": [
              { "rel": [ "self" ], "href": "http://api.x.io/orders/42" },
              { "rel": [ "previous" ], "href": "http://api.x.io/orders/41" },
              { "rel": [ "next" ], "href": "http://api.x.io/orders/43" }
            ]
          }
    """.stripMargin.parseJson

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
    "serialize a complete Siren entity with embedded linked and fully represented sub-entities correctly" in {
      entity.toJson mustEqual entityJson
    }
    "deserialize a complete Siren entity with embedded linked and fully represented sub-entities" in {
      entityJson.convertTo[Entity.RootEntity] mustEqual entity
    }
  }

}
