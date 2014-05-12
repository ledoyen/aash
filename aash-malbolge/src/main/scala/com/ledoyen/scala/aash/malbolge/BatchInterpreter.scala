package com.ledoyen.scala.aash.malbolge

import scala.io.Source
import com.ledoyen.scala.aash.malbolge.core.VM
import java.io.ByteArrayOutputStream
import java.io.InputStream

object BatchInterpreter {

  def main(args: Array[String]): Unit = {
    val progs = Source.fromFile("c:\\malbolge_progs.txt").mkString.split("\n")
    
    val startTime = System.currentTimeMillis
    val results = progs.filter(_.length > 1).par.map(p => run(p))
    
    NormalizedProgramGenerator.writeToFile("c:\\malbolge_progs_results.txt", results.mkString("\n"))
    
    val duration = (System.currentTimeMillis - startTime) / 1000
    println(s"\n\n ==========> END in $duration sec")
  }

  def run(prog: String) = {
    val output = new ByteArrayOutputStream

    val vm = new VM(new InputStream {
      def read: Int = throw new IllegalStateException(s"Input used !")
    }, output)

    val message = try {
	    vm.execute(
	      program = prog,
	      maxInstructions = Some(500),
	      quiet = true,
	      normalized = true) + " instructions"
	      
    } catch {
      case e: IllegalStateException => s"[ERROR] ${e.getMessage}"
    }
    
    val progOutput = new String(output.toByteArray).replaceAll("\n", "\\n").replaceAll("\r", "\\r")
    
    val result = s"$prog \t\t=>\t\t $progOutput \t\t\t\t$message"
//    println(result)
    result
  }
}