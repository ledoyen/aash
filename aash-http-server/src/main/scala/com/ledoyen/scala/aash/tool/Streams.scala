package com.ledoyen.scala.aash.tool

object Streams {

  def continually[A](elem: => A, condition: => Boolean): Stream[A] = {
    if (condition) {
      Stream.cons(elem, continually(elem, condition))
    } else Stream.Empty
  }
}