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

class RssFeedView(val feedSource: FeedSource) extends Function1[HttpRequest, HttpResponse] {

  def apply(req: HttpRequest): HttpResponse = {
    def buildRss: Node =
      <rss version="2.0" xmlns:atom="http://www.w3.org/2005/Atom">
        <channel>
          <title>{ feedSource.channel.title }</title>
          <description>{ feedSource.channel.description }</description>
          <link>{ feedSource.channel.link }</link>
          <atom:link rel="self" href={ feedSource.channel.link }/>
          <lastBuildDate>{ Rss.format(feedSource.lastBuildDate) }</lastBuildDate>
          { for (item <- feedSource.last25Feeds) yield item.toRSS }
        </channel>
      </rss>
    new HttpResponse(req.version, StatusCode.OK, s"<?xml version='1.0' encoding='UTF-8' ?>\r\n${buildRss.toString}", List("Content-Type: application/rss+xml;charset=utf-8"))
  }
}

object Rss {
  // RFC822
  val RSS_DATE_FORMAT = new java.text.SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH)

  def format(date: Date): String = RSS_DATE_FORMAT.format(date)
}
