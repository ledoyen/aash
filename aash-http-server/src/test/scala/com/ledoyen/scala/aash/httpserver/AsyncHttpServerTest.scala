package com.ledoyen.scala.aash.httpserver

import org.junit._
import org.powermock.reflect.Whitebox
import sun.nio.ch.ThreadPool
import java.util.concurrent.ThreadPoolExecutor

object AsyncHttpServerTest {
  def main(args: Array[String]) {
    val server = HttpServer.async(8080).start
    server.enableStatistics
//    server.stop
  }
}

class AsyncHttpServerTest {

  @Test
  @Ignore
  def testListeningServer = {
    val server = HttpServer.async(8080).start
    server.stop
  }
}