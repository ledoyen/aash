package com.ledoyen.scala.aash.httpserver

import scala.collection.JavaConversions._
import scala.collection.{ mutable, immutable, generic }
import java.net.ServerSocket
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.net.Socket
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.io.BufferedWriter
import java.io.OutputStreamWriter
import org.javasimon.SimonManager
import java.util.concurrent.ThreadPoolExecutor
import java.net.SocketException
import java.net.InetAddress
import org.slf4j.LoggerFactory
import scala.annotation.tailrec
import com.ledoyen.scala.aash.tool.Options

object SyncHttpServer {

  def main(args: Array[String]) {
    val server = new SyncHttpServer(Option(System.getProperty("aash.http.server.port")).map(_.toInt).getOrElse(80)).start
    if (Option(System.getProperty("aash.http.server.statistics.enabled")).exists("true" == _)) {
      server.enableStatistics
      server.registerListener(Option(System.getProperty("aash.http.server.statistics.path")).getOrElse("/stat"), server.statistics)
    }
  }
}

class SyncHttpServer(val port: Int = 80, val pool: ThreadPoolExecutor = Executors.newCachedThreadPool.asInstanceOf[ThreadPoolExecutor]) extends HttpServer {

  type HttpHandler = SyncHttpHandler

  val url = s"${InetAddress.getLocalHost}:$port/"

  private val serverThread = new SyncHttpServerThread

  def start = {
    serverThread.start
    SyncHttpServer.this
  }

  def stop = {
    serverThread.stopServer
    shutdownHooks.foreach(p => p())
  }

  def registerListener(path: String, listener: SyncHttpHandler): Unit = {
    registerAppropriateListener(path, listener)
  }

  override def statistics: SyncHttpHandler = (req: HttpRequest) => {
    super.statistics
    new HttpResponse(req.version, StatusCode.OK, s"Active threads : ${pool.getActiveCount} (${pool.getPoolSize})\r\n${simons.mkString("\r\n")}", Map("Content-type" -> "text/plain; charset=UTF-8"))
  }

  class SyncHttpServerThread extends Thread {
    HttpServer.logger.debug(s"Starting Sync HTTP Server on port [$port]")
    val serverSocket: ServerSocket = new ServerSocket(port)
    var mustStop = false

    override def run = {
      try {
        while (!mustStop) {
          if (!serverSocket.isClosed) {
            try {
              val socket = serverSocket.accept
              pool.submit(new SocketHandler(socket))
            }
          }
        }
      } catch {
        case e: SocketException =>
      }
    }

    def stopServer: Unit = {
      HttpServer.logger.debug("Stoping server...")
      mustStop = true
      // wait for the pill to go
      Thread.sleep(32)
      serverSocket.close
    }
  }

  class SocketHandler(socket: Socket) extends Runnable {
    def run() {
      try {
        val optionalRequest = HttpUtils.parseRequest(socket.getInputStream)
        optionalRequest match {
          case None =>
            socket.close; return
          case Some(request) =>
            HttpServer.logger.trace(s"$request")

            val out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream)), true)

            val split = Options.option(statActive, SimonManager.getStopwatch(s"HTTP-$port-${request.path.replace("/", "")}").start)
            HttpServer.logger.trace(s"$request")

            try {
              val listener = Http.getListener(pathListeners, request.path)
              listener match {
                case Some(l) => {
                  request.listenerPath = l._1
                  Http.writeHttpResponse(out, l._2(request))
                }
                case None => Http.writeHttpResponse(out, Http.notFound(request))
              }
            } catch {
              case e: Throwable => Http.writeHttpResponse(out, Http.error(e))
            } finally {
              out.close
              split.foreach(_.stop)
            }
        }
      } catch {
        case t: Throwable => HttpServer.logger.error("", t)
      } finally {
        socket.getInputStream.close
        socket.close
      }
    }
  }
}
