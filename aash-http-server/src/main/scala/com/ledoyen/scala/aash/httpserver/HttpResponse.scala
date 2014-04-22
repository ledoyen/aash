package com.ledoyen.scala.aash.httpserver

import java.util.Date

object HttpResponse {
  def apply(_code: StatusCode.Value, _body: String, _headers: Map[String, String] = Map()) = {
    new HttpResponse(code = _code, body = Option(_body), headers = _headers)
  }

  def code(_code: StatusCode.Value) = new HttpResponse(code = _code)
}

class HttpResponse(
    val cache: Boolean = false, 
    val code: StatusCode.Value = StatusCode.OK,
    val headers: Map[String, String] = Map(),
    val body: Option[String] = None) {

  def cached = new HttpResponse(true, code, headers, body)
  def header(_key: String, _value: String) = new HttpResponse(cache, code, headers + (_key -> _value), body)
  def body(_body: String) = new HttpResponse(cache, code, headers, Some(_body))
  def body(_body: Option[String]) = new HttpResponse(cache, code, headers, _body)
  
  def toHttpLiteral = s"${Http.VERSION} ${code.id} ${code.toString}\r\n" +
		  s"Date: ${Http.format(new Date)}\r\n" +
		  // TODO use the real ${project.version}
		  "Server: Aash/0.0.1-SNAPSHOT\r\n" +
		  headers.map(h => s"${h._1}: ${h._2}").mkString("\r\n") +
		  (if(headers.size == 0) "\r\n" else "\r\n\r\n") +
		  body.getOrElse("")
}