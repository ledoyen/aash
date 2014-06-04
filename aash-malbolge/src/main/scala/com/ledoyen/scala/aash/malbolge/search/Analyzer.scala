package com.ledoyen.scala.aash.malbolge.search

import scala.collection.parallel.immutable.ParSeq

object Analyzer {

  val RESULT_TO_MATCH = "I am the king of hell."

  def analyze(interpretations: ParSeq[(String, String, Long, String)], beamWidth: Int) = {
    interpretations.map(tu => (countPoints(tu), tu)).toList.sortWith(_._1 < _._1).take(beamWidth)
  }

  def countPoints(interpretation: (String, String, Long, String)): Long = {
    val commonPrefix = RESULT_TO_MATCH.zip(interpretation._2).takeWhile(Function.tupled(_ == _)).map(_._1).mkString
    errorCount(interpretation._4) * interpretation._1.length * 2 + interpretation._3 - (interpretation._2.size / 2) - (commonPrefix.length * 50 * interpretation._1.length)
  }

  def errorCount(m: String) = if(m.startsWith("[ERROR]")) 1 else 0
}