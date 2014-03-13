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

   def toJson: String = JacksonWrapper.serialize(this)
}

object Feed {
  def fromJSON(jsonString: String): Feed = {
    JacksonWrapper.deserialize[Feed](jsonString)
  }
}
