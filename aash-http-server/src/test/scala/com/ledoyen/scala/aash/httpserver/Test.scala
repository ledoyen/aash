package com.ledoyen.scala.aash.httpserver

object Test {

  def main(args: Array[String]) {
    val server = new SyncHttpServer(8080).start
    server.enableStatistics
    server.registerListener("/toto", req => HttpResponse(StatusCode.OK, "<h1>TOTO !</h1>"))
    server.registerListener("/titi", req => HttpResponse(StatusCode.OK, "<h1>TITI !</h1>"))
    server.registerListener("/stat", server.statistics)
  }
}