package com.ledoyen.scala.feed4work

import cronish._
import dsl._
import java.io.File
import java.nio.file.Paths
import dispatch.Http
import com.ledoyen.scala.feed4work.connector.JenkinsConnector
import com.ledoyen.scala.feed4work.connector.MailConnector
import com.ledoyen.scala.feed4work.connector.Connector
import com.ledoyen.scala.aash.httpserver._

object Feed4Work {

  val defaultSourceFolderPath = s"${System.getProperty("user.home")}${System.getProperty("file.separator")}.feed4work"

  def main(args: Array[String]) {
    val sourceFolderPath = Option(System.getProperty("feed4work.source.folder"))
      .getOrElse(s"${System.getProperty("user.home")}${System.getProperty("file.separator")}.feed4work")
    val port = Option(System.getProperty("feed4work.server.port")).map(_.toInt).getOrElse(80)

    val server = new Feed4Work(port, new FileFeedSource(FileFeedSource.resolveSourceFolder(sourceFolderPath))).start
    try {
      if (Option(System.getProperty("feed4work.server.statistics.enabled")).exists("true" == _)) {
        server.enableStatistics
        server.registerListener(Option(System.getProperty("feed4work.server.statistics.path")).getOrElse("/stat"), server.statistics)
      }
    } catch {
      case t: Throwable => {
        t.printStackTrace
        server.stop
      }
    }
  }
}

class Feed4Work(override val port: Int = 80, val feedSource: FeedSource, val rssPath: String = "/rss") extends SyncHttpServer(port) {
  import scala.collection.{ mutable, immutable, generic }

  val connectors: mutable.Map[String, Connector] = mutable.Map()

  override def start = {
    super.start
    new FeedView(this, feedSource, "/rss", FeedType.RSS).selfRegister
    new FeedView(this, feedSource, "/atom", FeedType.ATOM).selfRegister
    this
  }

  override def stop = {
    super.stop
    Scheduled.shutdown
    Http.shutdown
  }

  def push(feed: Feed) = feedSource.push(feed)

  def registerConnector(name: String, connector: Connector): Connector = {
    connectors += (name -> connector)
    connector.connect(feedSource)
    connector.start
  }
}
