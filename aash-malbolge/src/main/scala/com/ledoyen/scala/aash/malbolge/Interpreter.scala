package com.ledoyen.scala.aash.malbolge

import com.ledoyen.scala.aash.malbolge.core.Crazy
import com.ledoyen.scala.aash.malbolge.core.Operation
import com.ledoyen.scala.aash.malbolge.core.Encrypt
import com.ledoyen.scala.aash.malbolge.core.EndOperation
import com.ledoyen.scala.aash.malbolge.core.NopOperation
import com.ledoyen.scala.aash.malbolge.core.JumpOperation

/**
 * @see http://esolangs.org/wiki/Malbolge
 */
object Interpreter {

  def main(args: Array[String]): Unit = {
    import scala.io.Source

    val program = parseArgs(args) match {
      case ("classpath", classpathFile) => {
        val stream = getClass.getClassLoader.getResourceAsStream(classpathFile)
        Source.fromInputStream(stream).mkString
      }
      case ("file", file) => {
        Source.fromFile(file).mkString
      }
      case ("inline", program) => program
    }
    
    val i = new VM

    i.execute(program)
  }

  def parseArgs(args: Array[String]): (String, String) = {
    args match {
      case Array("classpath", classpathFile) => ("classpath", classpathFile)
      case Array("file", file) => ("file", file)
      case Array("inline", program) => ("inline", program)
      case _ => {
        println("Wrong parameters, use with following ways :\n")
        println(" - \"classpath\" \"path of a file in CP\"")
        println(" - \"file\" \"path of a file in FS\"")
        println(" - \"inline\" \"program in Malbolge\"")
        System.exit(-1)
        throw new IllegalArgumentException("Wrong parameters")
      }
    }
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
    import scala.util.control.Breaks._

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