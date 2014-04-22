package com.ledoyen.scala.aash.tool

trait StreamCallback[T] {

  def onNext(value: T)
  def onComplete
  def onError(t: Throwable)
}