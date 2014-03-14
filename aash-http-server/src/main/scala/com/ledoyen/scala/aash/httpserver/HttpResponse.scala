package com.ledoyen.scala.httpserver

class HttpResponse(val version: String = "HTTP/1.1", val code: StatusCode.Value, val body: String, val headers: List[String] = List()) {
  override def toString = s"version ${code.id} ${code.toString}\r\n$body"
}