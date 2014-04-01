package com.ledoyen.scala.aash.httpserver

import java.net.URL

class HttpRequest(val method: String, val version: String, val path: String, val getParameters: Map[String, String], val headers: Map[String, String], val body: String) {
  var listenerPath: String = ""
  override def toString = s"$method $version $path $getParameters ($headers)"

  def withPath(overridedPath: String) = new HttpRequest(method, version, overridedPath, getParameters, headers, body)

  def toHttpLiteral(url: URL) = {
    val slashPrefixLength = path.prefixLength(_ == '/')
    val slashSuffixLength = url.getPath.reverse.prefixLength(_ == '/')
    s"${method} ${url.getPath.substring(slashSuffixLength)}/${path.substring(slashPrefixLength)} ${version}\r\n" +
      s"Host: ${url.getHost}\r\n" +
      "Connection: keep-alive\r\n" +
      headers
      .filter((p) => !"Connection".equalsIgnoreCase(p._1) && !"Host".equalsIgnoreCase(p._1) && !"Content-Length".equalsIgnoreCase(p._1))
      .map(h => s"${h._1}: ${h._2}").mkString("\r\n") +
      (if (body != null && body.trim != "")
        s"Content-Length: ${body.length}\r\n\r\n$body"
      else "\r\n\r\n")
  }
}