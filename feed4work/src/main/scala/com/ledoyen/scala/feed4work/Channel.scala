package com.ledoyen.scala.feed4work

import scala.xml.XML
import scala.xml.Node

class Channel(val title: String,
  val description: String,
  val link: String) {
  

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
    new Channel (
      title = (node \ "title").text,
      description = (node \ "description").text,
      link = (node \ "link").text
    )
  }
}