package com.ledoyen.scala.feed4work

import java.io.File
import scala.io.Source
import java.nio.file.Paths
import java.util.Date
import java.io.RandomAccessFile
import org.slf4j.LoggerFactory
import java.io.PrintWriter

object FeedSource {
  def logger = LoggerFactory.getLogger("FeedSource")
}

class FeedSource(val sourceFolder: File) {

  private val channelFilepath = s"${sourceFolder.getAbsolutePath}/channel.xml"
  private val feedsFilepath = s"${sourceFolder.getAbsolutePath}/feeds.json"

  val channel = Channel.fromXMLFile(channelFilepath)
  var lastBuildDate = new Date
  var last25Feeds: List[Feed] = Source.fromFile(feedsFilepath).getLines.filter(line => "" != line && line.head != 0xfeff).map(Feed.fromJSON(_)).take(25).toList

  // TODO store async
  // TODO use some queue
  def push(feed: Feed) = {
    this.synchronized {
	    last25Feeds = feed :: (last25Feeds.take(24))
	    lastBuildDate = new Date
    	writeToFile(feed)
    }
    FeedSource.logger.trace(s"new Feed pushed [${feed.title}]")
  }

  private def writeToFile(feed: Feed) = {
    val newLines = feed.toJson :: Source.fromFile(feedsFilepath).getLines.filter(line => "" != line && line.head != 0xfeff).take(299).toList
    val pw = new PrintWriter(feedsFilepath , "UTF-8")
    pw.print(newLines.mkString("\r\n"))
    pw.close
  }
}
