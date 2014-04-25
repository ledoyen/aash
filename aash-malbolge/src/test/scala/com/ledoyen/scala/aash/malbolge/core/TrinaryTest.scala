package com.ledoyen.scala.aash.malbolge.core

import org.junit.Assert._
import org.junit.Test
import org.hamcrest.CoreMatchers

class TrinaryTest {

  @Test
  def testTrinary = {
    
    assertThat(Trinary.toTrinary(2), CoreMatchers.equalTo(2l))
    assertThat(Trinary.toTrinary(42), CoreMatchers.equalTo(1120l))
    assertThat(Trinary.toTrinary(59048), CoreMatchers.equalTo(2222222222l))
  }

  @Test
  def testCrazy = {
    assertThat(Crazy.crazyOp(1112220l, 120120120l), CoreMatchers.equalTo(1001022211l))
  }
}