package com.ledoyen.scala.feed4work.connector

import com.ledoyen.scala.feed4work.FeedSource
import com.ledoyen.scala.feed4work.Feed
import cronish.Cron

object MailConnector {
  import javax.mail.Message

  def getDate(message: Message) = Option(message.getReceivedDate()).getOrElse(message.getSentDate())

  def buildFeed(message: Message, tag: String, link: String): Feed = {
    val date = getDate(message)
    new Feed(s"[$tag] ${message.getSubject}", s"$link/${date.getTime}", date, s"${message.getContent}")
  }
}

class MailConnector(val host: String, val port: Option[Int] = None, val protocol: String, val cron: Cron,
    val login: String, val password: String, val tag: String = "MAIL", val link: String = "") extends Connector(Option(cron)) {

  var lastMail = ("", 0l)

  def task = {
    import javax.mail._
    import javax.mail.internet._
    import javax.mail.search._

    val props = System.getProperties
    props.setProperty("mail.store.protocol", protocol)
    val session = Session.getDefaultInstance(props, null)
    val store = session.getStore(protocol)
    port match {
      case Some(p) => store.connect(host, p, login, password)
      case None => store.connect(host, login, password)
    }
    val inbox = store.getFolder("Inbox")
    inbox.open(Folder.READ_ONLY)
    val limit = 20
    // Chronological order
    val messages = inbox.getMessages(inbox.getMessageCount() - limit, inbox.getMessageCount())
    for(message <- messages) {
      val date = MailConnector.getDate(message)
      if(lastMail._2 < date.getTime || (lastMail._2 == date.getTime && lastMail._1 != message.getSubject)) {
        feedSource.foreach(_.push(MailConnector.buildFeed(message, tag, link)))
        lastMail = (message.getSubject, date.getTime)
      }
    }
  }
}