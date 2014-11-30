package com.ledoyen.scala.aash.malbolge.core

trait Memory {
  val MEMORY_SIZE = 59049

  // Shorthands
  def apply(pos: Int) = getValue(pos)
  def update(pos: Int, value: Int) = setValueAt(pos, value)

  def setValueAt(pos: Int, value: Int)
  def getValue(pos: Int): Int
  def toList: List[Int]
  def setProgramAndInit(program: Array[Int])

  def usedMemory: Int
  def programLength: Int

  def take(n: Int): Array[Int]
}
