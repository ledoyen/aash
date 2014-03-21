package com.ledoyen.scala.aash.httpserver

import java.util.Date
import java.util.Locale
import java.net.URL
import java.net.HttpURLConnection

object Http {

  val HTTP_DATE_FORMAT = new java.text.SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH)

  def format(date: Date): String = HTTP_DATE_FORMAT.format(date)

  def parse(source: String): Date = HTTP_DATE_FORMAT.parse(source)

  def notFound(request: HttpRequest): HttpResponse = {
    new HttpResponse(request.version, StatusCode.NOT_FOUND, "<TITLE>404 - NOT FOUND</TITLE>\r\n<P>Content cannot be found</P>")
  }

  def error(request: HttpRequest, e: Throwable): HttpResponse = {
    val body = s"<TITLE>500 - INTERNAL SERVER ERROR</TITLE>\r\n <p><b>${e.getClass.getName} ${e.getMessage}</b></p><p>${e.getStackTrace() mkString ("</p>\r\n<p>")}</p>"
    new HttpResponse(request.version, StatusCode.INTERNAL_SERVER_ERROR, body)
  }

  def option[T](condition: Boolean, o: => T) = {
    if(!condition) None else Some(o)
  }

  def connect(url: URL, req: HttpRequest): HttpResponse = {
    val con = url.openConnection.asInstanceOf[HttpURLConnection]
    con.setDoInput(true)
    con.setDoOutput(true)
    con.setUseCaches(false)

    for (h <- req.headers) {
      con.setRequestProperty(h._1, h._2)
    }
    val out = con.getOutputStream

    try {
//      out.write(req.)
      out.flush
    } finally {
      out.close
    }
    ???
  }
}