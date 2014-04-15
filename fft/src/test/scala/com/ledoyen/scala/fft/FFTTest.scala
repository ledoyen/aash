package com.ledoyen.scala.fft

import org.scalatest.FunSuite

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class FFTTest extends FunSuite {

  test("FFT operation") {
    val N = 8
    val x = new Array[Complex](N)
    for (i <- 0 until N) {
      x(i) = Complex(i, 0);
      x(i) = Complex(-2 * Math.random() + 1, 0);
    }
    FFT.show(x, "x");
    
    FFT.show(FFT.fft(x), "y = fft(x)")
  }
}