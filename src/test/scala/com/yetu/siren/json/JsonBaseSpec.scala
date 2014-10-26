package com.yetu.siren.json

import com.yetu.siren.model._
import org.scalatest.WordSpec

import scalaz.NonEmptyList
import scalaz.std.option._

trait JsonBaseSpec[JsonBaseType] extends WordSpec {

  protected def parseJson(jsonString: String): JsonBaseType

  protected lazy val propsJson = parseJson(propsJsonString)

  protected lazy val classesJson = parseJson(classesJsonString)

  protected lazy val embeddedLinkJson = parseJson(embeddedLinkJsonString)

  protected lazy val embeddedRepresentationJson = parseJson(embeddedRepresentationJsonString)

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
    classes = some(List("items", "collection"))
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

  protected lazy val embeddedRepresentation = Entity.EmbeddedRepresentation(
    classes = some(List("info", "customer")),
    rel = List("http://x.io/rels/customer"),
    properties = some(List(
      Property("customerId", Property.StringValue("pj123")),
      Property("name", Property.StringValue("Peter Joseph")))),
    entities = some(List(
      Entity.EmbeddedLink(
        classes = some("company" :: Nil),
        rel = "http://x.io/rels/company" :: Nil,
        href = "http://api.x.io/customer/pj123/company"
      )
    )),
    actions = some(List(
      Action(
        name = "set-name",
        href = "http://api.x.io/customer/pj123/name",
        title = some("Set Customer's Name"),
        method = some(Action.Method.POST),
        `type` = some(Action.Encoding.`application/json`),
        fields = some(List(
          Action.Field(name = "name", `type` = Action.Field.Type.`text`)
        ))
      )
    )),
    links = some(List(Link(href = "http://api.x.io/customers/pj123", rel = "self" :: Nil))),
    title = some("Customer information")
  )

  protected lazy val props: Properties = List(
    Property("orderNumber", Property.NumberValue(42)),
    Property("itemCount", Property.NumberValue(3)),
    Property("status", Property.StringValue("pending")),
    Property("foo", Property.BooleanValue(value = false)),
    Property("bar", Property.NullValue))

  protected val properties = List(
    Property("orderNumber", Property.NumberValue(42)),
    Property("itemCount", Property.NumberValue(3)),
    Property("status", Property.StringValue("pending")))

  protected lazy val classesJsonString =
    """[ "info", "customer" ]""".stripMargin
  protected lazy val classes = NonEmptyList("info", "customer")

  protected lazy val links = List(
    Link(href = "http://api.x.io/orders/42", rel = "self" :: Nil),
    Link(href = "http://api.x.io/orders/41", rel = "previous" :: Nil),
    Link(href = "http://api.x.io/orders/43", rel = "next" :: Nil)
  )

  protected lazy val action = Action(
    name = "add-item",
    href = "http://api.x.io/orders/42/items",
    title = some("Add Item"),
    method = some(Action.Method.POST),
    `type` = some(Action.Encoding.`application/x-www-form-urlencoded`),
    fields = some(List(
      Action.Field(name = "orderNumber", `type` = Action.Field.Type.`hidden`, value = some("42")),
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
    classes = some("order" :: Nil),
    properties = some(properties),
    entities = some(List(embeddedLink, embeddedRepresentation)),
    actions = some(action :: Nil),
    links = some(links),
    title = some("Order number 42")
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
