package com.ledoyen.scala.aash.tool

import java.util.concurrent.ThreadFactory
import java.util.concurrent.Executors

object Threads {

  def single(name: String) = Executors.newSingleThreadExecutor(new NamedThreadFactory(name, false))

  def singleDaemon(name: String) = {
    val executor = Executors.newSingleThreadExecutor(new NamedThreadFactory(name, true))
    executor.submit(new Runnable{ def run = {}})
    executor
  }
  
  class NamedThreadFactory(name: String, on: Boolean) extends ThreadFactory {
    def newThread(r: Runnable): Thread = {
      val t = new Thread(r, name)
      t.setDaemon(on)
      t
    }
  }
}