package com.ledoyen.scala.aash.tool

import scala.collection.JavaConversions._
import java.nio.file.Path
import java.nio.channels.AsynchronousFileChannel
import java.nio.ByteBuffer
import java.nio.file.OpenOption
import java.nio.charset.Charset
import java.nio.channels.CompletionHandler
import java.nio.file.StandardOpenOption
import java.nio.charset.CharsetDecoder

object AsyncFiles {

  val executor = Threads.singleDaemon("File Channel Thread")
  val BUFFER_SIZE = 1024

  def read(path: Path, callback: String => Unit, exceptionCallback: Option[Throwable => Unit] = None): Unit = {
    val asyncChannel = AsynchronousFileChannel.open(path, setAsJavaSet(Set(StandardOpenOption.READ)), executor)
    val buffer = ByteBuffer.allocate(BUFFER_SIZE)
    val decoder = Charset.forName("UTF-8").newDecoder
    asyncChannel.read(buffer, 0, null, new ReadCompletionHandler(asyncChannel, buffer, "", 0, decoder, callback, exceptionCallback))
  }

  private class ReadCompletionHandler(afc: AsynchronousFileChannel, buffer: ByteBuffer, acc: String, pos: Long, decoder: CharsetDecoder, callback: String => Unit, exceptionCallback: Option[Throwable => Unit]) extends CompletionHandler[Integer, Void] {
    def completed(bytes: Integer, nothing: Void) = {
      buffer.flip
      decoder.reset

      val part = decoder.decode(buffer).toString
      val newacc = acc + part
      buffer.clear

      if (bytes != -1 && bytes == AsyncFiles.BUFFER_SIZE) {
        val newpos = pos + bytes
        afc.read(buffer, pos, null, new ReadCompletionHandler(afc, buffer, newacc, newpos, decoder, callback, exceptionCallback))
      } else {
        afc.close
        callback(newacc)
      }
    }
    def failed(t: Throwable, nothing: Void) = {
      exceptionCallback.foreach(_(t))
      afc.close
    }
  }

  def read(path: Path, callback: StreamCallback[String]): Unit = {
    val asyncChannel = AsynchronousFileChannel.open(path, setAsJavaSet(Set(StandardOpenOption.READ)), executor)
    val buffer = ByteBuffer.allocate(BUFFER_SIZE)
    val decoder = Charset.forName("UTF-8").newDecoder
    asyncChannel.read(buffer, 0, null, new StreamReadCompletionHandler(asyncChannel, buffer, 0, decoder, callback))
  }

  private class StreamReadCompletionHandler(afc: AsynchronousFileChannel, buffer: ByteBuffer, pos: Long, decoder: CharsetDecoder, callback: StreamCallback[String]) extends CompletionHandler[Integer, Void] {
    def completed(bytes: Integer, nothing: Void) = {
      buffer.flip
      decoder.reset

      val part = decoder.decode(buffer).toString
      callback.onNext(part)
      buffer.clear

      if (bytes != -1 && bytes == AsyncFiles.BUFFER_SIZE) {
        val newpos = pos + bytes
        afc.read(buffer, pos, null, new StreamReadCompletionHandler(afc, buffer, newpos, decoder, callback))
      } else {
        afc.close
        callback.onComplete
      }
    }
    def failed(t: Throwable, nothing: Void) = {
      callback.onError(t)
      afc.close
    }
  }
}