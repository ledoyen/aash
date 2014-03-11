package com.ledoyen.scala.httpserver

object Test {

  def main(args: Array[String]) {
    val server = new HttpServer(8080).start;
    server.enableStatistics
    server.registerListener("/toto", (req: HttpRequest) => new HttpResponse(req.version, StatusCode.OK, "<h1>TOTO !</h1>"))
    server.registerListener("/titi", (req: HttpRequest) => new HttpResponse(req.version, StatusCode.OK, "<h1>TOTO !</h1>"))
    server.registerListener("/stat", server.statistics)
  }
}