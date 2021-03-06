package com.ledoyen.scala.aash.malbolge.search

import org.junit.Assert._
import org.junit.Test
import org.hamcrest.CoreMatchers

class AnalyzerTest {

  @Test
  def testCountPoints = {
     assertThat(Analyzer.countPoints(("", "g", 3l, "", 0)), CoreMatchers.equalTo(0l))
     assertThat(Analyzer.countPoints(("", "I", 3l, "", 0)), CoreMatchers.equalTo(-10l))
     assertThat(Analyzer.countPoints(("", "I ", 3l, "", 0)), CoreMatchers.equalTo(-23l))
     assertThat(Analyzer.countPoints(("", "I a", 3l, "", 0)), CoreMatchers.equalTo(-36l))
  }
}