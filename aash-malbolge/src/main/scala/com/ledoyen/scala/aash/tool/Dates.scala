package com.ledoyen.scala.aash.tool

import scala.collection.mutable.StringBuilder

object Dates {

  def smartParse(ms: Long): String = {
    val sb = new StringBuilder
    var remaining = ms

    val days = remaining / (1000 * 60 * 60 * 24)
    if (days > 0) {
      sb.append(s"$days d ")
      remaining = remaining - (days * (1000 * 60 * 60 * 24))
    }

    val hours = remaining / (1000 * 60 * 60)
    if (hours > 0) {
      sb.append(s"$hours h ")
      remaining = remaining - (hours * (1000 * 60 * 60))
    }

    val minutes = remaining / (1000 * 60)
    if (minutes > 0) {
      sb.append(s"$minutes m ")
      remaining = remaining - (minutes * (1000 * 60))
    }
    
    val seconds = remaining / (1000)
    if (seconds > 0) {
      sb.append(s"$seconds s ")
      remaining = remaining - (seconds * (1000))
    }

    if (remaining > 0) {
      sb.append(s"$remaining ms ")
    }

    sb.deleteCharAt(sb.length - 1)

    sb.toString
  }
}