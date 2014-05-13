package com.ledoyen.scala.aash.malbolge.search

import java.io.ByteArrayOutputStream
import com.ledoyen.scala.aash.malbolge.core.VM
import java.io.InputStream
import scala.collection.parallel.immutable.ParSeq

object BatchInterpreter {

  def interpret(progs: ParSeq[List[Char]]) = {
    progs.map(p => run(p.mkString))
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
        normalized = true)
      "OK"
    } catch {
      case e: IllegalStateException => s"[ERROR] ${e.getMessage}"
    }

    val progOutput = new String(output.toByteArray).replaceAll("\n", "\\n").replaceAll("\r", "\\r")

    val instructionCount = vm.instructionCount.longValue

    val result = (prog, progOutput, instructionCount, message)

    result
  }
}