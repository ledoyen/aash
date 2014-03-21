package com.ledoyen.scala.aash.httpserver

import ExtendedStringMap._
import java.io.BufferedReader
import scala.annotation.tailrec
import java.io.Writer
import java.util.Date
import java.io.InputStreamReader
import java.io.InputStream
import com.ledoyen.scala.aash.tool.Streams
import scala.collection.immutable.Stream

package object HttpUtils {

  def parseRequest(is: InputStream): Option[HttpRequest] = {
    def readHeaders(in: BufferedReader, acc: Map[String, String]): Map[String, String] = {
      val line = in.readLine
      Option(line) match {
        case Some(x) if !"".equals(x) => {
          val sp = x.splitAt(x.prefixLength(p => ':' != p))
          readHeaders(in, acc + (sp._1 -> sp._2.substring(1).trim))
        }
        case _ => acc
      }
    }

    val in = new BufferedReader(new InputStreamReader(is))
    val firstLine = in.readLine
    if (firstLine != null) {
      val firstLineArray = firstLine.split(" ")
      val pathAndParams = readPath(firstLineArray(1))
      val headers  = readHeaders(in, Map())
      val body = readBody(headers, in)
      
      Option(new HttpRequest(firstLineArray(0), firstLineArray(2), pathAndParams._1, pathAndParams._2, headers, body))
    } else None
  }

  def readBody(headers: Map[String, String], in: BufferedReader): List[String] = {
    val contentType = headers.getIC("Content-Length")
    contentType match {
        case None => Nil
        case Some(x) => Stream.continually(in.read).take(x.toInt).map(_.toChar).mkString.split("\\r?\\n").toList
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
    response.headers.foreach(h => out.write(s"$h\r\n"))

    out.write("\r\n");
    out.write(response.body);

    out.flush
  }
}