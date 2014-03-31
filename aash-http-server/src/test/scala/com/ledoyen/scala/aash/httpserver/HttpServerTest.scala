package com.ledoyen.scala.aash.httpserver

import org.junit._
import org.junit.Assert._
import org.hamcrest.CoreMatchers
import com.gargoylesoftware.htmlunit.WebClient
import com.gargoylesoftware.htmlunit.html.HtmlPage
import com.gargoylesoftware.htmlunit.TextPage

class HttpServerTest {

  @Test
  def testSyncHttpServer = {
   testHttpServer(new SyncHttpServer(8080).start)
  }

  @Test
  def testAsyncHttpServer = {
   testHttpServer(new AsyncHttpServer(8080).start)
  }

  def testHttpServer(server: HttpServer) = {
    server.enableStatistics
    server.registerListener("/toto", (req: HttpRequest) => new HttpResponse(req.version, StatusCode.OK, "<h1>TOTO !</h1>"))
    server.registerListener("/titi", (req: HttpRequest) => new HttpResponse(req.version, StatusCode.OK, "<h1>TITI !</h1>"))
    server.registerListener("/stat", server.statistics)
    server.registerShutdownHook(() => println("exit hook"))

    val webClient = new WebClient

    val totoPage: TextPage = webClient.getPage("http://localhost:8080/toto")
    assertThat(totoPage.getContent, CoreMatchers.equalTo("<h1>TOTO !</h1>"))

    val titiPage: TextPage = webClient.getPage("http://localhost:8080/titi")
    assertThat(titiPage.getContent, CoreMatchers.equalTo("<h1>TITI !</h1>"))
    
    val statPage: TextPage = webClient.getPage("http://localhost:8080/stat")
    assertThat(statPage.getContent, CoreMatchers.containsString("Active threads"))
    assertThat(statPage.getContent, CoreMatchers.containsString("HTTP-8080-toto"))
    assertThat(statPage.getContent, CoreMatchers.containsString("HTTP-8080-titi"))
    assertThat(statPage.getContent, CoreMatchers.containsString("HTTP-8080-stat"))

    server.stop
  }
}