package com.ledoyen.scala.aash.malbolge.core

import org.junit.Assert._
import org.junit.Test
import org.hamcrest.CoreMatchers

class TrinaryTest {

  @Test
  def testToTrinary = {
    assertThat(Trinary.toTrinary(2), CoreMatchers.equalTo(2l))
    assertThat(Trinary.toTrinary(42), CoreMatchers.equalTo(1120l))
    assertThat(Trinary.toTrinary(59048), CoreMatchers.equalTo(2222222222l))
  }

  @Test
  def testFromTrinary = {
    assertThat(Trinary.fromTrinary(2l), CoreMatchers.equalTo(2))
    assertThat(Trinary.fromTrinary(1120l), CoreMatchers.equalTo(42))
    assertThat(Trinary.fromTrinary(2222222222l), CoreMatchers.equalTo(59048))
  }

  @Test
  def testCrazy = {
    assertThat(Trinary.crazyOp(1112220l, 120120120l), CoreMatchers.equalTo(1001022211l))
  }

  @Test
  def testRotate = {
    assertThat(Trinary.rotate(2111112l), CoreMatchers.equalTo(2000211111l))
    assertThat(Trinary.rotate(2111110l), CoreMatchers.equalTo(211111l))
  }
}