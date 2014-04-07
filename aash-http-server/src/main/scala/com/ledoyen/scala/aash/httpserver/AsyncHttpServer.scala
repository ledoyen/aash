package com.ledoyen.scala.aash.httpserver

import scala.collection.JavaConversions._
import scala.collection.{ mutable, immutable, generic }
import org.slf4j.LoggerFactory
import java.nio.channels.AsynchronousServerSocketChannel
import java.net.InetSocketAddress
import java.nio.channels.CompletionHandler
import java.nio.channels.AsynchronousSocketChannel
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.Executors
import java.nio.channels.AsynchronousChannelGroup
import java.util.concurrent.ThreadFactory
import java.nio.channels.AsynchronousCloseException
import java.nio.ByteBuffer
import java.nio.charset.CharsetDecoder
import java.nio.charset.Charset
import scala.collection.mutable.StringBuilder
import java.nio.charset.CharacterCodingException
import java.io.ByteArrayInputStream
import org.javasimon.SimonManager
import com.ledoyen.scala.aash.tool.Options
import org.javasimon.Split
import java.net.InetAddress
import java.nio.channels.spi.AsynchronousChannelProvider
import sun.nio.ch.ThreadPool
import sun.nio.ch.AsynchronousChannelGroupImpl

/**
 * http://openjdk.java.net/projects/nio/presentations/TS-4222.pdf
 * TODO implements websocket handling, sample on https://github.com/TooTallNate/Java-WebSocket
 */
object AsyncHttpServer {

  val BUFFER_SIZE = 1024
}

class AsyncHttpServer(val port: Int = 80, val pool: ThreadPoolExecutor = Executors.newCachedThreadPool.asInstanceOf[ThreadPoolExecutor]) extends HttpServer {

  type HttpHandler = AsyncHttpHandler

  val UTF8 = Charset.forName("UTF-8")

  val url = s"${InetAddress.getLocalHost}:$port/"

  private val serverImplementation = new AsyncHttpServerImpl

  def start = {
    serverImplementation.run
    AsyncHttpServer.this
  }

  def stop = {
    serverImplementation.stopServer
    shutdownHooks.foreach(p => p())
  }

  def registerListener(path: String, listener: SyncHttpHandler): Unit = {
    registerAppropriateListener(path, (req: HttpRequest, callback: WriteCallback) => callback.write(listener(req)))
  }

  def registerAsyncListener(path: String, listener: AsyncHttpHandler): Unit = {
    registerAppropriateListener(path, listener)
  }

  class AsyncHttpServerImpl {
    import scala.reflect.runtime.universe._
    import scala.reflect.runtime.currentMirror
    import scala.reflect.runtime.{universe => ru}

    HttpServer.logger.debug(s"Starting Async HTTP Server on port [$port]")
    val group = AsynchronousChannelGroup.withFixedThreadPool(1, new EventLoopThreadFactory)
    
    val ssc = AsynchronousServerSocketChannel.open(group).bind(new InetSocketAddress("localhost", port))

    def eventLoopThreadGroup = group

    def run = {
      if (ssc.isOpen) {
        try {
          ssc.accept(ssc, new SocketCompletionHandler)
        } catch {
          case e: Exception => e.printStackTrace
        }
      }
    }

    def stopServer: Unit = {
      HttpServer.logger.debug("Stoping server...")
      ssc.close
      group.shutdown
    }
  }

  class SocketCompletionHandler extends CompletionHandler[AsynchronousSocketChannel, AsynchronousServerSocketChannel] {
    def completed(asc: AsynchronousSocketChannel, ssc: AsynchronousServerSocketChannel) {
      val buffer = ByteBuffer.allocate(AsyncHttpServer.BUFFER_SIZE)
      val rch = new ReadCompletionHandler(asc, buffer, UTF8.newDecoder)
      asc.read(buffer, null, rch)
      if (ssc.isOpen) {
        ssc.accept(ssc, new SocketCompletionHandler)
      }

    }

    def failed(exception: Throwable, ssc: AsynchronousServerSocketChannel) {
      if (ssc.isOpen) {
        ssc.accept(ssc, new SocketCompletionHandler)
      }
      exception match {
        case e: AsynchronousCloseException => // Do nothing, server have been stopped, so channel have been closed
        case _ => {
          println("Ohohohoh")
          exception.printStackTrace
        }
      }
    }

    class ReadCompletionHandler(asc: AsynchronousSocketChannel, buffer: ByteBuffer, decoder: CharsetDecoder) extends CompletionHandler[Integer, Void] {
      var acc = ""
      def completed(bytes: Integer, nothing: Void): Unit = {
        if (!asc.isOpen) return
        try {
          buffer.flip
          decoder.reset
          val part = decoder.decode(buffer).toString
          acc = acc + part
          buffer.clear

          // More data to be read
          if (bytes != -1 && bytes == AsyncHttpServer.BUFFER_SIZE) {
            asc.read(buffer, null, this)
          } else {
            asc.shutdownInput
            val optionalRequest = HttpUtils.parseRequest(new ByteArrayInputStream(acc.toString.getBytes(UTF8)))
            optionalRequest match {
              case None =>
                asc.close; return
              case Some(request) => {
                val split = Options.option(statActive, SimonManager.getStopwatch(s"HTTP-$port-${request.path.replace("/", "")}").start)
                HttpServer.logger.trace(s"$request")
                // If any async listener is registered
                val asyncListener = Http.getListener(pathListeners, request.path)
                asyncListener match {
                  case Some(l) => {
                    request.listenerPath = l._1
                    l._2(request, new WriteCallBackImpl(asc, split))
                  } case None => {
                    new WriteCallBackImpl(asc, None).write(Http.notFound)
                  }
                }
              }
            }
          }
        } catch {
          case e: Exception => failed(e, nothing)
        }
      }
      def failed(exception: Throwable, nothing: Void) = {
        HttpServer.logger.warn("Unable to complete reading from socket", exception)
        new WriteCallBackImpl(asc, None).write(Http.error(exception))
      }
    }
  }

  class WriteCompletionHandler(asc: AsynchronousSocketChannel, buffer: ByteBuffer) extends CompletionHandler[Integer, Option[Split]] {
    def completed(bytes: Integer, split: Option[Split]): Unit = {
      if (!asc.isOpen) return
      if (buffer.hasRemaining) {
        asc.write(buffer, null, this)
      } else {
        split.foreach(_.stop)
        asc.shutdownOutput
        asc.close
      }
    }
    def failed(exception: Throwable, split: Option[Split]) = {
      HttpServer.logger.warn("Unable to complete writing to socket", exception)
      split.foreach(_.stop)
      asc.shutdownOutput
      asc.close
    }
  }

  class EventLoopThreadFactory extends ThreadFactory {
    def newThread(r: Runnable): Thread = {
      new Thread(r, "Event Loop Thread")
    }
  }

  private class WriteCallBackImpl(asc: AsynchronousSocketChannel, split: Option[Split]) extends WriteCallback {
    def write(response: HttpResponse) = {
      val sendingBuffer = ByteBuffer.wrap(response.toHttpLiteral.getBytes(UTF8))
      asc.write(sendingBuffer, split, new WriteCompletionHandler(asc, sendingBuffer))
    }
  }
}