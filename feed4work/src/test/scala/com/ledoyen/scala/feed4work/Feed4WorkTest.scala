package com.ledoyen.scala.feed4work

import org.junit.Assert._
import org.junit.Test
import cronish._
import dsl._
import com.ledoyen.scala.feed4work.connector._
import dispatch.Http
import java.util.Date
import com.gargoylesoftware.htmlunit.WebClient
import com.gargoylesoftware.htmlunit.TextPage
import com.gargoylesoftware.htmlunit.xml.XmlPage
import javax.xml.xpath.XPathFactory
import javax.xml.xpath.XPathConstants
import org.w3c.dom.NodeList
import org.hamcrest.CoreMatchers
import org.w3c.dom.Node
import scala.xml.XML

class Feed4WorkTest {

  @Test
  def test = {
    val server = new Feed4Work(8040, new InMemoryFeedSource(new Channel ("test title", "test description", "www.testlink.com"))).start

    server.enableStatistics

    server.registerShutdownHook(Scheduled.shutdown)
    server.registerShutdownHook(Http.shutdown)
    server.registerShutdownHook(() => println("Exit Hook"))

    server.registerListener("/stat", server.statistics)
    
    val testConnector = new TestConnector
    server.registerConnector("test", testConnector)

    val webClient = new WebClient
    var atomPage: XmlPage = webClient.getPage("http://localhost:8040/atom")

    var xmlContent = XML.loadString(atomPage.getContent)
    assertThat((xmlContent \\ "feed" \ "title").text, CoreMatchers.equalTo("test title"))
    assertThat((xmlContent \\ "feed" \\ "entry").size, CoreMatchers.equalTo(0))
    
    // test feed is empty and check channel
    
    testConnector.push(new Feed("feed test title", "www.feedtestlink.com", new Date, "feed test description"))

    atomPage = webClient.getPage("http://localhost:8040/atom")
    xmlContent = XML.loadString(atomPage.getContent)
    assertThat((xmlContent \\ "feed" \ "title").text, CoreMatchers.equalTo("test title"))
    assertThat((xmlContent \\ "feed" \\ "entry").size, CoreMatchers.equalTo(1))

    // test feed is not empty

    server.stop
  }
}

object Feed4WorkTest {
  def main(args: Array[String]) {
    val sourceFolderPath = Option(System.getProperty("feed4work.source.folder"))
      .getOrElse(s"${System.getProperty("user.home")}${System.getProperty("file.separator")}.feed4work")
    registerConnectors(startServer(new FileFeedSource(FileFeedSource.resolveSourceFolder(sourceFolderPath))))
  }

  def startServer(feedSource: FeedSource) = {
    

    val server = new Feed4Work(8060, feedSource).start

    server.enableStatistics

    server.registerShutdownHook(Scheduled.shutdown)
    server.registerShutdownHook(Http.shutdown)
    server.registerShutdownHook(() => println("Exit Hook"))

    server.registerListener("/stat", server.statistics)

    server
  }

  def registerConnectors(server: Feed4Work) {
    server.registerConnector("Jenkins", new JenkinsConnector(
      adress = System.getProperty("feed4work.server.jenkins.url"),
      cron = "every 30 seconds".cron,
      login = System.getProperty("feed4work.server.jenkins.login"),
      password = System.getProperty("feed4work.server.jenkins.password")))

    server.registerConnector("Outlook", new MailConnector(
      tag = "OUTLOOK",
      host = "outlook.office365.com",
      link = "http://google.com",
      port = Option(995),
      protocol = "pop3s",
      cron = "every 10 minutes".cron,
      login = System.getProperty("feed4work.server.outlook.login"),
      password = System.getProperty("feed4work.server.outlook.password")))

    server.registerConnector("Gmail", new MailConnector(
      tag = "GMAIL",
      host = "imap.gmail.com",
      link = "http://google.com",
      protocol = "imaps",
      cron = "every 3 minutes".cron,
      login = System.getProperty("feed4work.server.gmail.login"),
      password = System.getProperty("feed4work.server.gmail.password")))
  }
}

class TestConnector extends Connector {
 
  def push(feed: Feed) = feedSource.foreach(_.push(feed))

  def task = ???
}