package com.ledoyen.scala.aash.tool

import org.junit.Assert._
import org.junit.Test
import org.hamcrest.CoreMatchers

class DatesTest {

  @Test
  def testSmartparse = {
    assertThat(Dates.smartParse(1 * 60*60*1000 + 23 * 60*1000 + 15*1000 + 562), CoreMatchers.equalTo("1 h 23 m 15 s 562 ms"))
  }
}