package com.ledoyen.scala.aash.malbolge.search

import org.junit.Assert._
import org.junit.Test
import org.hamcrest.CoreMatchers

class AnalyzerTest {

  @Test
  def testCountPoints = {
     assertThat(Analyzer.countPoints(("", "g", 3l, "")), CoreMatchers.equalTo(3l))
     assertThat(Analyzer.countPoints(("", "I", 3l, "")), CoreMatchers.equalTo(-7l))
     assertThat(Analyzer.countPoints(("", "I ", 3l, "")), CoreMatchers.equalTo(-17l))
     assertThat(Analyzer.countPoints(("", "I a", 3l, "")), CoreMatchers.equalTo(-27l))
  }
}