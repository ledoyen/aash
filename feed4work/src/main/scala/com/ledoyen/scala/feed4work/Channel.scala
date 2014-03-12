package com.ledoyen.scala.feed4work

import scala.xml.XML
import scala.xml.Node

abstract class Channel {
  val title: String
  val description: String
  val link: String

  def toXML: Node =
    <channel>
	  <title>{title}</title>
      <description>{description}</description>
      <link>{link}</link>
    </channel>
}

object Channel {
  def fromXMLFile(source: String): Channel = fromXML(XML.loadFile(source))
  def fromXML(node: Node): Channel = {
    new Channel {
      val title = (node \ "title").text
      val description = (node \ "description").text
      val link = (node \ "link").text
    }
  }
}