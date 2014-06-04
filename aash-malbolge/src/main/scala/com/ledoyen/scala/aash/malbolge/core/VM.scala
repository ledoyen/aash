package com.ledoyen.scala.aash.malbolge.core

import java.io.InputStream
import java.io.OutputStream
import java.util.concurrent.atomic.AtomicLong
import org.javasimon.SimonManager

class VM(var in: InputStream = System.in, var out: OutputStream = System.out, val memory: Memory = new LazyMemory) {

  var exited = false

  val instructionCount = new AtomicLong

  val a = new Register("a")
  val c = new Register("c")
  val d = new Register("d")
  var lastOperation: Operation = null

  override def toString = state

  def state = s"[$a $c $d]"

  def execute(program: String, maxInstructions: Option[Int] = None, quiet: Boolean = false, normalized: Boolean = false) = {
    import scala.util.control.Breaks._

    val startTime = System.currentTimeMillis

    Runtime.getRuntime().addShutdownHook(new Thread {
      override def run = if(exited) println(endMessage(startTime))
    })

    // init memory
    val split = SimonManager.getStopwatch("init").start
    init(program, normalized)
    split.stop

    val split2 = SimonManager.getStopwatch("run").start
    breakable {
      while (true) {
        if (processOneInstruction) break
        instructionCount.incrementAndGet
        val mustStop = maxInstructions.map(instructionCount.longValue > _).getOrElse(false)
        if (mustStop) throw new IllegalStateException(s"Maximum number of instructions reached [$maxInstructions]")
        //        displayState
      }
    }
    split2.stop
    if (!quiet) println(endMessage(startTime))
    instructionCount.longValue
  }

  def endMessage(startTime: Long) = {
    val duration = System.currentTimeMillis - startTime
    s"\nEND $state within ${instructionCount.longValue} instructions in $duration ms   (used memory cells : ${memory.usedMemory} / program length : ${memory.programLength})"
  }

  def processOneInstruction = {
    val decryptedInstruction = (memory(c.innerValue) + c.innerValue) % 94
    val op = Operation.parse(decryptedInstruction)
    lastOperation = op

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
    a.innerValue = 0
    c.innerValue = 0
    d.innerValue = 0

    instructionCount.set(0l)

    val programInMemoryLength = if (normalized) initNormalized(program) else initClassic(program)
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

    memory.setProgramAndInit(programAsIntArray)
    //    Array.copy(programAsIntArray, 0, memory, 0, programAsIntArray.length)

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