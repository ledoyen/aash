package com.ledoyen.scala.aash.malbolge

import com.ledoyen.scala.aash.malbolge.core.Operation
import scala.annotation.tailrec
import java.io.File
import java.io.BufferedWriter
import java.io.FileWriter
import com.ledoyen.scala.aash.malbolge.core.Normalizer

object NormalizedProgramGenerator {

  val MAX_GEN = 4

  def main(args: Array[String]): Unit = {
    val startTime = System.currentTimeMillis

    val progs = genrec(Operation.operations.map(op => List(op.toNormalizedCode)), MAX_GEN, List()).par.map(lst => lst.mkString) // .map(p => Normalizer.unNormalize(p))
    
//    val progs = normalizedProgs.map(p => Normalizer.unNormalize(p))
    
    writeToFile("c:\\malbolge_progs.txt", progs.mkString("\n"))
    
    val duration = (System.currentTimeMillis - startTime) / 1000
    println(s"\n\n ==========> END generating ${progs.length} programs in $duration sec")
  }

  @tailrec
  def genrec(progs: List[List[Char]], remainingLength: Int, acc: List[List[Char]]): List[List[Char]] = remainingLength match {
    case 0 => acc
    case _ => {
      val newProgs = for {
        prog <- progs
        newProg <- addOperations(prog)
      } yield newProg
      genrec(newProgs, remainingLength - 1, acc ++ progs)
    }
  }

  @tailrec
  def genreclast(progs: List[List[Char]], remainingLength: Int): List[List[Char]] = remainingLength match {
    case 0 => progs
    case _ => {
      val newProgs = for {
        prog <- progs
        newProg <- addOperations(prog)
      } yield newProg
      genreclast(newProgs, remainingLength - 1)
    }
  }

  def addOperations(prog: List[Char]): List[List[Char]] = {
    Operation.operations.map(op => prog :+ op.toNormalizedCode)
  }

  def writeToFile(fileName: String, content: String) = {
    val file = new File(fileName)
    val writer = new BufferedWriter(new FileWriter(file))
    try {
      writer.write(content)
    } finally {
      writer.close
    }
  }
}