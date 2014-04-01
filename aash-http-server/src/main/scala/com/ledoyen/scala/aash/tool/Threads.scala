package com.ledoyen.scala.aash.tool

import java.util.concurrent.ThreadFactory
import java.util.concurrent.Executors

object Threads {

  def factory(name: String) = new NamedThreadFactory(name, false)

  def factoryDaemon(name: String) = new NamedThreadFactory(name, true)

  def single(name: String) = Executors.newSingleThreadExecutor(new NamedThreadFactory(name, false))

  def singleDaemon(name: String) = {
    val executor = Executors.newSingleThreadExecutor(new NamedThreadFactory(name, true))
    executor.submit(new Runnable{ def run = {}})
    executor
  }
  
  class NamedThreadFactory(name: String, on: Boolean) extends ThreadFactory {
    def newThread(r: Runnable): Thread = {
      val t = new Thread(r, name)
      if(t.isDaemon && !on) t.setDaemon(on)
      t
    }
  }
}