package com.ledoyen.scala.aash.malbolge

import com.ledoyen.scala.aash.tool.ExtendedStringList._
import com.ledoyen.scala.aash.malbolge.core.Crazy
import com.ledoyen.scala.aash.malbolge.core.Operation
import com.ledoyen.scala.aash.malbolge.core.Encrypt
import com.ledoyen.scala.aash.malbolge.core.EndOperation
import com.ledoyen.scala.aash.malbolge.core.NopOperation
import com.ledoyen.scala.aash.malbolge.core.JumpOperation
import com.ledoyen.scala.aash.malbolge.core.Trinary
import scala.annotation.tailrec

/**
 * @see http://esolangs.org/wiki/Malbolge
 */
object Interpreter {

  def main(args: Array[String]): Unit = {
    import scala.io.Source

    val parsedArgs = parseArgs(args)
    val program = parsedArgs match {
      case (_, "classpath", classpathFile) => {
        val stream = getClass.getClassLoader.getResourceAsStream(classpathFile)
        Source.fromInputStream(stream).mkString
      }
      case (_, "file", file) => {
        Source.fromFile(file).mkString
      }
      case (_, "inline", program) => program
    }

    val i = new VM

    parsedArgs._1 match {
      case "run" => i.execute(program)
      case "normalize" => println(normalize(program))
    }
  }

  def parseArgs(args: Array[String]): (String, String, String) = {
    val command = argAtPos(args, 0, List("run", "normalize"))
    val vType = argAtPos(args, 1, List("classpath", "file", "inline"))
    val content = argAtPos(args, 2)

    (command, vType, content)
  }

  def argAtPos(args: Array[String], pos: Int, matchValues: List[String] = Nil): String = {
    def printHelpAndThrow = {
      printHelp
      System.exit(-1)
      throw new IllegalArgumentException("Wrong parameters")
    }

    if (args.size > pos) {
      val arg = args(pos)
      matchValues match {
        case Nil => arg
        case _ => {
          if (matchValues.containsIC(arg)) arg.toLowerCase else printHelpAndThrow
        }
      }
    } else printHelpAndThrow
  }

  def printHelp = {
    println("Wrong parameters, use with following ones :\n")
    println("command type content") // \"classpath\" \"path of a file in CP\"")
    println("\twhere action can be one of [run, normalize]") // - \"file\" \"path of a file in FS\"")
    println("\twhere type can be one of [classpath, file, inline]") // (" - \"inline\" \"program in Malbolge\"")
    println("\twhere content must be the adequate classpath, path or program, given the type you use")

    println("\n For example :")
    println("\t>java -jar aash-malbolge.jar run classpath helloWorld.mlb")
    println("\t>java -jar aash-malbolge.jar normalize file \"/home/helloWorld.mlb\"")
  }

  def normalize(program: String): String = normalizeRec(program.toList, Nil, 0).mkString

  // TailRec is needed for long program that may result in StackOverflow otherwise
  // It could also be done in a loop/mutable way instead of the recursive/immutable one here 
  @tailrec
  def normalizeRec(program: List[Char], acc: List[Char], index: Int): List[Char] = program match {
    case head :: tail => {
      // Blank characters are rewritten as it is
      if (List(10, 13, 32).contains(program.head.toInt)) {
        normalizeRec(program.tail, acc :+ program.head, index)
      } else {
        val decryptedInstruction = (program.head.toInt + index) % 94
        val op = try {
          Operation.parse((program.head.toInt + index) % 94, true)
        } catch {
          case e: Exception => throw new IllegalArgumentException(s"Decrypted value ${decryptedInstruction.toChar} ($decryptedInstruction), initially ${program.head} (${program.head.toInt}) at pos $index is not mapped to any Malbolge operation")
        }
        normalizeRec(program.tail, acc :+ op.toNormalizedCode, index + 1)
      }
    }
    case Nil => acc
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

  def state = s"[$a $c $d]"

  def execute(program: String) = {
    import scala.util.control.Breaks._

    // init memory
    init(program)

    breakable {
      while (true) {
        if (processOneInstruction) break
        //        displayState
      }
    }
    println("\nEND " + state)
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