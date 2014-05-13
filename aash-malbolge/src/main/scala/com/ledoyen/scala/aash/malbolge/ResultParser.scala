package com.ledoyen.scala.aash.malbolge

import scala.io.Source

object ResultParser {
  def main(args: Array[String]): Unit = {
    val startTime = System.currentTimeMillis

    val lines = Source.fromFile("c:\\malbolge_progs_results.txt").mkString.split("\n")

    val matchingResults = for {
      line <- lines
      output = parseOutput(line)
      points = countPoints(output._2)
      if points > 0
    } yield (points, output)

    val orderedResults = matchingResults.sortWith(_._1 < _._1)
    
    NormalizedProgramGenerator.writeToFile("c:\\malbolge_matching_results.txt", orderedResults.map(tu => tu._2._1).mkString("\n"))
    
    println(matchingResults.mkString("\n"))

    val duration = (System.currentTimeMillis - startTime) / 1000
    println(s"\n\n ==========> END processing ${lines.size} (${matchingResults.size}) lines in $duration sec")
  }

  def parseOutput(s: String): (String, String) = {
    val parsed = s.split("\t\t")
    val output = parsed(2)
    (parsed(0), output)
  }

  def countPoints(s: String): Int = {
    var points = 0
    if(s.startsWith("I")) points = 1
    else if(s.startsWith("I ")) points = 2
    else if(s.startsWith("I a")) points = 3
    points
  }
}