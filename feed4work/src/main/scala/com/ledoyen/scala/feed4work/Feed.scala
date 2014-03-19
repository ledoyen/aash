package com.ledoyen.scala.feed4work

import com.ledoyen.scala.feed4work.json._

import java.util.Date
import scala.xml.Node
import com.ledoyen.scala.aash.httpserver.Http
import scala.util.parsing.json.JSON
import scala.io.Source

class Feed(val title: String, val link: String, val pubDate: Date, val description: String) {
  def toRSS: Node =
    <item>
      <title>{ title }</title>
      <link>{ link }</link>
      <description>{ description }</description>
      <pubDate>{ Rss.format(pubDate) }</pubDate>
      <guid>http://feed4work{ pubDate.getTime }.com/</guid>
    </item>

  def toATOM: Node =
    <entry>
      <title>{ title }</title>
      <link href={ link }/>
      <summary type="html">{ description }</summary>
      <id>http://feed4work{ pubDate.getTime }.com/</id>
      <updated>{ Atom.format(pubDate) }</updated>
      <author>
        <name>feed4work</name>
      </author>
    </entry>

  def toJson: String = JacksonWrapper.serialize(this)

  override def toString = s"$title ($link) $pubDate"
}

object Feed {
  def fromJSON(jsonString: String): Feed = {
    JacksonWrapper.deserialize[Feed](jsonString)
  }
}

object FeedType extends Enumeration {
  type Feed = Value
  
  val RSS = Value
  val ATOM = Value
}

