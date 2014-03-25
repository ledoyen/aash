package com.ledoyen.scala.aash.httpserver

object StatusCode extends Enumeration {
  type StatusCode = Value

  // 2xx
  val OK = Value(200, "OK")

  // 3xx
  val MOVED_PERMANENTLY = Value(301, "Moved Permanently")
  val FOUND = Value(302, "Found")

  // 4xx
  val BAD_REQUEST = Value(400, "Bad Request")
  val UNAUTHORIZED = Value(401, "Unauthorized")
  val FORBIDDEN = Value(403, "Forbidden")
  val NOT_FOUND = Value(404, "Not Found")

  // 5xx
  val INTERNAL_SERVER_ERROR = Value(500, "Internal Server Error")
  val NOT_IMPLEMENTED = Value(501, "Not Implemented")
  val SERVICE_UNAVAILABLE = Value(503, "Service Unavailable")
}