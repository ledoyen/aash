package com.ledoyen.scala.feed4work.duration

import scala.concurrent.duration.Duration

object Durations {

  def format(duration: Duration): String = {
    val days = duration.toDays
    val hours = duration.toHours - duration.toDays * 24
    val minutes = duration.toMinutes - duration.toHours * 60
    val seconds = duration.toSeconds - duration.toMinutes * 60
    val milis = duration.toMillis - duration.toSeconds * 1000
    
    val result = new StringBuilder
    if(days > 0) result ++= s"${days}d "
    if(hours > 0) result ++= s"${hours}h "
    if(minutes > 0) result ++= s"${minutes}m "
    if(seconds > 0) result ++= s"${seconds}s "
    if(milis > 0) result ++= s"${milis}ms"

    result.toString
  }
}