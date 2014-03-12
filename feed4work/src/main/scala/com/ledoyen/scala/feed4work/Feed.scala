package com.ledoyen.scala.feed4work

import com.ledoyen.scala.feed4work.json._

import java.util.Date
import scala.xml.Node
import com.ledoyen.scala.aash.httpserver.Http
import scala.util.parsing.json.JSON
import scala.io.Source

class Feed(val title: String, val link: String, val pubDate: Date, val description: String) {
  def toXML: Node =
    <item>
      <title>{ title }</title>
      <link>{ link }</link>
      <description>{ description }</description>
      <pubDate>{ Http.format(pubDate) }</pubDate>
    </item>
}

object Feed {
  def fromJSONFile(source: String): List[Feed] = {
    val jsonString = Source.fromFile(source).getLines mkString ("\r\n")
    fromJSON(jsonString)
  }

  def fromJSON(jsonString: String): List[Feed] = {
    JacksonWrapper.deserialize[List[Feed]](jsonString)
  }

  def toJson(feeds : List[Feed]): String = JacksonWrapper.prettySerialize(feeds)
}
