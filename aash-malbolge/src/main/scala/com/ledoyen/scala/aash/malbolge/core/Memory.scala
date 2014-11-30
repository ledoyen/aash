package com.ledoyen.scala.aash.malbolge.core

trait Memory {
  val MEMORY_SIZE = 59049

  var access = 0

  // Shorthands
  def apply(pos: Int) = { access = access + 1; getValue(pos) }
  def update(pos: Int, value: Int) = { access = access + 1; setValueAt(pos, value) }

  def setValueAt(pos: Int, value: Int)
  def getValue(pos: Int): Int
  def toList: List[Int]
  def setProgramAndInit(program: Array[Int])

  def usedMemory: Int
  def programLength: Int

  def take(n: Int): Array[Int]
}
