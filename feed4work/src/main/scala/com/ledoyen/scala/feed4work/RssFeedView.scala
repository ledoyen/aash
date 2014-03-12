package com.ledoyen.scala.feed4work

import com.ledoyen.scala.httpserver.HttpRequest
import com.ledoyen.scala.httpserver.HttpResponse
import com.ledoyen.scala.httpserver.HttpResponse
import com.ledoyen.scala.httpserver.StatusCode
import scala.xml.Node
import scala.xml.XML
import com.ledoyen.scala.aash.httpserver.Http

class RssFeedView(val feedSource: FeedSource) extends Function1[HttpRequest, HttpResponse] {

  def apply(req: HttpRequest): HttpResponse = {
    def buildRss: Node =
      <rss version="2.0">
        <channel>
          <title>{ feedSource.channel.title }</title>
          <description>{ feedSource.channel.description }</description>
          <link>{ feedSource.channel.link }</link>
          <lastBuildDate>{ Http.format(feedSource.lastBuildDate) }</lastBuildDate>
          {for (item <- feedSource.last25Feeds) yield item.toXML}
        </channel>
      </rss>
    new HttpResponse(req.version, StatusCode.OK, s"<?xml version='1.0' encoding='UTF-8' ?>\r\n${buildRss.toString}")
  }
}
