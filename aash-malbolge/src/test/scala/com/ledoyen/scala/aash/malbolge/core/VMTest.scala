package com.ledoyen.scala.aash.malbolge.core

import scala.io.Source
import org.junit.Assert._
import org.junit.Test
import org.javasimon.SimonManager

object VMTest {

  val PERFORMANCE_LOOP_RANGE = 1 to 10

  val program = {
    // 99bottles
    val stream = getClass.getClassLoader.getResourceAsStream("helloWorld.mlb")
    val program = Source.fromInputStream(stream).mkString //.replaceAll("\\s", "")
    program
  }
}

class VMTest {

  @Test
  def testNormalizeBijection = {
    val vm = new VM

    for (i <- VMTest.PERFORMANCE_LOOP_RANGE) {
      val split = SimonManager.getStopwatch("total").start
      val vm = new VM
      vm.execute(VMTest.program)
      split.stop
    }
    System.out.println(SimonManager.getStopwatch("init"))
    System.out.println(SimonManager.getStopwatch("init-program"))
    System.out.println(SimonManager.getStopwatch("init-memory"))
    System.out.println(SimonManager.getStopwatch("run"))
    System.out.println(SimonManager.getStopwatch("total"))
  }
}
