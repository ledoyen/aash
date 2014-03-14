package com.ledoyen.scala.feed4work.connector

import dispatch._
import dispatch.Defaults._
import com.ledoyen.scala.feed4work.FeedSource
import cronish.Cron
import cronish.dsl.CronTask
import com.ledoyen.scala.httpserver.HttpRequest
import org.slf4j.LoggerFactory

object JenkinsConnector {
  def logger = LoggerFactory.getLogger("JenkinsConnector")
}

class JenkinsConnector(val adress: String, override val cron: Cron) extends Connector(cron) {

  val svc = url(adress)

  def task = {
    val startDate = System.currentTimeMillis
    val res = Http(svc.POST OK as.String).either

    for (resp <- res) resp match {
      case Right(respBody) => {
        val duration = System.currentTimeMillis() - startDate
        JenkinsConnector.logger.trace(s"OK in ${duration} ms  (${respBody})")
      }
      case Left(e) => {
        val duration = System.currentTimeMillis() - startDate
        JenkinsConnector.logger.trace(s"KO in ${duration} ms  KO (${e.getMessage()})")
      }
    }
  }
}