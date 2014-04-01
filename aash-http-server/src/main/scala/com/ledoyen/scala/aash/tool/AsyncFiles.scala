package com.ledoyen.scala.aash.tool
import scala.collection.JavaConversions._
import java.nio.file.Path
import java.nio.channels.AsynchronousFileChannel
import java.nio.ByteBuffer
import java.nio.file.OpenOption
import java.nio.charset.Charset
import java.nio.channels.CompletionHandler
import java.nio.file.StandardOpenOption

object AsyncFiles {

  val executor = Threads.singleDaemon("File Channel Thread")

  def read(path: Path, callback: String => Unit, exceptionCallback: Option[Throwable => Unit] = None): Unit = {
    val asyncChannel = AsynchronousFileChannel.open(path, setAsJavaSet(Set(StandardOpenOption.READ)), executor)
    val buffer = ByteBuffer.allocate(100)
    val decoder = Charset.forName("UTF-8").newDecoder
    asyncChannel.read(buffer, 0, null, new CompletionHandler[Integer, Void] {
      def completed(bytes: Integer, nothing: Void) = {
        buffer.flip
        decoder.reset
        val part = decoder.decode(buffer).toString
        callback(part)
        asyncChannel.close
      }
      def failed(t: Throwable, nothing: Void) = {
        exceptionCallback.foreach(_(t))
        asyncChannel.close
      }
    })
  }
}