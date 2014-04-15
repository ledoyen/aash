package com.ledoyen.scala.fft

import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import scala.io.Source
import java.io.File

@RunWith(classOf[JUnitRunner])
class SoundsTest extends FunSuite with ShouldMatchers {

  test("parse standard MP3") {
   val values = Sounds.analyze(new File(getClass.getResource("/1-welcome.wav").getFile))
   val fft = Sounds.fft(values._2)
   fft.foreach(c => println(c))
  }

  test("power of 2 inferior at") {
    Sounds.power2Superior(7) should be (8)
    
    Sounds.power2Superior(12) should be (16)
    
    Sounds.power2Superior(17) should be (32)
  }
}