package com.ledoyen.scala.aash.tool

import java.net.URL
import java.nio.channels.AsynchronousChannelGroup
import java.nio.channels.AsynchronousSocketChannel
import java.net.InetSocketAddress
import java.nio.channels.CompletionHandler
import java.nio.ByteBuffer
import java.nio.charset.Charset
import java.io.ByteArrayInputStream
import com.ledoyen.scala.aash.httpserver.Http
import com.ledoyen.scala.aash.httpserver.HttpRequest
import com.ledoyen.scala.aash.httpserver.HttpResponse

object AsyncHttp {

  val UTF8 = Charset.forName("UTF-8")

  val BUFFER_SIZE = 1024

  val group = AsynchronousChannelGroup.withFixedThreadPool(1, Threads.factoryDaemon("HTTP Connection Channel Thread"))

  def shutdown = group.shutdownNow

  def connect(url: URL, req: HttpRequest, followRedirect: Boolean = true, callback: Either[Throwable, HttpResponse] => Unit): Unit = {
    val client = AsynchronousSocketChannel.open(group)

    client.connect(new InetSocketAddress(url.getHost, if (url.getPort != -1) url.getPort else url.getDefaultPort), null, new CompletionHandler[Void, Void] {
      def completed(nothing: Void, nothing2: Void) = {
        // Connection is established, write now
        val request = req.toHttpLiteral(url)
        val sendingBuffer = ByteBuffer.wrap(request.getBytes(UTF8))

        client.write(sendingBuffer, null, new CompletionHandler[Integer, Void] {
          def completed(bytes: Integer, nothing2: Void) = {
            if (sendingBuffer.hasRemaining) {
              client.write(sendingBuffer, null, this)
            } else {
              client.shutdownOutput
              // Data is sent, read response now
              val buffer = ByteBuffer.allocate(BUFFER_SIZE)
              val decoder = UTF8.newDecoder
              var acc = ""

              client.read(buffer, null, new CompletionHandler[Integer, Void] {
                def completed(bytes: Integer, nothing2: Void) = {
                  buffer.flip
                  decoder.reset
                  val part = decoder.decode(buffer).toString
                  acc = acc + part
                  buffer.clear

                  if (bytes != -1 ) {
                    client.read(buffer, null, this)
                  } else {
                    client.shutdownInput
                    client.close
                    val optionalResponse = Http.parseResponse(new ByteArrayInputStream(acc.toString.getBytes(UTF8)))
                    optionalResponse match {
                      case Some(response) => {
                        callback(Right(response))
                      } case None => {
                        callback(Right(Http.notFound))
                      }
                    }
                  }
                }
                def failed(t: Throwable, nothing: Void) = callback(Left(t))
              })
            }
          }
          def failed(t: Throwable, nothing: Void) = callback(Left(t))
        })
      }
      def failed(t: Throwable, nothing: Void) = callback(Left(t))
    })
  }
}