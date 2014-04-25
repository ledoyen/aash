package com.ledoyen.scala.aash.malbolge.core

object Trinary {

  def main(args: Array[String]): Unit = {
    toTrinary(2)
    toTrinary(42)
  }

  def toTrinary(number: Int): Long = {
    assert(number <= 59049, { s"given number [$number] is > 59049, this is not possible in Malbolge" })
    if (number == 59049) 0
    else {
      var remaining = number
      var result: Long = 0
      for (pos <- 9 to 0 by -1) {
        val pow = Math.pow(3, pos).toInt

        val digit = remaining / pow
        if (digit != 0) {
          remaining = remaining - digit * pow
          result = result + digit * Math.pow(10, pos).toInt
        }
      }
      result
    }
  }

  def fromTrinary(tri: Long): Int = {
    var remaining = tri
    var result: Int = 0
    for (pos <- 9 to 0 by -1) {
      val pow = Math.pow(10, pos).toInt

      val digit = (remaining / pow).toInt
      if (digit != 0) {
        remaining = remaining - digit * pow
        result = result + digit * Math.pow(3, pos).toInt
      }
    }
    result
  }
}