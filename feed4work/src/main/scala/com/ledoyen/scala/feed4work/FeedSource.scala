package com.ledoyen.scala.feed4work

import java.io.File
import scala.io.Source
import java.nio.file.Paths
import java.util.Date
import java.io.RandomAccessFile

class FeedSource(val sourceFolder: File) {

  private val channelFilepath = s"${sourceFolder.getAbsolutePath}/channel.xml"
  private val feedsFilepath = s"${sourceFolder.getAbsolutePath}/feeds.json"

  val channel = Channel.fromXMLFile(channelFilepath)
  var lastBuildDate = new Date
  var last25Feeds: List[Feed] = Source.fromFile(feedsFilepath).getLines.filter("" != _).map(Feed.fromJSON(_)).take(25).toList

  // TODO store async
  // TODO handle transaction and
  def push(feed: Feed) = {
    last25Feeds = feed :: (last25Feeds.take(24))
    lastBuildDate = new Date
    val f = new RandomAccessFile(new File(feedsFilepath), "rw")
    try {
      f.seek(0)
      f.write(feed.toJson.getBytes)
    } finally {
      f.close
    }
  }
}
