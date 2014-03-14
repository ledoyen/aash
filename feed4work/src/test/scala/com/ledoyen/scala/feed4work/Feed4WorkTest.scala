package com.ledoyen.scala.feed4work

import org.junit.Test

class Feed4WorkTest {

  @Test
  def test = {
    val server = new Feed4Work().start
    server.enableStatistics
    server.registerListener("/stat", server.statistics)

    // Wait for user actions
    // TODO test it programatically
    Thread.sleep(5000)
    server.stop
  }
}