package com.ledoyen.scala.aash.httpserver

import org.junit.Assert._
import org.junit._
import java.net.URL
import org.hamcrest.CoreMatchers

class HttpTest {

  @Test
  def testHttpConnect: Unit = {
    val resp = Http.connect(new URL("http://www.totoofficial.com"), new HttpRequest("GET", "HTTP/1.1", "/", Map(),
        Map("User-Agent" -> "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.154 Safari/537.36",
            "Content-Type" -> "text/plain; charset=utf-8",
        	"Accept" -> "*/*",
        	"Accept-Encoding" -> "gzip,deflate,sdch",
        	"Accept-Language" -> "fr-FR,fr;q=0.8,en-US;q=0.6,en;q=0.4"),
        null))
     resp match {
      case None => throw new IllegalStateException
      case _ =>
    }
    println(resp.get.body);
  }
}