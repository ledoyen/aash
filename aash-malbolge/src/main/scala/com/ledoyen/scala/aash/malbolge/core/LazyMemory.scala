package com.ledoyen.scala.aash.malbolge.core

class LazyMemory extends Memory {

  private val innerMemory = new Array[Int](MEMORY_SIZE) //Array.fill(MEMORY_SIZE) { 0 }
  
  private var pl = 0

  private var initializedSize = 0
  private var initializedN = 0
  private var initializedN_1 = 0

  def setValueAt(pos: Int, value: Int) = {
    if(pos >= initializedSize) {
      initUntil(pos)
    }
    innerMemory(pos) = value
  }
  def getValue(pos: Int): Int = {
    if(pos >= initializedSize) {
      initUntil(pos)
    }
    innerMemory(pos)
  }

  def toList: List[Int] = innerMemory.toList

  def setProgramAndInit(program: Array[Int]) = {
    Array.copy(program, 0, innerMemory, 0, program.length)
    pl = program.length
    initializedSize = program.length
    initializedN = innerMemory(initializedSize - 1)
    initializedN_1 = innerMemory(initializedSize - 2)
  }

  def usedMemory = initializedSize

  def programLength = pl

  def take(n: Int) = innerMemory.take(n)

  private def initUntil(pos: Int) = {
    if(pos < initializedSize) {
      throw new IllegalStateException(s"Requesting initializing at pos $pos whereas memory is initialized until $initializedSize");
    }
    var mem_2 = 0
    var mem_1 = 0
    for (memoryAdress <- initializedSize to (pos)) {
      // Use the previously n and n-1 stored value to compute the first
      mem_2 = if (memoryAdress == initializedSize) initializedN_1 else if (memoryAdress == initializedSize + 1) initializedN else innerMemory(memoryAdress - 2)
      mem_1 = if (memoryAdress == initializedSize) initializedN else innerMemory(memoryAdress - 1)
      val crazyValue = Crazy(mem_2, mem_1)
      innerMemory(memoryAdress) = crazyValue
    }
    initializedSize = pos + 1
    initializedN = mem_2
    initializedN_1 = mem_1
  }

  override def toString = {
    // Take elements (+1 for 0-based index) (+2 to see next 2 normally blank cells)
    val indexed = (0 to MEMORY_SIZE).toList.zip(innerMemory).take(initializedSize)
    indexed.take(30).mkString("\n") + "\n...\n\n" + indexed.takeRight(30).mkString("\n")
  }
}
