package com.ledoyen.scala.feed4work.connector

import com.ledoyen.scala.feed4work.FeedSource
import cronish.dsl.CronTask
import cronish.Cron

abstract class Connector(val cron: Cron) {

  var feedSource: FeedSource = null

  def connect(feedSource: FeedSource) = this.feedSource = feedSource
  
   def start = new CronTask(task).executes(cron)

   def task: Unit
}