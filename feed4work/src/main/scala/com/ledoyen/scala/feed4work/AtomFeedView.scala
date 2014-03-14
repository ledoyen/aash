package com.ledoyen.scala.feed4work

import com.ledoyen.scala.httpserver.HttpRequest
import com.ledoyen.scala.httpserver.HttpResponse
import com.ledoyen.scala.httpserver.HttpResponse
import com.ledoyen.scala.httpserver.StatusCode
import scala.xml.Node
import scala.xml.XML
import com.ledoyen.scala.aash.httpserver.Http
import java.util.Locale
import java.util.Date

class AtomFeedView(val feedSource: FeedSource) extends Function1[HttpRequest, HttpResponse] {

  def apply(req: HttpRequest): HttpResponse = {
    def buildAtom: Node =
      <feed xmlns="http://www.w3.org/2005/Atom">
        <id>tag:localhost.feed4work.com,2014-01-01:feed4work</id>
        <title>{ feedSource.channel.title }</title>
        <subtitle>{ feedSource.channel.description }</subtitle>
        <link href={ feedSource.channel.link } rel="self"/>
        <updated>{ Atom.format(feedSource.lastBuildDate) }</updated>
        { for (item <- feedSource.last25Feeds) yield item.toATOM }
      </feed>
    new HttpResponse(req.version, StatusCode.OK, s"<?xml version='1.0' encoding='UTF-8' ?>\r\n${buildAtom.toString}", List("Content-Type: application/atom+xml;charset=utf-8"))
  }
}

object Atom {
  val ATOM_DATE_FORMAT = new java.text.SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ssXXX", Locale.ENGLISH)

  def format(date: Date): String = ATOM_DATE_FORMAT.format(date)
}
