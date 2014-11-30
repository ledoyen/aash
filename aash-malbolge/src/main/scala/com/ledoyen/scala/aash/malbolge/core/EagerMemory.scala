package com.ledoyen.scala.aash.malbolge.core

class EagerMemory extends Memory {

  private val innerMemory = Array.fill(MEMORY_SIZE) { 0 }
  var pl: Int = -1

  def setValueAt(pos: Int, value: Int) = {
    innerMemory(pos) = value
  }

  def getValue(pos: Int): Int = {
    innerMemory(pos)
  }

  def toList: List[Int] = innerMemory.toList

  def setProgramAndInit(program: Array[Int]) = {
    Array.copy(program, 0, innerMemory, 0, program.length)
    pl = program.length

    for (memoryAdress <- program.length to (MEMORY_SIZE - 1)) {
      val crazyValue = Crazy(innerMemory(memoryAdress - 2), innerMemory(memoryAdress - 1))
      innerMemory(memoryAdress) = crazyValue
    }
  }

  def usedMemory = MEMORY_SIZE

  def programLength = pl

  def take(n: Int) = innerMemory.take(n)
}