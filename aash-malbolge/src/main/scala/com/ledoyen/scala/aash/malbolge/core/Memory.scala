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

class LazyMemory extends Memory {

  private val pointerOnLastInitializedCell = new Pointer
  private val innerMemory = Array.fill(MEMORY_SIZE) { 0 }
  var pl: Int = -1

  def setValueAt(pos: Int, value: Int) = {
    if (pos - 1 > pointerOnLastInitializedCell.toInt) initUntil(pos - 1)
    innerMemory(pos) = value
  }
  def getValue(pos: Int): Int = {
    if (pos > pointerOnLastInitializedCell.toInt) initUntil(pos)
    innerMemory(pos)
  }
  def toList: List[Int] = innerMemory.take(pointerOnLastInitializedCell.toInt).toList
  def setProgramAndInit(program: Array[Int]) = {
    Array.copy(program, 0, innerMemory, 0, program.length)
    pl = program.length
    // (-1 for 0-based index)
    pointerOnLastInitializedCell.update(program.length - 1)
  }
  def usedMemory = pointerOnLastInitializedCell.toInt + 1

  def programLength = pl

  def take(n: Int) = innerMemory.take(n)

  private def initUntil(pos: Int) = {
    for (memoryAdress <- (pointerOnLastInitializedCell.toInt + 1) to (pos)) {
      val mem_2 = innerMemory(memoryAdress - 2)
      val mem_1 = innerMemory(memoryAdress - 1)
      val crazyValue = Crazy(innerMemory(memoryAdress - 2), innerMemory(memoryAdress - 1))
      innerMemory(memoryAdress) = crazyValue
    }
    if(pointerOnLastInitializedCell.toInt >= MEMORY_SIZE) {
      throw new OutOfMemoryError(s"Malbolge memory cannot exceed $MEMORY_SIZE")
    }
    pointerOnLastInitializedCell.update(pos)
  }

  override def toString = {
    // Take elements (+1 for 0-based index) (+2 to see next 2 normally blank cells)
    val indexed = (0 to MEMORY_SIZE).toList.zip(innerMemory).take(pointerOnLastInitializedCell.toInt + 3)
    indexed.take(30).mkString("\n") + "\n...\n\n" + indexed.takeRight(30).mkString("\n")
  }

  class Pointer {
    var innerValue = -1
    
    def update(value: Int) = {
      if(value > innerValue) {
        innerValue = value
      } else {
        throw new IllegalArgumentException("Pointer cannot be decreased")
      }
    }
    
    def toInt = {
      val result = innerValue.toInt
      result
    }
    
    override def toString = s"Pointer -> $innerValue"
  }
}