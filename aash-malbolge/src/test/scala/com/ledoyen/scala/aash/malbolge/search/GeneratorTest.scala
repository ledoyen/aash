package com.ledoyen.scala.aash.malbolge.search

import org.junit.Assert._
import org.junit.Test
import org.hamcrest.CoreMatchers
import com.ledoyen.scala.aash.malbolge.core.Operation

class GeneratorTest {

  @Test
  def testGenrec = {
    val progs = Operation.operations.map(op => List(op.toNormalizedCode))
    val newProgs = Generator.genrec(progs, 2, List())
    assertThat(newProgs.size, CoreMatchers.equalTo(progs.size * 8 + progs.size))
  }
  
  @Test
  def testGenrecLast = {
    val progs = Operation.operations.map(op => List(op.toNormalizedCode))
    assertThat(Generator.genreclast(progs, 1).size, CoreMatchers.equalTo(progs.size * 8))
    assertThat(Generator.genreclast(progs, 5).size, CoreMatchers.equalTo(progs.size * Math.pow(8, 5).toInt))
  }
}