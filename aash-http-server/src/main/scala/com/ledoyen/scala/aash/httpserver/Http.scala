package com.ledoyen.scala.aash.httpserver

import java.util.Date
import java.util.Locale
import java.net.URL
import java.net.HttpURLConnection
import java.io.Writer
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.Socket
import java.io.PrintWriter
import scala.io.Source
import java.io.InputStream
import java.io.ByteArrayOutputStream

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

  def connect(url: URL, req: HttpRequest): Option[HttpResponse] = {
    val socket = new Socket(url.getHost, if (url.getPort != -1) url.getPort else 80)
    try {
      val out = socket.getOutputStream()
//      val out = new ByteArrayOutputStream
      writeHttpRequest(new PrintWriter(out, true), req, url)
//      print(out.toString("UTF-8"))
//      println(Source.fromInputStream(socket.getInputStream).getLines().mkString("\n"))
      parseResponse(socket.getInputStream)
//      ???
    } finally {
      socket.close
    }
  }

  private def printLines(in: BufferedReader, acc: List[String]): List[String] = {
    val line = in.readLine
    Option(line) match {
      case Some(x) => {
        println(x)
        printLines(in, acc :+ x)
      }
      case _ => acc
    }
  }

  def writeHttpResponse(out: Writer, response: HttpResponse): Unit = {
    out.write(s"${response.version} ${response.code.id} ${response.code.toString}\r\n")
    out.write(s"Date: ${Http.format(new Date())}\r\n")
    // TODO use the real ${project.version}
    out.write("Server: Aash/0.0.1-SNAPSHOT\r\n");
    response.headers.foreach(h => out.write(s"$h\r\n"))

    out.write("\r\n");
    out.write(response.body);

    out.flush
  }

  def writeHttpRequest(out: Writer, req: HttpRequest, url: URL): Unit = {
    val firstLine = s"${req.method} ${req.path} ${req.version}\r\n"
    out.write(firstLine)
    val host = s"Host: ${url.getHost}\r\n"
    out.write(host)
    val keepAlive = "Connection: keep-alive\r\n"
    out.write(keepAlive)

    req.headers.foreach(h => out.write(s"${h._1}: ${h._2}\r\n"))

    if (req.body != null) {
      out.write(s"Content-Length: ${req.body.length}\r\n")
      out.write("\r\n")
      out.write(req.body)
    } else {
      out.write("\r\n");
    }

    out.flush
  }

  def parseResponse(is: InputStream): Option[HttpResponse] = {
    val in = new BufferedReader(new InputStreamReader(is))
    val firstLine = in.readLine
    if (firstLine != null) {
      val firstLineArray = firstLine.split(" ")
      val version = firstLineArray(0)
      val code = StatusCode(firstLineArray(1).toInt)
      val headers = HttpUtils.readHeaders(in, Map())
      val body = HttpUtils.readResponseBody(in, is)

      Option(new HttpResponse(version, code, body, headers))
    } else None
  }
}