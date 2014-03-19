package com.ledoyen.scala.feed4work

import cronish._
import dsl._
import com.ledoyen.scala.httpserver.HttpServer
import java.io.File
import java.nio.file.Paths
import com.ledoyen.scala.feed4work.connector.JenkinsConnector
import com.ledoyen.scala.feed4work.connector.MailConnector
import dispatch.Http
import com.ledoyen.scala.feed4work.connector.Connector

object Feed4Work {

  val defaultSourceFolderPath = s"${System.getProperty("user.home")}${System.getProperty("file.separator")}.feed4work"

  def main(args: Array[String]) {
    val sourceFolderPath = Option(System.getProperty("feed4work.source.folder"))
    		.getOrElse(s"${System.getProperty("user.home")}${System.getProperty("file.separator")}.feed4work")
    val port = Option(System.getProperty("feed4work.server.port")).map(_.toInt).getOrElse(80)

    val server = new Feed4Work (port, sourceFolderPath).start
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

  private def resolveSourceFolder(path: String) = {
    val sourceFolder = Paths.get(path).toFile
    if (sourceFolder.exists && !sourceFolder.isDirectory()) throw new IllegalArgumentException(s"Feed source path [$path] is not a directory")
    if (!sourceFolder.exists) sourceFolder.mkdirs
    sourceFolder
  }
}

class Feed4Work(override val port: Int = 80, val sourceFolderPath: String = Feed4Work.defaultSourceFolderPath, val rssPath: String = "/rss") extends HttpServer(port) {

  val feedSource = new FeedSource(Feed4Work.resolveSourceFolder(sourceFolderPath))

  val connectors: List[Connector] = List(
      new JenkinsConnector("https://jenkins.megalo-company.com", "every 10 seconds".cron, "lledoyen", "azerty")
//      ,new MailConnector("https://jenkins.megalo-company.com", "every 30 seconds".cron)
      )

  override def start = {
    super.start
    new FeedView(this, feedSource, "/rss", FeedType.RSS).selfRegister
    new FeedView(this, feedSource, "/atom", FeedType.ATOM).selfRegister

    connectors.foreach(_.connect(feedSource))
    connectors.foreach(_.start)
    this
  }

  override def stop = {
	super.stop
    Scheduled.shutdown
    Http.shutdown
  }

  def push(feed: Feed) = feedSource.push(feed)
}
