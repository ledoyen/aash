package com.ledoyen.scala.fft

object FFT {

  def fft(x: Array[Complex]): Array[Complex] = {
    val N = x.length
    
    if(N == 1) return Array(x(0))
    if (N % 2 != 0) throw new RuntimeException("N is not a power of 2")
    
    val even = new Array[Complex](N/2)
    for (k <- 0 until  N/2) {
        even(k) = x(2*k)
    }
    val q = fft(even)

    // fft of odd terms
    val odd  = even;  // reuse the array
    for (k <- 0 until  N/2) {
        odd(k) = x(2*k + 1)
    }
    val r = fft(odd)

    // combine
    val y = new Array[Complex](N)
    for (k <- 0 until  N/2) {
        val kth = -2 * k * Math.PI / N;
        val wk = Complex(Math.cos(kth), Math.sin(kth));
        y(k)       = q(k).+(wk.*(r(k)))
        y(k + N/2) = q(k).-(wk.*(r(k)));
    }
    y
  }

  def show(x: Array[Complex], title: String) = {
    println(title)
    println("-------------------")
    for (i <- 0 until x.length) {
      println(x(i))
    }
    println()
  }
}