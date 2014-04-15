package com.ledoyen.scala.fft

object Complex {
  def apply(re: Double, im: Double) = new Complex(re, im)
}

class Complex(val re: Double, val im: Double) {

  override def toString = (re, im) match {
    case (0, im) => im.toString
    case (re, 0) => re.toString
    case (re, im) if im < 0 => re + " - " + (-im) + "i"
    case _ => re + " + " + im + "i"
  }

  override def equals(o: Any): Boolean = o match {
    case c: Complex => c.re == re && c.im == im
    case _ => false
  }

  def abs = Math.hypot(re, im)
  def phase = Math.atan2(im, re)
  def conjugate = Complex(re, -im)
  def reciprocal = {
    val scale = re*re + im*im
    Complex(re / scale, -im / scale)
  }
  def exp = Complex(Math.exp(re) * Math.cos(im), Math.exp(re) * Math.sin(im))
  def sin = Complex(Math.sin(re) * Math.cosh(im), Math.cos(re) * Math.sinh(im))
  def cos = Complex(Math.cos(re) * Math.cosh(im), -Math.sin(re) * Math.sinh(im))
  def tan = sin/cos
  
  def +(b: Complex) = Complex(re + b.re, im + b.im)
  def -(b: Complex) = Complex(re - b.re, im - b.im)
  def *(b: Complex) = Complex(re * b.re - im * b.im, re * b.im + im * b.re)
  def /(b: Complex) = this * (b.reciprocal)
  // scalar multiplication
  def *(alpha: Double) = Complex(alpha * re, alpha * im)
}
