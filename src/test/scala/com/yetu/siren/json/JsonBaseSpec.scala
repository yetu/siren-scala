package com.yetu.siren.json

import com.yetu.siren.model.{ Property, Action, Link }
import org.scalatest.WordSpec

import scalaz.NonEmptyList
import scalaz.std.option._

trait JsonBaseSpec extends WordSpec {

  import scalaz.syntax.nel._

  protected lazy val props = NonEmptyList(
    Property("orderNumber", Property.NumberValue(42)),
    Property("itemCount", Property.NumberValue(3)),
    Property("status", Property.StringValue("pending")),
    Property("foo", Property.BooleanValue(value = false)),
    Property("bar", Property.NullValue))

  protected lazy val classesJsonString =
    """[ "info", "customer" ]""".stripMargin
  protected lazy val classes = NonEmptyList("info", "customer")

  protected lazy val links = NonEmptyList(
    Link(href = "http://api.x.io/orders/42", rel = "self".wrapNel),
    Link(href = "http://api.x.io/orders/41", rel = "previous".wrapNel),
    Link(href = "http://api.x.io/orders/43", rel = "next".wrapNel)
  )

  protected lazy val action = Action(
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

}
