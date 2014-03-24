package com.ledoyen.scala.aash.httpserver

class HttpResponse(val version: String = "HTTP/1.1", val code: StatusCode.Value, val body: String, val headers: Map[String, String] = Map()) {
  override def toString = s"$version ${code.id} ${code.toString} ($headers)"

  def changeBody(f: String => String) = new HttpResponse(version, code, f(body), headers)
}