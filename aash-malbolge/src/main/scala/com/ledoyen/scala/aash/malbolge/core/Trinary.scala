package com.ledoyen.scala.aash.malbolge.core

object Trinary {

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

  def crazyOp(l1D: Long, l2A: Long) = {
    var remaining1 = l1D
    var remaining2 = l2A
    var result: Long = 0

    for (pos <- 9 to 0 by -1) {
      val pow = Math.pow(10, pos).toInt

      val digit1 = remaining1 / pow
      if (digit1 != 0) {
        remaining1 = remaining1 - digit1 * pow
      }
      val digit2 = remaining2 / pow
      if (digit2 != 0) {
        remaining2 = remaining2 - digit2 * pow
      }

      val crazyDigit =
        if (digit1 == 0) {
          if (digit2 == 0) 1 else 0
        } else if (digit1 == 1) {
          if (digit2 == 0) 1 else if(digit2 == 1) 0 else 2
        } else {
          if (digit2 == 2) 1 else 2
        }
      
      result = result + crazyDigit * Math.pow(10, pos).toInt
    }
    result
  }

  def rotate(tri: Long): Long = {
    val asStr = tri.toString
    val padded = Array.fill(10 - asStr.length)('0').mkString + asStr
    val rotated = padded.charAt(9).toString + padded.subSequence(0, 9)
    rotated.toLong
  }
}

/**
 * <table ><tbody>
 * <tr>
 * 	<th colspan="2" rowspan="2"></th>
 * 	<th colspan="3" style="text-align:center"> <b><code>A</code></b></th>
 * </tr>
 * <tr>
 * 	<th> <b><code>0</code></b></th>
 * 	<th> <b><code>1</code></b></th>
 * 	<th> <b><code>2</code></b></th>
 * </tr>
 * <tr>
 * 	<th rowspan="3"><b><code>[D]</code></b></th>
 * 	<th> <b><code>0</code></b></th>
 * 	<td> <code>1</code></td>
 * 	<td> <code>0</code></td>
 * 	<td> <code>0</code></td>
 * </tr>
 * <tr>
 * 	<th> <code>1</code></th>
 * 	<td> <code>1</code></td>
 * 	<td> <code>0</code></td>
 * 	<td> <code>2</code></td>
 * </tr>
 * <tr>
 * 	<th> <code>2</code></th>
 * 	<td> <code>2</code></td>
 * 	<td> <code>2</code></td>
 * 	<td> <code>1</code></td>
 * </tr>
 * </tbody></table>
 */
object Crazy {
  def apply(t1: Int, t2: Int) = {
    Trinary.fromTrinary(Trinary.crazyOp(Trinary.toTrinary(t1), Trinary.toTrinary(t2)))
  }
}

object Rotate {
  def apply(tri: Int) = {
    Trinary.fromTrinary(Trinary.rotate(Trinary.toTrinary(tri)))
  }
}