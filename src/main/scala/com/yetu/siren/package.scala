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

package com.yetu

package object siren {

  import model.Entity.RootEntity

  /**
   * Type class whose instances are able to be written as Siren root entities.
   * @tparam A the type that is an instance of this type class
   */
  trait SirenRootEntityWriter[A] {
    /**
     * Returns a Siren [[model.Entity.RootEntity]] representation of the given object.
     * @param a the object to be written as a Siren [[model.Entity.RootEntity]]
     */
    def toSiren(a: A): RootEntity
  }

  /**
   * Implicit class that provides operations on types that are instances of the [[SirenRootEntityWriter]]
   * type class.
   */
  implicit class ToSirenRootEntityWriterOps[A](a: A) {
    /**
     * Returns a Siren [[model.Entity.RootEntity]] from the wrapped object.
     */
    def rootEntity(implicit writer: SirenRootEntityWriter[A]): RootEntity = {
      Siren.asRootEntity(a)
    }
  }

  /**
   * Entry point for interacting with the Siren library.
   */
  object Siren {
    /**
     * Returns a Siren [[model.Entity.RootEntity]] from the provided value
     */
    def asRootEntity[A](a: A)(implicit writer: SirenRootEntityWriter[A]): RootEntity =
      writer toSiren a
  }

}
