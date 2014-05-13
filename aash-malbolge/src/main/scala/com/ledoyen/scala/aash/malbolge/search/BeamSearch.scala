package com.ledoyen.scala.aash.malbolge.search

import scala.annotation.tailrec
import com.ledoyen.scala.aash.malbolge.core.Operation
import com.ledoyen.scala.aash.tool.Files
import scala.collection.parallel.immutable.ParSeq
import scala.io.Source

object BeamSearch {

  val rootPath = "c:\\malbolge"

  def main(args: Array[String]): Unit = {
    Files.createFolderIfNotExists(rootPath)

    val initProgs = Operation.operations.map(op => List(op.toNormalizedCode))
    // Start with all 8 operations as base programs
    // Create all possible programs of length 5 --> 37448 programs
    searchIteration(0, 5, Some(initProgs), 100)

    for (i <- 1 to 10) {
      searchIteration(i, 3, None, 100)
    }
  }

  def searchIteration(iterationNumber: Int, length: Int, opInitProgs: Option[List[List[Char]]] = None, beamWidth: Int) = {
    val startTime = System.currentTimeMillis
    println("#######################################")
    println(s"############# ITERATION $iterationNumber #############")
    println("#######################################")
    println(s"\tlength=$length\tbeamWidth=$beamWidth")
    println("#######################################")

    val initProgs = opInitProgs.getOrElse(readEligiblePrograms(iterationNumber - 1))
    println(s"Number of initial programs : ${initProgs.size}")

    // Create all possible programs of length length (5 => 37448 programs)
    val progs = Generator.genrec(initProgs, length, List()).par
    println(s"Number of generated programs : ${progs.size}\t\tafter ${(System.currentTimeMillis - startTime) / 1000} sec")

    // Save programs in file
    storePrograms(iterationNumber, progs)

    // Interpret programs
    val interpretations = BatchInterpreter.interpret(progs)
    println(s"Interpretation done after ${(System.currentTimeMillis - startTime) / 1000} sec")

    // Save interpretations in file
    storeInterpretations(iterationNumber, interpretations)

    // Analyze interpretations
    val eligibleInterpretations = Analyzer.analyze(interpretations, beamWidth)
    println(s"Number of matching programs : ${eligibleInterpretations.size} with lowest (best) score : ${eligibleInterpretations(0)._1}\t\tafter ${(System.currentTimeMillis - startTime) / 1000} sec")

    // Save Eligible programs
    storeEligiblePrograms(iterationNumber, eligibleInterpretations)

    println()
    println()
  }

  def readEligiblePrograms(tag: Int) = {
    Source.fromFile(s"$rootPath\\$tag\\eligible_programs.txt").mkString.split("\n").map(_.toCharArray.toList).toList
  }

  def storePrograms(tag: Int, progs: ParSeq[List[Char]]) = {
    Files.createFolderIfNotExists(s"$rootPath\\$tag")
    Files.writeToFile(s"$rootPath\\$tag\\programs.txt", progs.map(lst => lst.mkString).mkString("\n"))
  }

  /**
   * Assume that #storePrograms have been called on this tag before and so folder already exists.
   */
  def storeInterpretations(tag: Int, interpretations: ParSeq[(String, String, Long, String)]) = {
    Files.writeToFile(s"$rootPath\\$tag\\results.txt", interpretations.map(tu => s"${tu._1}\t\t\t\t${tu._2}\t\t\t\t${tu._3}\t\t\t\t${tu._4}").mkString("\n"))
  }

  def storeEligiblePrograms(tag: Int, eligibleInterpretations: List[(Long, (String, String, Long, String))]) = {
    Files.writeToFile(s"$rootPath\\$tag\\eligible_programs.txt", eligibleInterpretations.map(tu => tu._2._1).mkString("\n"))
  }
}