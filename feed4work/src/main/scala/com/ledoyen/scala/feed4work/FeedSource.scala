package com.ledoyen.scala.feed4work

import java.io.File
import scala.io.Source
import java.nio.file.Paths
import java.util.Date
import java.io.RandomAccessFile
import org.slf4j.LoggerFactory
import java.io.PrintWriter

trait FeedSource {
  val channel: Channel
  var lastBuildDate: Date
  var last25Feeds: List[Feed]

  def push(feed: Feed): Unit
}

object FeedSource {
  def logger = LoggerFactory.getLogger("FeedSource")
}

object FileFeedSource {
  def resolveSourceFolder(path: String) = {
    val sourceFolder = Paths.get(path).toFile
    if (sourceFolder.exists && !sourceFolder.isDirectory()) throw new IllegalArgumentException(s"Feed source path [$path] is not a directory")
    if (!sourceFolder.exists) sourceFolder.mkdirs
    sourceFolder
  }
}

class InMemoryFeedSource(val channel: Channel) extends FeedSource {
  
  var lastBuildDate = new Date
  var last25Feeds: List[Feed] = List()
  
  def push(feed: Feed) = {
    last25Feeds = feed :: (last25Feeds.take(24))
    lastBuildDate = new Date
  }
}

class FileFeedSource(val sourceFolder: File) extends FeedSource {

  private val channelFilepath = s"${sourceFolder.getAbsolutePath}/channel.xml"
  private val feedsFilepath = s"${sourceFolder.getAbsolutePath}/feeds.json"

  val channel = Channel.fromXMLFile(channelFilepath)
  var lastBuildDate = new Date
  var last25Feeds = Source.fromFile(feedsFilepath).getLines.filter(line => "" != line && line.head != 0xfeff).map(Feed.fromJSON(_)).take(25).toList

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
    val pw = new PrintWriter(feedsFilepath, "UTF-8")
    pw.print(newLines.mkString("\r\n"))
    pw.close
  }
}
