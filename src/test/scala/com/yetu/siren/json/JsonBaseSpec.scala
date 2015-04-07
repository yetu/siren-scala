package com.yetu.siren.json

import com.yetu.siren.model._
import org.scalatest.WordSpec

trait JsonBaseSpec[JsonBaseType] extends WordSpec {

  protected def parseJson(jsonString: String): JsonBaseType

  protected lazy val propsJson = parseJson(propsJsonString)

  protected lazy val propsWithArrayJson = parseJson(propsJsonStringWithArray)

  protected lazy val propsWithComplexArrayJson = parseJson(propsJsonStringWithComplexArray)

  protected lazy val propsWithJsonObjectJson = parseJson(propsJsonStringWithNestedJsonObject)

  protected lazy val classesJson = parseJson(classesJsonString)

  protected lazy val embeddedLinkJson = parseJson(embeddedLinkJsonString)

  protected lazy val embeddedRepresentationJson = parseJson(embeddedRepresentationJsonString)

  protected lazy val invalidEmbeddedRepresentationJson = parseJson(invalidEmbeddedRepresentationJsonString)

  protected lazy val actionJson = parseJson(actionJsonString)

  protected lazy val linksJson = parseJson(linksJsonString)

  protected lazy val entityJson = parseJson(entityJsonString)

  protected lazy val embeddedLinkJsonString =
    """
      {
        "class": [ "items", "collection" ],
        "rel": [ "http://x.io/rels/order-items" ],
        "href": "http://api.x.io/orders/42/items"
      }
    """.stripMargin

  protected lazy val embeddedLink = Entity.EmbeddedLink(
    rel = List("http://x.io/rels/order-items"),
    href = "http://api.x.io/orders/42/items",
    classes = Some(List("items", "collection"))
  )

