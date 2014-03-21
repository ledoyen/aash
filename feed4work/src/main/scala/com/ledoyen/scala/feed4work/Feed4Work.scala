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
      new JenkinsConnector(
          adress = "https://jenkins.megalo-company.com",
          cron = "every 30 seconds".cron,
          login = System.getProperty("feed4work.server.jenkins.login"),
          password = System.getProperty("feed4work.server.jenkins.password"))
      , new MailConnector(
          tag = "OUTLOOK",
          host = "outlook.office365.com",
          link = "http://google.com",
          port = Option(995),
          protocol = "pop3s",
          cron = "every 10 minutes".cron,
          login = System.getProperty("feed4work.server.outlook.login"),
          password = System.getProperty("feed4work.server.outlook.password"))
      , new MailConnector(
          tag = "GMAIL",
          host = "imap.gmail.com",
          link = "http://google.com",
          protocol = "imaps",
          cron = "every 3 minutes".cron,
          login = System.getProperty("feed4work.server.gmail.login"),
          password = System.getProperty("feed4work.server.gmail.password"))
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
