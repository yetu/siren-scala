package com.yetu.siren

package object json {

  /**
   * Collects only those of the given options that are defined, removing the others.
   * @param opts one or more options of type A
   * @tparam A the type of the given options
   */
  private[json] def collectSome[A](opts: Option[A]*): List[A] =
    (opts collect { case Some(field) ⇒ field }).toList

}
