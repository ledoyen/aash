package com.ledoyen.scala.aash.tool

object Options {

  def option[T](condition: Boolean, o: => T) = {
    if (!condition) None else Some(o)
  }
}
