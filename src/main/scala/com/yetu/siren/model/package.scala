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

/**
 * The model package, containing a complete model of Siren entities in terms of
 * algebraic data types).
 */
package object model {

  import collection.immutable
  import immutable.{Seq => ImmutableSeq}

  type Properties = ImmutableSeq[Property]

  /**
   * Companion object for the property type.
   */
  object Property {
    /** Sum type for property values */
    sealed trait Value
    /**
     * A property value that is string-typed.
     * @param value the string value
     */
    case class StringValue(value: String) extends Value
    /**
     * A property value that is number-typed.
     * @param value the number value
     */
    case class NumberValue(value: BigDecimal) extends Value
    /**
     * A property value that is boolean-typed.
     * @param value the boolean value
     */
    case class BooleanValue(value: Boolean) extends Value

    case class JsObjectValue(fields : Seq[(String, Value)]) extends Value

    case class JsArrayValue(value: Seq[Value]) extends Value

    /**
     * The property value that represents a non-existing value.
     */
    case object NullValue extends Value
  }
  /**
   * Representation of a property of a Siren entity.
   * @param name the name of the property
   * @param value the value of the property
   */
  case class Property(name: String, value: Property.Value)

  /**
   * Companion object of the [[Action]] type.
   */
  object Action {

    /**
     * A sum type that represents a method that can be specified for an action.
     * The available methods are a subset of the HTTP verbs.
     */
    sealed trait Method extends Enum.Val[Method]
    /** Companion object of the [[Method]] trait. **/
    object Method extends Enum[Method] {
      /** The HTTP GET method */
      case object GET extends Method
      /** The HTTP PUT method */
      case object PUT extends Method
      /** The HTTP POST method */
      case object POST extends Method
      /** The HTTP DELETE method */
      case object DELETE extends Method
      /** The HTTP PATCH method */
      case object PATCH extends Method

      override val values = List(GET, PUT, POST, DELETE, PATCH)
    }

    /**
     * Sum type that encompasses all the supported encodings for actions in Siren.
     */
    sealed trait Encoding extends Enum.Val[Encoding]
    /** Companion object for the [[Encoding]] trait. */
    object Encoding extends Enum[Encoding] {
      /** The application/x-www-form-urlencoded encoding for an action's payload. */
      case object `application/x-www-form-urlencoded` extends Encoding
      /** The application/json encoding for an action's payload. */
      case object `application/json` extends Encoding

      override val values = List(`application/json`, `application/x-www-form-urlencoded`)
    }

    /**
     * Companion object for the [[Field]] type.
     */
    object Field {

      /**
       * A sum type for all possible types of a field.
       */
      sealed trait Type extends Enum.Val[Type]

      /**
       * Companion object for the [[Type]] trait.
       */
      object Type extends Enum[Type] {
        case object `hidden` extends Type
        case object `text` extends Type
        case object `search` extends Type
        case object `tel` extends Type
        case object `url` extends Type
        case object `email` extends Type
        case object `password` extends Type
        case object `datetime` extends Type
        case object `date` extends Type
        case object `month` extends Type
        case object `week` extends Type
        case object `time` extends Type
        case object `datetime-local` extends Type
        case object `number` extends Type
        case object `range` extends Type
        case object `color` extends Type
        case object `checkbox` extends Type
        case object `radio` extends Type
        case object `file` extends Type
        case object `image` extends Type
        case object `reset` extends Type
        case object `button` extends Type

        override val values = List(
          `hidden`, `text`, `search`, `tel`, `url`, `email`, `password`, `datetime`,
          `date`, `month`, `week`, `time`, `datetime-local`, `number`, `range`,
          `color`, `checkbox`, `radio`, `file`, `image`, `reset`, `button`)
      }
    }
    /**
     * A field that specifies part of the payload for an action.
     * @param name the name of the field
     * @param `type` the type of the field
     * @param value the optional value of the field; only makes sense for certain types
     *              of fields;
     * @param title an optional textual annotation for the field
     */
    case class Field(
      name: String,
      `type`: Field.Type,
      value: Option[String] = None,
      title: Option[String] = None)

  }

  /**
   * An action that can be specified for an entity in Siren.
   * @param name the name of the action
   * @param href the URL to be used for executing the action
   * @param classes the optional classes of the action
   * @param title optional descriptive text about the action
   * @param method the HTTP method to be used when executing the action
   * @param `type` the encoding to be used for the payload when sending the request to the
   *               URL of this action
   * @param fields the fields specified for this action
   */
  case class Action(
    name: String,
    href: String,
    classes: Option[ImmutableSeq[String]] = None,
    title: Option[String] = None,
    method: Option[Action.Method] = None,
    `type`: Option[Action.Encoding] = None,
    fields: Option[ImmutableSeq[Action.Field]] = None)

  /**
   * A navigational link that can be specified for an entity in Siren.
   * @param rel the relationship of this link to the entity
   * @param href the URL of the linked resource
   * @param title an optional text describing the nature of the link
   */
  case class Link(rel: ImmutableSeq[String], href: String, title: Option[String] = None)

  /**
   * A Siren entity.
   */
  sealed trait Entity {
    /** the optional classes of this entity */
    def classes: Option[ImmutableSeq[String]]
  }
  /**
   * An embedded entity, i.e. a sub entity of a [[Entity.RootEntity]]
   */
  sealed trait EmbeddedEntity extends Entity {
    /** the relationship between the parent entity  and this sub entity */
    def rel: ImmutableSeq[String]
  }
  /**
   * A fully represented entity.
   */
  sealed trait EntityRepresentation extends Entity {
    /** the optional properties of this entity */
    def properties: Option[Properties]
    /** the optional actions specified for this entity */
    def actions: Option[ImmutableSeq[Action]]
    /** the optional links specified for this entity */
    def links: Option[ImmutableSeq[Link]]
    /** an optional descriptive text about this entity */
    def title: Option[String]
  }

  /**
   * Companion object for the [[Entity]] trait.
   */
  object Entity {

    /**
     * A root, i.e. top-level, Siren entity.
     */
    case class RootEntity(
      classes: Option[ImmutableSeq[String]] = None,
      properties: Option[Properties] = None,
      entities: Option[ImmutableSeq[EmbeddedEntity]] = None,
      actions: Option[ImmutableSeq[Action]] = None,
      links: Option[ImmutableSeq[Link]] = None,
      title: Option[String] = None) extends EntityRepresentation
    /**
     * A sub entity that is only an embedded link, not a a full representation of the
     * sub entity.
     * @param rel the relationship between the parent entity  and this sub entity
     * @param href the URL of the linked sub entity
     * @param classes the optional classes of this entity
     */
    case class EmbeddedLink(
      rel: ImmutableSeq[String],
      href: String,
      classes: Option[ImmutableSeq[String]] = None) extends EmbeddedEntity
    /**
     * A full representation of an embedded sub entity.
     */
    case class EmbeddedRepresentation(
      rel: ImmutableSeq[String],
      classes: Option[ImmutableSeq[String]] = None,
      properties: Option[Properties] = None,
      entities: Option[ImmutableSeq[EmbeddedEntity]] = None,
      actions: Option[ImmutableSeq[Action]] = None,
      links: Option[ImmutableSeq[Link]] = None,
      title: Option[String] = None) extends EmbeddedEntity with EntityRepresentation
  }

  /**
   * Base type for enumerations.
   * @tparam A the type of the enumerated values
   */
  trait Enum[A <: Enum.Val[A]] {
    /**
     * All values of this enumeration in order.
     */
    def values: List[A]
    /**
     * Returns the enumeration value with the specified name as a [[Some]], or [[None]] if no
     * enumeration value with that name exists.
     * @param name the name for which a corresponding enumeration value is to be returned
     */
    def forName(name: String): Option[A] = values find (_.name == name)

    def unapply(name: String): Option[A] = forName(name)
  }
  /**
   * Companion object of the [[Enum]] trait.
   */
  object Enum {
    /**
     * Base trait for enumerated values.
     * @tparam A the type of the enumerated values
     */
    trait Val[A] {
      /**
       * the name of the enumeration value
       */
      def name: String = toString
    }
  }
}
