package com.ledoyen.scala.aash.httpserver.extended.view

import java.net.URL
import com.ledoyen.scala.aash.httpserver._
import org.slf4j.LoggerFactory
import com.ledoyen.scala.aash.tool.AsyncHttp

object ForwardView {
  def logger = LoggerFactory.getLogger("ForwardView")

  def remainingPath(req: HttpRequest): String = req.path.substring(req.listenerPath.length)

  def replaceUrls(listenerPath: String)(body: Option[String]): Option[String] = {
    body.map(b => b
      .replaceAllLiterally("href=\"/", "href=\"" + listenerPath + "/")
      .replaceAllLiterally("src=\"/", "href=\"" + listenerPath + "/")
      // replace all nor relatives
      .replaceAllLiterally("href=\"" + listenerPath + "//", "href=\"//")
      .replaceAllLiterally("src=\"" + listenerPath + "//", "href=\"//"))
  }
}

class ForwardView(val rootUrl: URL) extends (HttpRequest => HttpResponse) {
  import java.net.HttpURLConnection
  import java.net.MalformedURLException

  def apply(req: HttpRequest): HttpResponse = {
    Http.connect(rootUrl, req.withPath(ForwardView.remainingPath(req))) match {
      case Some(resp) => {
        val r = resp.body(ForwardView.replaceUrls(req.listenerPath)(resp.body))
        ForwardView.logger.trace(r.toString)
        r
      }
      case None => Http.error(new RuntimeException("Unknown issue"))
    }
  }
}

class AsyncForwardView(val rootUrl: URL) extends ((HttpRequest, WriteCallback) => Unit) {

  def apply(req: HttpRequest, callback: WriteCallback) = {
    AsyncHttp.connect(rootUrl, req.withPath(ForwardView.remainingPath(req)), true, maybe => maybe match {
      case Right(resp) => {
        val r = resp.body(ForwardView.replaceUrls(req.listenerPath)(resp.body))
        ForwardView.logger.trace(r.toString)
        callback.write(r)
      }
      case Left(t) => callback.write(Http.error(t))
    })
  }
}