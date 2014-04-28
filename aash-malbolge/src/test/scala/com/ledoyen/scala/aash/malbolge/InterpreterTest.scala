package com.ledoyen.scala.aash.malbolge.core

import scala.io.Source

import org.junit.Assert._
import org.junit.Test
import org.hamcrest.CoreMatchers
import com.ledoyen.scala.aash.malbolge.Interpreter

class InterpreterTest {

  @Test
  def testNormalize = {
    val stream = getClass.getClassLoader.getResourceAsStream("99bottles.mlb")
    val program = Source.fromInputStream(stream).mkString
    println(Interpreter.normalize(program));
  }
}