package com.ledoyen.scala.aash.malbolge.search

import scala.annotation.tailrec
import com.ledoyen.scala.aash.malbolge.core.Operation

object Generator {

  /**
   * Generate all possible (normalized) programs, based on given programs, adding the given length of operations.
   */
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

  /**
   * Generate all possible (normalized) programs of max length, based on given programs, adding the given length.
   */
  @tailrec
  def genreclast(programs: List[List[Char]], remainingLength: Int): List[List[Char]] = remainingLength match {
    case 0 => programs
    case _ => {
      val newProgs = for {
        prog <- programs
        newProg <- addOperations(prog)
      } yield newProg
      genreclast(newProgs, remainingLength - 1)
    }
  }

  /**
   * Generate all 8 possible programs, adding all Malbolge operations to the given program.
   */
  def addOperations(prog: List[Char]): List[List[Char]] = {
    Operation.operations.map(op => prog :+ op.toNormalizedCode)
  }
}