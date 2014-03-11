package com.ledoyen.scala.httpserver

class HttpRequest(val method: String, val version: String, val path: String, val getParameters: Map[String, String], val headers: Map[String, String]) {
  override def toString = s"$method $version $path $getParameters ($headers)"
}