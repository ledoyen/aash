package com.ledoyen.scala.aash.malbolge.core

import scala.annotation.tailrec

object Normalizer {

  def normalize(program: String): String = normalizeRec(program.toList, Nil, 0).mkString

  // TailRec is needed for long program that may result in StackOverflow otherwise
  // It could also be done in a loop/mutable way instead of the recursive/immutable one here 
  @tailrec
  def normalizeRec(program: List[Char], acc: List[Char], index: Int): List[Char] = program match {
    case head :: tail => {
      // Blank characters are rewritten as it is
      if (List(10, 13, 32).contains(program.head.toInt)) {
        normalizeRec(program.tail, acc :+ program.head, index)
      } else {
        val decryptedInstruction = (program.head.toInt + index) % 94
        val op = try {
          Operation.parse((program.head.toInt + index) % 94, true)
        } catch {
          case e: Exception => throw new IllegalArgumentException(s"Decrypted value ${decryptedInstruction.toChar} ($decryptedInstruction), initially ${program.head} (${program.head.toInt}) at pos $index is not mapped to any Malbolge operation")
        }
        normalizeRec(program.tail, acc :+ op.toNormalizedCode, index + 1)
      }
    }
    case Nil => acc
  }

  def unNormalize(program: String): String = unNormalizeRec(program.toList, Nil, 0).mkString
  
  @tailrec
  def unNormalizeRec(program: List[Char], acc: List[Char], index: Int): List[Char] = program match {
    case head :: tail => {
      // Blank characters are rewritten as it is
      if (List(10, 13, 32).contains(program.head.toInt)) {
        unNormalizeRec(program.tail, acc :+ program.head, index)
      } else {
        val op = try {
          Operation.parseNormalized(head)
        } catch {
          case e: Exception => throw new IllegalArgumentException(s"Normalized value ${head} at pos $index is not mapped to any Malbolge operation")
        }
        unNormalizeRec(program.tail, acc :+ op.toBaseValue(index).toChar, index + 1)
      }
    }
    case Nil => acc
  }
}