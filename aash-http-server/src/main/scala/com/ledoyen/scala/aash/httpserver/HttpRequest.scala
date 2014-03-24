package com.ledoyen.scala.aash.httpserver

class HttpRequest(val method: String, val version: String, val path: String, val getParameters: Map[String, String], val headers: Map[String, String], val body: String) {
  var listenerPath: String = ""
  override def toString = s"$method $version $path $getParameters ($headers)"
}