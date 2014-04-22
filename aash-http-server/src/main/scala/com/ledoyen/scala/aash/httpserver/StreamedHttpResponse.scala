package com.ledoyen.scala.aash.httpserver

import java.util.Date

class StreamedHttpResponse(val code: StatusCode.Value, val headers: Map[String, String] = Map()) {

  override def toString = s"${code.id} ${code.toString} ($headers)"

  def readBody(callback: StringStreamCallback) = {
    
  }

  def toHttpLiteral = s"${Http.VERSION} ${code.id} ${code.toString}\r\n" +
		  s"Date: ${Http.format(new Date)}\r\n" +
		  // TODO use the real ${project.version}
		  "Server: Aash/0.0.1-SNAPSHOT\r\n" +
		  headers.map(h => s"${h._1}: ${h._2}").mkString("\r\n") +
		  (if(headers.size == 0) "\r\n" else "\r\n\r\n")
}

trait StringStreamCallback {
  def onNext(value: String)
  def onComplete
}