package com.ledoyen.scala.fft

import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class ComplexTest extends FunSuite with ShouldMatchers {

  test("Standard operations") {
    val a = Complex(5, 6)
    val b = Complex(-3, 4)

    b + a should be (Complex(2, 10))
    a - b should be (Complex(8, 2))
    b * a should be (a * b)
    a * b should be (Complex(-39, 2))
    (a / b) * b should be (Complex(5, 6))
    a.conjugate should be (Complex(5, -6))
    a.abs should be (7.810249675906654)
  }
}