package com.ledoyen.scala.feed4work

import org.junit.Assert._
import org.junit.Test
import java.util.Date
import org.hamcrest.CoreMatchers

class FeedTest {

  @Test
  def testJsonSerializationAndDeserialization = {
    val feeds = List(new Feed("Feed 1", "http://toto.titi.com", new Date, "toto <br/><b>titi</b>"),
      new Feed("Feed 2", "http://toto.titi.com", new Date, "titi <br/><b>toto</b>"))
      for(feed <- feeds) {
        println(feed.toJson)
      }
  }
}