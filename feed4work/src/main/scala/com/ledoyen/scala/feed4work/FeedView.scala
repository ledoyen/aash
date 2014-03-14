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
import com.ledoyen.scala.httpserver.HttpServer

class FeedView(val server: HttpServer, val feedSource: FeedSource, val path: String, val feedType: FeedType.Value) extends Function1[HttpRequest, HttpResponse] {

  def selfRegister = {
    server.registerListener(path, this)
  }

  def apply(req: HttpRequest): HttpResponse = {
    def buildAtom: Node =
      <feed xmlns="http://www.w3.org/2005/Atom">
        <id>tag:${ server.url },2014-01-01:feed4work</id>
        <title>{ feedSource.channel.title }</title>
        <subtitle>{ feedSource.channel.description }</subtitle>
        <link href={ server.url + path } rel="self"/>
        <updated>{ Atom.format(feedSource.lastBuildDate) }</updated>
        { for (item <- feedSource.last25Feeds) yield item.toATOM }
      </feed>

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

    def body: String = if(FeedType.RSS == feedType) buildRss.toString else buildAtom.toString
    
    def contentType: String = if(FeedType.RSS == feedType) "application/rss+xml" else "application/atom+xml"

    new HttpResponse(req.version, StatusCode.OK, s"<?xml version='1.0' encoding='UTF-8' ?>\r\n${body.toString}", List(s"Content-Type: $contentType;charset=utf-8"))
  }
}

object Atom {
  val ATOM_DATE_FORMAT = new java.text.SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ssXXX", Locale.ENGLISH)

  def format(date: Date): String = ATOM_DATE_FORMAT.format(date)
}

object Rss {
  // RFC822
  val RSS_DATE_FORMAT = new java.text.SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH)

  def format(date: Date): String = RSS_DATE_FORMAT.format(date)
}
