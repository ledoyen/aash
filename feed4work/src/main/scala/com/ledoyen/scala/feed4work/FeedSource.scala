package com.ledoyen.scala.feed4work

import java.io.File
import scala.io.Source
import java.nio.file.Paths
import java.util.Date

class FeedSource(val sourceFolder: File) {
  val channel = Channel.fromXMLFile(s"${sourceFolder.getAbsolutePath}/channel.xml")
  var lastBuildDate = new Date
  var last25Feeds: List[Feed] = Feed.fromJSONFile(s"${sourceFolder.getAbsolutePath}/feeds.json")
}
