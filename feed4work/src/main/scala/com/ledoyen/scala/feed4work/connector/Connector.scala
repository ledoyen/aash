package com.ledoyen.scala.feed4work.connector

import com.ledoyen.scala.feed4work.FeedSource
import cronish.dsl.CronTask
import cronish.Cron
import cronish.dsl.Scheduled

abstract class Connector(val cronOption: Option[Cron] = None) {

  var scheduled: Option[Scheduled] = null
  var feedSource: Option[FeedSource] = None

  def connect(feedSource: FeedSource) = this.feedSource = Option(feedSource)

  def disconnect = this.feedSource = None

  def start: Connector = {
    scheduled = cronOption.map(new CronTask(task).executes(_))
    this
  }

  def stop = scheduled.foreach(_.stop)

  def task: Unit
}