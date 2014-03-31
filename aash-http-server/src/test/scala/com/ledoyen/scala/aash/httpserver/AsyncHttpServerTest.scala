package com.ledoyen.scala.aash.httpserver

import org.junit._

object AsyncHttpServerTest {
  def main(args: Array[String]) {
    val server = HttpServer.sync(8080).start
//    server.stop
  }
}

class AsyncHttpServerTest {

  @Test
  def testListeningServer = {
    val server = HttpServer.async(8080).start
    Thread.sleep(10000)
    server.stop
  }
}