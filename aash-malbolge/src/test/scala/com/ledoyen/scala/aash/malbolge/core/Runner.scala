package com.ledoyen.scala.aash.malbolge.core

import java.io.ByteArrayOutputStream
import java.io.InputStream

object Runner {

  val prog = "jpp<ppppp<pppp<<pp<ppp<pppp<ppppp<pp<ioooj/ojji</oiivoooi<"
    
    def main(args: Array[String]): Unit = {
    
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
    
    println(result)
  }
}