package com.ledoyen.scala.aash.httpserver

import org.junit._
import org.powermock.reflect.Whitebox
import sun.nio.ch.ThreadPool
import java.util.concurrent.ThreadPoolExecutor
import com.ledoyen.scala.aash.tool.AsyncFiles
import java.nio.file.Paths

object AsyncHttpServerTest {
  def main(args: Array[String]) {
    val server = HttpServer.async(8080).start
    server.enableStatistics
    
    server.registerListener("/stat", server.statistics)

    server.registerAsyncListener("/file1",
      (req, callback) => {
        AsyncFiles.read(
          path = Paths.get(getClass.getClassLoader.getResource("file.txt").toURI),
          callback = (content) => callback.write(HttpResponse(req.version, StatusCode.OK, content)))
      })

    server.registerAsyncListener("/file2",
      (req, callback) => {
        AsyncFiles.read(
          path = Paths.get(getClass.getClassLoader.getResource("file2.txt").toURI),
          callback = (content) => callback.write(HttpResponse(req.version, StatusCode.OK, content)))
      })

    server.registerAsyncListener("/file3",
      (req, callback) => {
        AsyncFiles.read(
          path = Paths.get(getClass.getClassLoader.getResource("file3.txt").toURI),
          callback = (content) => callback.write(HttpResponse(req.version, StatusCode.OK, content)))
      })
  }
}

class AsyncHttpServerTest {

  @Test
  @Ignore
  def testListeningServer = {
    val server = HttpServer.async(8080).start
    server.stop
  }
}