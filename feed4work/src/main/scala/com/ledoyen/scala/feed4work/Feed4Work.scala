package com.ledoyen.scala.feed4work

import com.ledoyen.scala.httpserver.HttpServer
import java.io.File
import java.nio.file.Paths

object Feed4Work {

  def main(args: Array[String]) {
    val server = new HttpServer(Option(System.getProperty("feed4work.server.port")).map(_.toInt).getOrElse(80)).start
    if(Option(System.getProperty("feed4work.server.statistics.enabled")).exists("true" == _)) {
      server.enableStatistics
      server.registerListener(Option(System.getProperty("feed4work.server.statistics.path")).getOrElse("/stat"), server.statistics)
    }
    try {
	    val sourceFolderPath = Option(System.getProperty("feed4work.source.folder")).getOrElse(s"${System.getProperty("user.home")}${System.getProperty("file.separator")}.feed4work")
	    val feedSource = new FeedSource(resolveSourceFolder(sourceFolderPath))
	    server.registerListener("/rss", new RssFeedView(feedSource))
    } catch {
      case t: Throwable => {
        t.printStackTrace
        server.stop
      }
    }
  }

  private def resolveSourceFolder(path: String) = {
    val sourceFolder = Paths.get(path).toFile
    if(sourceFolder.exists && !sourceFolder.isDirectory()) throw new IllegalArgumentException(s"Feed source path [$path] is not a directory")
    if(!sourceFolder.exists) sourceFolder.mkdirs
    sourceFolder
  }
}