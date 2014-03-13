package com.ledoyen.scala.feed4work

import com.ledoyen.scala.httpserver.HttpServer
import java.io.File
import java.nio.file.Paths

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

class Feed4Work(val serverPort: Int = 80, val sourceFolderPath: String = Feed4Work.defaultSourceFolderPath, statEnabled: Boolean = true, statPath: String = "/stat", val rssPath: String = "/rss") extends HttpServer(serverPort) {

  override def start = {
    super.start
    val feedSource = new FeedSource(Feed4Work.resolveSourceFolder(sourceFolderPath))
    registerListener(rssPath, new RssFeedView(feedSource))
    this
  }
}
