package com.ledoyen.scala.aash.httpserver.extended.view

import org.junit.Assert._
import com.ledoyen.scala.aash.httpserver._
import java.net.URL
import org.junit.Test
import com.gargoylesoftware.htmlunit.WebClient
import com.gargoylesoftware.htmlunit.html.HtmlPage
import org.hamcrest.CoreMatchers

class ForwardViewTest {

  @Test
//  @Ignore
  def testForward() {
    val realServer = HttpServer.sync(8060).start
    realServer.registerListener("/real", req => HttpResponse(StatusCode.OK, "<html><head><title>Hey hey !</title></head><body>toto !</body></html>", Map("Content-Type" -> "text/html")))
    val proxyServer = HttpServer.sync(8070).start
    proxyServer.registerListener("/toto", new ForwardView(new URL("http://localhost:8060/real")))
    val webClient = new WebClient
    //    Thread.sleep(1000)
    val realPage: HtmlPage = webClient.getPage("http://localhost:8060/real")
    val proxyPage: HtmlPage = webClient.getPage("http://localhost:8070/toto")

    assertThat(proxyPage.getTitleText, CoreMatchers.equalTo(realPage.getTitleText))

    realServer.stop
    proxyServer.stop
  }

  @Test
//  @Ignore
  def testAsyncForward() {
    val realServer = HttpServer.sync(8060).start
    realServer.registerListener("/real", req => HttpResponse(StatusCode.OK, "<html><head><title>Hey hey !</title></head><body>toto !</body></html>", Map("Content-Type" -> "text/html")))
    val proxyServer = HttpServer.async(8070).start
    proxyServer.registerAsyncListener("/toto", new AsyncForwardView(new URL("http://localhost:8060/real")))
    val webClient = new WebClient
    //    Thread.sleep(1000)
    val realPage: HtmlPage = webClient.getPage("http://localhost:8060/real")
    val proxyPage: HtmlPage = webClient.getPage("http://localhost:8070/toto")

    assertThat(proxyPage.getTitleText, CoreMatchers.equalTo(realPage.getTitleText))

    realServer.stop
    proxyServer.stop
  }
}

object ForwardViewTest {
  def main(args: Array[String]) {
    val server = new SyncHttpServer(8090).start
    server.registerListener("/toto", new ForwardView(new URL("http://www.totoofficial.com/"))) // OK
  }
}