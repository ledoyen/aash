package com.ledoyen.scala.aash.httpserver

import java.util.Date

object HttpResponse {
  def apply(version: String = "HTTP/1.1", code: StatusCode.Value, body: String, headers: Map[String, String] = Map()) = {
    new HttpResponse(version, code, body, headers)
  }
}
class HttpResponse(val version: String = "HTTP/1.1", val code: StatusCode.Value, val body: String, val headers: Map[String, String] = Map()) {
  override def toString = s"$version ${code.id} ${code.toString} ($headers)"

  def changeBody(f: String => String) = new HttpResponse(version, code, f(body), headers)

  def toHttpLiteral = s"${version} ${code.id} ${code.toString}\r\n" +
		  s"Date: ${Http.format(new Date)}\r\n" +
		  // TODO use the real ${project.version}
		  "Server: Aash/0.0.1-SNAPSHOT\r\n" +
		  headers.map(h => s"${h._1}: ${h._2}").mkString("\r\n") +
		  (if(headers.size == 0) "\r\n" else "\r\n\r\n") +
		  body
		  
}