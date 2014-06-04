package com.ledoyen.scala.aash.malbolge.core

import java.io.ByteArrayOutputStream
import java.io.InputStream

import scala.io.Source
import org.junit.Assert._
import org.junit.Test
import org.javasimon.SimonManager

object VMTest {

  val PERFORMANCE_LOOP_RANGE = 1 to 100

  val program = {
    // 99bottles
    // helloWorld
    val stream = getClass.getClassLoader.getResourceAsStream("helloWorld.mlb")
    val program = Source.fromInputStream(stream).mkString //.replaceAll("\\s", "")
    program
  }
}

class VMTest {

  @Test
  def testNormalizeBijection = {

    for (i <- VMTest.PERFORMANCE_LOOP_RANGE) {
      val split = SimonManager.getStopwatch("total").start
      val vm = new VM
      vm.in = new InputStream {
        var last4inputs: List[Char] = List()
        def read: Int = {
          val value = System.in.read
          last4inputs = value.toChar :: last4inputs.take(3)
          if(last4inputs.reverse.mkString == "exit") {
            vm.exited = true
            System.exit(0)
          }
          value
        }
      }
      vm.out = new ByteArrayOutputStream
      vm.execute(program = VMTest.program, quiet = true)
      split.stop
    }
    System.out.println(SimonManager.getStopwatch("init"))
    System.out.println(SimonManager.getStopwatch("run"))
    System.out.println(SimonManager.getStopwatch("total"))
  }
}
