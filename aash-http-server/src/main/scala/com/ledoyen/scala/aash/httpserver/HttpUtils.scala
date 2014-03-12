package com.ledoyen.scala.httpserver

import java.io.BufferedReader
import scala.annotation.tailrec
import java.io.Writer
import java.util.Date
import com.ledoyen.scala.aash.httpserver.Http

package object HttpUtils {

  def parseRequest(in: BufferedReader): HttpRequest = {
    val firstLine = in.readLine
    if(firstLine != null) {
	    val firstLineArray = firstLine.split(" ")
	    val pathAndParams = readPath(firstLineArray(1))
	    new HttpRequest(firstLineArray(0), firstLineArray(2), pathAndParams._1, pathAndParams._2, readHeaders(in, Map()))
    } else throw new IllegalStateException("InputStream is empty")
  }

  @tailrec
  def readHeaders(in: BufferedReader, acc: Map[String, String]): Map[String, String] = {
    val line = in.readLine
    Option(line) match {
      case Some(x) if !"".equals(x) => {
        val sp = x.splitAt(x.prefixLength(p => ':' != p))
        readHeaders(in, acc + (sp._1 -> sp._2))
      }
      case _ => acc
    }
  }

  def readPath(p: String): (String, Map[String, String]) = {
    val sp = p.splitAt(p.prefixLength(x => '?' != x))
    (sp._1, readPath(sp._2.drop(1), Map()))
  }

  @tailrec
  def readPath(p: String, acc: Map[String, String]): Map[String, String] = {
    if (!"".equals(p)) {
      val sp = p.splitAt(p.prefixLength(p => '&' != p))
      val v = sp._1.splitAt(p.prefixLength(p => '=' != p))
      readPath(sp._2.drop(1), acc + (v._1 -> v._2.drop(1)))
    } else acc
  }

  def writeResponse(out: Writer, response: HttpResponse): Unit = {
    out.write(s"${response.version} ${response.code.id} ${response.code.toString}\r\n")
    out.write(s"Date: ${Http.format(new Date())}\r\n")
    // TODO use the real ${project.version}
    out.write("Server: Aash/0.0.1-SNAPSHOT\r\n");

    out.write("\r\n");
    out.write(response.body);

    out.flush
  }

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
}