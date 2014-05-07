package com.ledoyen.scala.chess

abstract class Piece {
  def toChar(color: Color): Char
}

case object Pawn extends Piece {
  def toChar(color: Color) = if(color == White) 0x2659.toChar else 0x265F.toChar
}

case object Knight extends Piece {
  def toChar(color: Color) = if(color == White) 0x2658.toChar else 0x265E.toChar
}

case object Bishop extends Piece {
  def toChar(color: Color) = if(color == White) 0x2657.toChar else 0x265D.toChar
}

case object Rook extends Piece {
  def toChar(color: Color) = if(color == White) 0x2656.toChar else 0x265C.toChar
}

case object Queen extends Piece {
  def toChar(color: Color) = if(color == White) 0x2655.toChar else 0x265B.toChar
}

case object King extends Piece {
  def toChar(color: Color) = if(color == White) 0x2654.toChar else 0x265A.toChar
}
