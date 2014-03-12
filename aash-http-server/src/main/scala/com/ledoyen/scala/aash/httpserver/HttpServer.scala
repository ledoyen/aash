package com.ledoyen.scala.httpserver

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

object HttpServer {
  def main(args: Array[String]) {
    val server = new HttpServer(Option(System.getProperty("aash.http.server.port")).map(_.toInt).getOrElse(80)).start
    if(Option(System.getProperty("aash.http.server.statistics.enabled")).exists("true" == _)) {
      server.enableStatistics
      server.registerListener(Option(System.getProperty("aash.http.server.statistics.path")).getOrElse("/stat"), server.statistics)
    }
  }
}

class HttpServer(val port: Int, val pool: ThreadPoolExecutor = Executors.newCachedThreadPool.asInstanceOf[ThreadPoolExecutor]) {

  type HttpHandler = HttpRequest => HttpResponse

  private val serverThread = new HttpServerThread
  private val pathListeners: mutable.Map[String, HttpHandler] = mutable.Map()
  private var statActive = false

  private def simons = SimonManager.getRootSimon().getChildren()

  def start = {
    serverThread.start
    HttpServer.this
  }

  def stop = serverThread.stopServer

  def registerListener(path: String, listener: HttpHandler): Unit = pathListeners += (path -> listener)

  def enableStatistics: Unit = {
    println("Statistics enabled");
    statActive = true
  }
  def disableStatistics: Unit = {
    println("Statistics disabled");
    statActive = false
  }
  def resetStatistics: Unit = simons.foreach(_.reset)

  def statistics: HttpRequest => HttpResponse = (req: HttpRequest) => {
    if (req.getParameters.get("reset").exists("true" == _)) resetStatistics
    if (req.getParameters.get("enable").exists("true" == _)) enableStatistics
    if (req.getParameters.get("enable").exists("false" == _)) disableStatistics
    new HttpResponse(req.version, StatusCode.OK, s"Active threads : ${pool.getActiveCount} (${pool.getPoolSize})\r\n${simons.mkString("\r\n")}")
  }

  class HttpServerThread extends Thread {
    println("Starting server");
    val serverSocket: ServerSocket = new ServerSocket(port)
    var mustStop = false

    override def run = {
      try {
        while (!mustStop) {
          if (!serverSocket.isClosed()) {
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
      println("Stoping server...");
      mustStop = true
      // wait for the pill to go
      Thread.sleep(32)
      serverSocket.close
    }
  }

  class SocketHandler(socket: Socket) extends Runnable {
    def run() {
      try {
        val in = new BufferedReader(new InputStreamReader(socket.getInputStream))
        val out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream)), true)
        try {
          val request = HttpUtils.parseRequest(in)

          val split = HttpUtils.option(statActive, SimonManager.getStopwatch(s"HTTP-$port-${request.path.replace("/", "")}").start)
          System.out.println(s"${Thread.currentThread().getName()} $request");

          try {
            if (pathListeners.contains(request.path)) {
              HttpUtils.writeResponse(out, pathListeners(request.path)(request))
            } else {
              HttpUtils.writeResponse(out, HttpUtils.notFound(request))
            }
          } catch {
            case e: Throwable => HttpUtils.writeResponse(out, HttpUtils.error(request, e))
          } finally {
            in.close
            out.close
            split.foreach(_.stop)
          }
        } catch {
          // InputStream is empty, meaningless case
          case e: IllegalStateException => {
            in.close
            out.close
            return
          }
        }
      } catch {
        case t: Throwable => t.printStackTrace();
      } finally {
        socket.close
      }
    }
  }
}
