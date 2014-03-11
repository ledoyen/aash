package com.ledoyen.scala.httpserver

import org.junit._

class HttpServerTest {

  @Test
  def testListeningServer = {
    val server = new HttpServer(8080).start
    server.enableStatistics
    server.registerListener("/toto", (req: HttpRequest) => new HttpResponse(req.version, StatusCode.OK, "<h1>TOTO !</h1>"))
    server.registerListener("/titi", (req: HttpRequest) => new HttpResponse(req.version, StatusCode.OK, "<h1>TOTO !</h1>"))
    server.registerListener("/stat", server.statistics)

    // Wait for user actions
    // TODO test it programatically
    Thread.sleep(5000)
    server.stop
  }
}