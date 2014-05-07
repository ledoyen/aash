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
import com.ledoyen.scala.aash.malbolge.core.Normalizer
import com.ledoyen.scala.aash.malbolge.core.VM

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

    

    parsedArgs._1 match {
      case "run" => {
        val i = new VM
        i.execute(program)
      }
      case "normalize" => println(Normalizer.normalize(program))
      case "unnormalize" => println(Normalizer.unNormalize(program))
      case "print" => println(program)
    }
  }

  def parseArgs(args: Array[String]): (String, String, String) = {
    val command = argAtPos(args, 0, List("run", "normalize", "unnormalize", "print"))
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
}
