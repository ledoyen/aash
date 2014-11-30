package com.ledoyen.scala.aash.malbolge.core

import scala.io.Source
import org.junit.Assert._
import org.junit.Test
import java.io.InputStream
import java.io.ByteArrayOutputStream
import java.util.concurrent.Executors
import java.util.concurrent.Callable

import scala.collection.JavaConversions._

class HelloWorldTest {

  @Test
  def buildTest() {
    
    val stream = getClass.getClassLoader.getResourceAsStream("helloWorld.mlb")
    val program = Source.fromInputStream(stream).mkString.replaceAll("\\s", "")
    
    
    val startTime = System.currentTimeMillis

    println(s"Processors : ${Runtime.getRuntime.availableProcessors}\n\n")
    val tries = (2 to program.length).map(i => toCallable(testProgram(program.substring(0, i)))).toList
    val pool = Executors.newFixedThreadPool(Runtime.getRuntime.availableProcessors)
    pool.invokeAll(tries)
    
    val duration = System.currentTimeMillis - startTime
    println(s"\n\n TOTAL calculations in $duration ms")
  }

  def testProgram(program: String) {
    val output = new ByteArrayOutputStream
    val vm = new VM(new InputStream {
      def read: Int = throw new IllegalStateException(s"Input used !")
    }, output)
    try {
      val startTime = System.currentTimeMillis
      val instructionCount = vm.execute(program, Some(300), true)
      val duration = System.currentTimeMillis - startTime
      print(s"[EXEC] $program   ---   ")
      println(new String(output.toByteArray) + s"    (in $instructionCount instructions / $duration ms)")
    } catch {
      case e: Exception => // println(s"[ERROR] ${e.getMessage}")
    }
  }

  def toCallable(pieceOfCode: => Unit) = new Callable[Unit] {
    def call = pieceOfCode
  }
}
