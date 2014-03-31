package com.ledoyen.scala.aash.httpserver

import scala.collection.JavaConversions._
import org.junit._
import org.junit.Assert._
import org.hamcrest.CoreMatchers
import com.gargoylesoftware.htmlunit.WebClient
import com.gargoylesoftware.htmlunit.html.HtmlPage
import com.gargoylesoftware.htmlunit.TextPage
import java.nio.file.Paths
import scala.io.Source
import java.nio.channels.AsynchronousFileChannel
import java.nio.ByteBuffer
import java.nio.channels.CompletionHandler
import java.nio.charset.Charset
import org.powermock.reflect.Whitebox
import sun.nio.ch.ThreadPool
import java.util.concurrent.ThreadPoolExecutor
import java.nio.file.attribute.FileAttribute
import java.nio.file.OpenOption
import java.util.concurrent.Executors
import com.ledoyen.scala.aash.tool.Threads
import org.junit.runners.MethodSorters
import com.ledoyen.scala.aash.tool.AsyncFiles

class HttpServerTest {

  @Test
  def testSyncHttpServer = {
    val server = HttpServer.sync(8080).start

    testHttpServer(server)

    server.stop
  }

  @Test
  def testAsyncHttpServer = {
    val server = HttpServer.async(8080).start

    testHttpServer(server)

    server.registerAsyncListener("/file",
      (req, callback) => {
        AsyncFiles.read(
            path = Paths.get(getClass.getClassLoader.getResource("file.txt").toURI),
            callback = (content) => callback.write(HttpResponse(req.version, StatusCode.OK, content)))
      })

    val webClient = new WebClient

    val filePage: TextPage = webClient.getPage("http://localhost:8080/file")
    assertThat(filePage.getContent, CoreMatchers.equalTo("test"))

    server.stop
  }

  def testHttpServer(server: HttpServer) = {
    server.registerListener("/toto", req => HttpResponse(req.version, StatusCode.OK, "<h1>TOTO !</h1>"))
    server.registerListener("/titi", req => HttpResponse(req.version, StatusCode.OK, "<h1>TITI !</h1>"))
    server.registerListener("/stat", server.statistics)
    server.enableStatistics

    server.registerShutdownHook(() => println("exit hook"))

    val webClient = new WebClient

    val totoPage: TextPage = webClient.getPage("http://localhost:8080/toto")
    assertThat(totoPage.getContent, CoreMatchers.equalTo("<h1>TOTO !</h1>"))

    val titiPage: TextPage = webClient.getPage("http://localhost:8080/titi")
    assertThat(titiPage.getContent, CoreMatchers.equalTo("<h1>TITI !</h1>"))

    val statPage: TextPage = webClient.getPage("http://localhost:8080/stat")
    val text = statPage.getContent
    assertThat(text, CoreMatchers.containsString("Active threads"))
    assertThat(text, CoreMatchers.containsString("HTTP-8080-toto"))
    assertThat(text, CoreMatchers.containsString("HTTP-8080-titi"))
    assertThat(text, CoreMatchers.containsString("HTTP-8080-stat"))
  }
}