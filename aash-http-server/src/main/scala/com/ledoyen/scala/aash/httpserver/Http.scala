package com.ledoyen.scala.aash.httpserver

import java.util.Date
import java.util.Locale

object Http {

  val HTTP_DATE_FORMAT = new java.text.SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH)

  def format(date: Date): String = HTTP_DATE_FORMAT.format(date)
}