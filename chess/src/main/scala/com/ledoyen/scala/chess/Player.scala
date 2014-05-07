package com.ledoyen.scala.chess

abstract class Color
case object White extends Color
case object Black extends Color

object Player {
  def apply(color: Color) = new Player(color)
}
class Player(val color: Color) {

  
}