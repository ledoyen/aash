package com.ledoyen.scala.aash.malbolge

import scala.util.control.Breaks._
import com.ledoyen.scala.aash.malbolge.core.Crazy
import com.ledoyen.scala.aash.malbolge.core.Operation
import com.ledoyen.scala.aash.malbolge.core.Encrypt
import com.ledoyen.scala.aash.malbolge.core.EndOperation
import com.ledoyen.scala.aash.malbolge.core.NopOperation

/**
 * @see https://github.com/b441berith/MalbolgeInterpreter
 */
object Interpreter {

  def main(args: Array[String]): Unit = {
    val i = new VM

    i.execute("(=<`:9876Z4321UT.-Q+*)M'&%$H\"!~}|Bzy?=|{z]KwZY44Eq0/{mlk** hKs_dG5[m_BA{?-Y;;Vb'rR5431M}/.zHGwEDCBA@98\\6543W10/.R,+O<")
  }
}

class VM {

  val MEMORY_SIZE = 59049

  val in = System.in
  val out = System.out

  val a = new Register("a")
  val c = new Register("c")
  val d = new Register("d")

  val memory = Array.fill(MEMORY_SIZE) { 0 } //Array[Int](59049)

  def state = s"State [$a $c $d]"

  def execute(program: String) = {
    // init memory
    init(program)
    displayState

    breakable {
      while (true) {
        if (processOneInstruction) break
        //        displayState
      }
    }
    println("\nEND")
  }

  def processOneInstruction = {
    val decryptedInstruction = (memory(c.innerValue) + c.innerValue) % 94
    val op = Operation.parse(decryptedInstruction)
//    if (op != NopOperation) println(s"$op $decryptedInstruction")

    if (op == EndOperation) true else {
      op.apply(this)

      // Encrypt used instruction
      memory(c.innerValue) = Encrypt.encrypt(memory(c.innerValue) % 94)
      
      // Increment C & D
      c.innerValue = c.innerValue + 1
      d.innerValue = d.innerValue + 1
      false
    }
  }

  def displayState = println(state)
  def displayMemory = println(memory.toList)

  def init(program: String) = {
    val programAsIntArray = program.replaceAll("\\s", "").toCharArray.map(_.toInt)

    // Assert Program is valid
    for (i <- 0 until programAsIntArray.length) {
      val memoryValue = programAsIntArray(i)
      val decryptedValue = (programAsIntArray(i) + i) % 94
      try {
        val op = Operation.parse(decryptedValue, true)
        //    			  println(op + decryptedValue.toChar.toString)
      } catch {
        case e: Exception => {
          println(s"Error on ${memoryValue}(${memoryValue.toChar}) decrypted is ${decryptedValue}(${decryptedValue.toChar})")
          throw e
        }
      }
    }

    Array.copy(programAsIntArray, 0, memory, 0, programAsIntArray.length)

    for (memoryAdress <- programAsIntArray.length to (MEMORY_SIZE - 1)) {
      memory(memoryAdress) = Crazy(memory(memoryAdress - 2), memory(memoryAdress - 1))
    }
  }
}

class Register(val name: String) {
  var innerValue = 0

  override def toString = s"$name=$innerValue"
}