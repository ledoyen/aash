package com.ledoyen.scala.aash.malbolge.search

import com.ledoyen.scala.aash.search.Search._
import com.ledoyen.scala.aash.malbolge.core.Operation

object BeamSearch2 {

  val TARGET = "I am the king of hell."

  def main(args: Array[String]): Unit = {

    beam_search("", success, successors, cost_fn, 10000)
  }

  def success(program: String) = TARGET.equals(resolve(program)._2)

  def successors(program: String) = Operation.operations.map(op => program :+ op.toNormalizedCode)

  def cost_fn(program: String) = {
    val resolved = resolve(program)
    val commonPrefix = TARGET.zip(resolved._2).takeWhile(Function.tupled(_ == _)).map(_._1).mkString
    resolved._1 - commonPrefix.length() * 10
  }

  def resolve(program: String): (Int, String) = {
    import java.io.ByteArrayOutputStream
    import com.ledoyen.scala.aash.malbolge.core.VM
    import java.io.InputStream

    if (program.length() < 2) {
      (0, "")
    } else {

      val output = new ByteArrayOutputStream

      val vm = new VM(new InputStream {
        def read: Int = throw new IllegalStateException(s"Input used !")
      }, output)

      val message = try {
        vm.execute(
          program = program,
          maxInstructions = None,
          quiet = true,
          normalized = true)
        "OK"
      } catch {
        case e: IllegalStateException => s"[ERROR] ${e.getMessage}"
      }
      val progOutput = new String(output.toByteArray).replaceAll("\n", "\\n").replaceAll("\r", "\\r")
      val memoryAccess = vm.memoryAccess

      (memoryAccess, progOutput)
    }
  }
}
