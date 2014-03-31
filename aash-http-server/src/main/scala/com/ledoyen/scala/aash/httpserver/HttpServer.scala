package com.ledoyen.scala.aash.httpserver

import scala.collection.JavaConversions._
import scala.collection.{ mutable, immutable, generic }
import org.javasimon.SimonManager
import org.slf4j.LoggerFactory
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.Executors
import java.net.InetAddress

object HttpServer {

  def logger = LoggerFactory.getLogger("HttpServer")

  def sync = new SyncHttpServer(80, Executors.newCachedThreadPool.asInstanceOf[ThreadPoolExecutor])
  def sync(port: Int = 80, pool: ThreadPoolExecutor = Executors.newCachedThreadPool.asInstanceOf[ThreadPoolExecutor]) = new SyncHttpServer(port, pool)

  def async = new AsyncHttpServer(80, Executors.newCachedThreadPool.asInstanceOf[ThreadPoolExecutor])
  def async(port: Int = 80, pool: ThreadPoolExecutor = Executors.newCachedThreadPool.asInstanceOf[ThreadPoolExecutor]) = new AsyncHttpServer(port, pool)
}

trait HttpServer {

  val url: String

  type HttpHandler = HttpRequest => HttpResponse

  protected val pathListeners: mutable.Map[String, HttpHandler] = mutable.Map()
  protected val shutdownHooks: mutable.MutableList[() => Unit] = mutable.MutableList()
  protected var statActive = false

  protected def simons = SimonManager.getRootSimon.getChildren

  def start: HttpServer

  def stop: Unit

  def registerListener(path: String, listener: HttpHandler): Unit = pathListeners += (path -> listener)

  def registerShutdownHook(hook: () => Unit): Unit = shutdownHooks += hook

  def enableStatistics: Unit = {
    HttpServer.logger.debug("Statistics enabled")
    statActive = true
  }
  def disableStatistics: Unit = {
    HttpServer.logger.debug("Statistics disabled")
    statActive = false
  }

  def resetStatistics: Unit = simons.foreach(_.reset)

  def statistics: HttpRequest => HttpResponse = (req: HttpRequest) => {
    if (req.getParameters.get("reset").exists("true" == _)) resetStatistics
    if (req.getParameters.get("enable").exists("true" == _)) enableStatistics
    if (req.getParameters.get("enable").exists("false" == _)) disableStatistics
    new HttpResponse(req.version, StatusCode.OK, s"Active threads : ${Thread.activeCount} \r\n${simons.mkString("\r\n")}", Map("Content-type" -> "text/plain; charset=UTF-8"))
  }
}