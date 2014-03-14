package com.ledoyen.scala.feed4work.connector

import com.ledoyen.scala.feed4work.FeedSource
import cronish.Cron
import cronish.dsl.CronTask
import com.ledoyen.scala.httpserver.HttpRequest

class MailConnector(val url: String, override val cron: Cron) extends Connector(cron) {

  def task = println(s"${Thread.currentThread().getName()} - Mail pooled")
}