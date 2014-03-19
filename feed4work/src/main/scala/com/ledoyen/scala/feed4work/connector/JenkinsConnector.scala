package com.ledoyen.scala.feed4work.connector

import scala.collection.{ mutable, immutable, generic }
import com.ledoyen.scala.feed4work.json._
import dispatch._
import dispatch.Defaults._
import com.ledoyen.scala.feed4work.FeedSource
import cronish.Cron
import cronish.dsl.CronTask
import com.ledoyen.scala.httpserver.HttpRequest
import org.slf4j.LoggerFactory
import scala.util.parsing.json.JSON
import scala.concurrent.duration._
import java.util.Date
import com.ledoyen.scala.feed4work.Feed
import com.ledoyen.scala.feed4work.duration.Durations

object JenkinsConnector {
  def logger = LoggerFactory.getLogger("JenkinsConnector")

  def parseJobs(jsonString: String): List[(String, String)] = {
    for {
      Some(M(map)) <- List(JSON.parseFull(jsonString))
      L(jobs) = map("jobs")
      M(job) <- jobs
      S(name) = job("name")
      S(jobUrl) = job("url")
    } yield {
      (name, jobUrl)
    }
  }

  def parseLastBuild(name: String, jsonString: String) = {
    val parsed = for {
      Some(M(map)) <- List(JSON.parseFull(jsonString))
      S(result) = map("result")
      D(duration) = map("duration")
      D(timestamp) = map("timestamp")
      S(buildUrl) = map("url")
      B(building) = map("building")
    } yield {
      new Build(name, result, Duration(duration, MILLISECONDS), timestamp.longValue, buildUrl, building)
    }
    parsed.head
  }

  def buildFeed(build: Build): Feed = new Feed(s"[JENKINS] [${build.result}] ${build.name} in ${Durations.format(build.duration)}", build.url, new Date(build.timestamp), "")
}

class JenkinsConnector(val adress: String, override val cron: Cron, val user: String = null, val password: String = "") extends Connector(cron) {

  private val histo: mutable.Map[String, Long] = mutable.Map()

  val svc = if (user == null) url(adress) else url(adress).as_!(user, password)

  def task = {
    val startDate = System.currentTimeMillis
    val apiEndPoint = svc / "api" / "json"
    val res = Http(apiEndPoint.POST OK as.String).either

    for (resp <- res) resp match {
      case Right(respBody) => {
        val jobs = JenkinsConnector.parseJobs(respBody)
        for (job <- jobs) {
          jobTask(startDate, job)
        }
      }
      case Left(e) => {
        val duration = System.currentTimeMillis() - startDate
        JenkinsConnector.logger.trace(s"KO in ${duration} ms  KO (${e.getMessage()})")
      }
    }
  }

  def jobTask(startDate: Long, job: (String, String)) = {
    val jobApiEndPoint = if (user == null) url(job._2) else url(job._2).as_!(user, password) / "lastBuild" / "api" / "json"
    val res = Http(jobApiEndPoint.POST OK as.String).either
    for (resp <- res) resp match {
      case Right(respBody) => {
        val build = JenkinsConnector.parseLastBuild(job._1, respBody)
        if(!build.building) {
          if (!histo.contains(build.name) || histo(build.name) != build.timestamp) {
            histo += (build.name -> build.timestamp)
            feedSource.push(JenkinsConnector.buildFeed(build))
          }
        }
//        if (!histo.contains(build.name)) {
//          histo += (build.name -> (build.timestamp, build.result))
//        } else if (!build.building && (histo(build.name)._1 != build.timestamp || histo(build.name)._2 != build.result)) {
//          histo += (build.name -> (build.timestamp, build.result))
//          feedSource.push(JenkinsConnector.buildFeed(build))
//        }
        //        val duration = System.currentTimeMillis() - startDate
        //        JenkinsConnector.logger.trace(s"OK in ${duration} ms  (${JenkinsConnector.parseLastBuild(job._1, respBody)})")
      }
      case Left(e) => {
        val duration = System.currentTimeMillis() - startDate
        JenkinsConnector.logger.trace(s"KO in ${duration} ms  KO (${e.getMessage()})")
      }
    }
  }
}

class Build(val name: String, val result: String, val duration: Duration, val timestamp: Long, val url: String, val building: Boolean) {}