  protected lazy val embeddedRepresentationJsonString =
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
        ],
        "title": "Customer information"
      }
    """.stripMargin

  protected lazy val invalidEmbeddedRepresentationJsonString =
    """
      {
        "class": [ "info", "customer" ],
        "rel": [ "http://x.io/rels/customer" ],
        "properties": {
          "customerId": [],
          "name": {}
        },
        "actions": [
          {
            "name": true,
            "href": "http://api.x.io/customer/pj123/name",
            "fields": [
              { "name": "name", "type": "text" }
            ]
          }
        ],
        "links": [
          { "rel": [ "self" ], "href": "http://api.x.io/customers/pj123" }
        ]
      }
    """.stripMargin

  protected lazy val embeddedRepresentation = Entity.EmbeddedRepresentation(
    classes = Some(List("info", "customer")),
    rel = List("http://x.io/rels/customer"),
    properties = Some(List(
      Property("customerId", Property.StringValue("pj123")),
      Property("name", Property.StringValue("Peter Joseph")))),
    entities = Some(List(
      Entity.EmbeddedLink(
        classes = Some("company" :: Nil),
        rel = "http://x.io/rels/company" :: Nil,
        href = "http://api.x.io/customer/pj123/company"
      )
    )),
    actions = Some(List(
      Action(
        name = "set-name",
        href = "http://api.x.io/customer/pj123/name",
        title = Some("Set Customer's Name"),
        method = Some(Action.Method.POST),
        `type` = Some(Action.Encoding.`application/json`),
        fields = Some(List(
          Action.Field(name = "name", `type` = Action.Field.Type.`text`)
        ))
      )
    )),
    links = Some(List(Link(href = "http://api.x.io/customers/pj123", rel = "self" :: Nil))),
    title = Some("Customer information")
  )

  protected lazy val props: Properties = List(
    Property("orderNumber", Property.NumberValue(42)),
    Property("itemCount", Property.NumberValue(3)),
    Property("status", Property.StringValue("pending")),
    Property("foo", Property.BooleanValue(value = false)),
    Property("bar", Property.NullValue))

  protected lazy val propsWithArray: Properties = List(
    Property("temperature", Property.NumberValue(42)),
    Property("mode", Property.NumberValue(3)),
    Property("capabilities", Property.JsArrayValue(Seq(
      Property.StringValue("dimmable"),
      Property.StringValue("switchable"))
    )),
    Property("status", Property.StringValue("pending")),
    Property("isOn", Property.BooleanValue(value = true)))

  protected lazy val propsWithComplexArray: Properties = List(
    Property("temperature", Property.NumberValue(42)),
    Property("mode", Property.NumberValue(3)),
    Property("colors", Property.JsArrayValue(Seq(
      Property.JsObjectValue(Seq(
        "name" -> Property.StringValue("superred"),
        "hue" -> Property.NumberValue(42),
        "saturation" -> Property.NumberValue(56),
        "brightness" -> Property.NumberValue(10)
      )),
      Property.JsObjectValue(Seq(
        "name" -> Property.StringValue("yetugreen"),
        "hue" -> Property.NumberValue(45),
        "saturation" -> Property.NumberValue(23),
        "brightness" -> Property.NumberValue(5)
      ))
    ))),
    Property("status", Property.StringValue("pending")),
    Property("isOn", Property.BooleanValue(value = true)))

  protected lazy val propsWithJsonObject: Properties = List(
    Property("temperature", Property.NumberValue(42)),
    Property("mode", Property.NumberValue(3)),
    Property("color", Property.JsObjectValue(Seq(
      "name" -> Property.StringValue("superred"),
      "hue" -> Property.NumberValue(42),
      "saturation" -> Property.NumberValue(56),
      "brightness" -> Property.NumberValue(10)
    ))),
    Property("status", Property.StringValue("pending")),
    Property("isOn", Property.BooleanValue(value = true)))

  protected val properties = List(
    Property("orderNumber", Property.NumberValue(42)),
    Property("itemCount", Property.NumberValue(3)),
    Property("status", Property.StringValue("pending")))

  protected lazy val classesJsonString =
    """[ "info", "customer" ]""".stripMargin
  protected lazy val classes = List("info", "customer")

  protected lazy val links = List(
    Link(href = "http://api.x.io/orders/42", rel = "self" :: Nil),
    Link(href = "http://api.x.io/orders/41", rel = "previous" :: Nil),
    Link(href = "http://api.x.io/orders/43", rel = "next" :: Nil)
  )

  protected lazy val action = Action(
    name = "add-item",
    href = "http://api.x.io/orders/42/items",
    title = Some("Add Item"),
    method = Some(Action.Method.POST),
    `type` = Some(Action.Encoding.`application/x-www-form-urlencoded`),
    fields = Some(List(
      Action.Field(name = "orderNumber", `type` = Action.Field.Type.`hidden`, value = Some("42")),
      Action.Field(name = "productCode", `type` = Action.Field.Type.`text`),
      Action.Field(name = "quantity", `type` = Action.Field.Type.`number`)))
  )

  protected lazy val propsJsonString =
    """
      {
        "orderNumber": 42,
        "itemCount": 3,
        "status": "pending",
        "foo": false,
        "bar": null
      }
    """.stripMargin

  protected lazy val propsJsonStringWithArray =
    """
      {
        "temperature": 42,
        "mode": 3,
        "capabilities" : ["dimmable", "switchable"],
        "status": "pending",
        "isOn": true
      }
    """.stripMargin

  protected lazy val propsJsonStringWithComplexArray =
    """
      {
        "temperature": 42,
        "mode": 3,
        "colors" : [{
          "name" : "superred",
          "hue": 42,
          "saturation" : 56,
          "brightness" : 10
        },
        {
          "name" : "yetugreen",
          "hue": 45,
          "saturation" : 23,
          "brightness" : 5
        }],
        "status": "pending",
        "isOn": true
      }
    """.stripMargin

  protected lazy val propsJsonStringWithNestedJsonObject =
    """
      {
        "temperature": 42,
        "mode": 3,
        "color": {
          "name" : "superred",
          "hue": 42,
          "saturation" : 56,
          "brightness" : 10
        },
        "status": "pending",
        "isOn": true
      }
    """.stripMargin

  protected lazy val linksJsonString =
    """
      [
        { "rel": [ "self" ], "href": "http://api.x.io/orders/42" },
        { "rel": [ "previous" ], "href": "http://api.x.io/orders/41" },
        { "rel": [ "next" ], "href": "http://api.x.io/orders/43" }
      ]
    """.stripMargin

  protected lazy val actionJsonString =
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
    """.stripMargin

  protected val entity = Entity.RootEntity(
    classes = Some("order" :: Nil),
    properties = Some(properties),
    entities = Some(List(embeddedLink, embeddedRepresentation)),
    actions = Some(action :: Nil),
    links = Some(links),
    title = Some("Order number 42")
  )

  protected lazy val entityJsonString =
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
                ],
                "title": "Customer information"
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
            ],
            "title": "Order number 42"
          }
    """.stripMargin

}
