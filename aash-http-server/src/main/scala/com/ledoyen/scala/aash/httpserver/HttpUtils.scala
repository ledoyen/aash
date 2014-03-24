package com.ledoyen.scala.aash.httpserver

import ExtendedStringMap._
import java.io.BufferedReader
import scala.annotation.tailrec
import java.io.Writer
import java.util.Date
import java.io.InputStreamReader
import java.io.InputStream
import scala.collection.immutable.Stream
import com.ledoyen.scala.aash.tool.Streams

package object HttpUtils {

  def parseRequest(is: InputStream): Option[HttpRequest] = {
    val in = new BufferedReader(new InputStreamReader(is))
    val firstLine = in.readLine
    if (firstLine != null) {
      val firstLineArray = firstLine.split(" ")
      val method = firstLineArray(0)
      val pathAndParams = readRequestPath(firstLineArray(1))
      val headers = readHeaders(in, Map())
      val body = if("POST".equalsIgnoreCase(method)) readRequestBody(headers, in) else ""

      Option(new HttpRequest(method, firstLineArray(2), pathAndParams._1, pathAndParams._2, headers, body))
    } else None
  }

  def readRequestPath(p: String): (String, Map[String, String]) = {
    val sp = p.splitAt(p.prefixLength(x => '?' != x))
    (sp._1, readRequestPath(sp._2.drop(1), Map()))
  }

  @tailrec
  def readRequestPath(p: String, acc: Map[String, String]): Map[String, String] = {
    if (!"".equals(p)) {
      val sp = p.splitAt(p.prefixLength(p => '&' != p))
      val v = sp._1.splitAt(p.prefixLength(p => '=' != p))
      readRequestPath(sp._2.drop(1), acc + (v._1 -> v._2.drop(1)))
    } else acc
  }

  @tailrec
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

  def readRequestBody(headers: Map[String, String], in: BufferedReader): String = {
    val contentType = headers.getIC("Content-Length")
    contentType match {
      case None => Stream.continually(in.read).takeWhile(_ != -1).map(_.toChar).mkString
      case Some(x) => Stream.continually(in.read).take(x.toInt).map(_.toChar).mkString
    }
  }
}