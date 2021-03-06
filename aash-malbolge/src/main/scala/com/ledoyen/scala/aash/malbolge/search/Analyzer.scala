package com.ledoyen.scala.aash.malbolge.search

import scala.collection.parallel.immutable.ParSeq

object Analyzer {

  val RESULT_TO_MATCH = "I am the king of hell."

  def analyze(interpretations: ParSeq[(String, String, Long, String, Int)], beamWidth: Int) = {
    interpretations.map(tu => (countPoints(tu), tu)).toList.sortWith(_._1 < _._1).take(beamWidth)
  }

  def countPoints(interpretation: (String, String, Long, String, Int)): Long = {
    val commonPrefix = RESULT_TO_MATCH.zip(interpretation._2).takeWhile(Function.tupled(_ == _)).map(_._1).mkString
    interpretation._5 - (commonPrefix.length * 10)
  }

  def errorCount(m: String) = if(m.startsWith("[ERROR]")) 1 else 0
}