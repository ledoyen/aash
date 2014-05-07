package com.ledoyen.scala.aash.malbolge.core

import java.io.InputStream
import java.io.OutputStream
import java.util.concurrent.atomic.AtomicLong

class VM(val in: InputStream = System.in, val out: OutputStream = System.out) {

  val MEMORY_SIZE = 59049

  val a = new Register("a")
  val c = new Register("c")
  val d = new Register("d")

  val memory = Array.fill(MEMORY_SIZE) { 0 } //Array[Int](59049)

  def state = s"[$a $c $d]"

  def execute(program: String, maxInstructions: Option[Int] = None, quiet: Boolean = false, normalized: Boolean = false) = {
    import scala.util.control.Breaks._

    val instructionCount = new AtomicLong
    // init memory
    init(program, normalized)

    breakable {
      while (true) {
        if (processOneInstruction) break
        instructionCount.incrementAndGet()
        val mustStop = maxInstructions.map(instructionCount.longValue > _).getOrElse(false)
        if(mustStop) throw new IllegalStateException(s"Maximum number of instructions reached [$maxInstructions]")
        //        displayState
      }
    }
    if(!quiet) println(s"\nEND $state within ${instructionCount.longValue} instructions")
    instructionCount.longValue
  }

  def processOneInstruction = {
    val decryptedInstruction = (memory(c.innerValue) + c.innerValue) % 94
    val op = Operation.parse(decryptedInstruction)

    if (op == EndOperation) true else {
      op.apply(this)

      // Encrypt used instruction
      memory(c.innerValue) = Encrypt.encrypt(memory(c.innerValue) % 94)

      // Increment C & D
      c.innerValue = Trinary.stayInMalbolgeRange(c.innerValue + 1)
      d.innerValue = Trinary.stayInMalbolgeRange(d.innerValue + 1)
      false
    }
  }

  def displayState = println(s"State $state")
  def displayMemory = println(s"Memory ${memory.toList}")

  def init(program: String, normalized: Boolean) = {
    val programInMemoryLength = if(normalized) initNormalized(program) else initClassic(program)

    for (memoryAdress <- programInMemoryLength to (MEMORY_SIZE - 1)) {
      memory(memoryAdress) = Crazy(memory(memoryAdress - 2), memory(memoryAdress - 1))
    }
  }
  
  def initClassic(program: String): Int = {
    val programAsIntArray = program.replaceAll("\\s", "").toCharArray.map(_.toInt)

    // Assert Program is valid
    for (i <- 0 until programAsIntArray.length) {
      val memoryValue = programAsIntArray(i)
      val decryptedValue = (programAsIntArray(i) + i) % 94
      try {
        val op = Operation.parse(decryptedValue, true)
      } catch {
        case e: Exception => {
          println(s"Error on ${memoryValue}(${memoryValue.toChar}) decrypted is ${decryptedValue}(${decryptedValue.toChar})")
          throw e
        }
      }
    }

    Array.copy(programAsIntArray, 0, memory, 0, programAsIntArray.length)
    
    programAsIntArray.length
  }
  
  def initNormalized(program: String): Int = {
    initClassic(Normalizer.unNormalize(program))
  }
}

class Register(val name: String) {
  var innerValue = 0

  override def toString = s"$name=$innerValue"
}