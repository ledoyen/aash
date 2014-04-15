package com.ledoyen.scala.fft

import javax.sound.sampled.AudioSystem
import java.io.File
import javax.sound.sampled.AudioFormat
import java.nio.ByteBuffer
import java.nio.ByteOrder
import sun.audio.AudioPlayer
import sun.audio.AudioStream
import java.io.FileInputStream

object Sounds {

  def analyze(file: File) = {
    val audioInputStream = AudioSystem.getAudioInputStream(file)
    val targetEncoding = AudioFormat.Encoding.PCM_SIGNED
    val pcmStream = AudioSystem.getAudioInputStream(targetEncoding, audioInputStream)
    val format = pcmStream.getFormat
    println(s"${format.getSampleRate} Hz")
    val b = new Array[Byte](102400)
    var result = List[Double]()
    while (pcmStream.read(b, 0, b.length) > 0) {
      val l = ByteBuffer.wrap(b).order(ByteOrder.LITTLE_ENDIAN).asFloatBuffer
      val audioFloats = new Array[Float](l.capacity)
      l.get(audioFloats)
      result = result ::: audioFloats.map(_.toDouble).toList
    }

    (format.getSampleRate, toSizePowerOf2(result.filter(d => !d.isNaN && !d.isInfinite)))
  }

  def fft(values: List[Double]) = {

    val complexes = values.map(Complex(_, 0)).toArray

    println(s"min=${complexes.map(_.re).min} / max=${complexes.map(_.re).max}")

    //    complexes.filter(_.re != 0).foreach(c => println(c))

    // Get rid of the meaningless imaginary part
    FFT.fft(complexes).map(_.re)
  }

  def power2Superior(nb: Int): Int = {
      def power2Superior(nb: Int, acc: Int): Int = if (acc > nb) acc else power2Superior(nb, acc * 2)
      power2Superior(nb, 1)
    }

  def toSizePowerOf2(ar: List[Double]): List[Double] = {
    val returnSize = power2Superior(ar.size)
//    println(s"array splitted at $returnSize / ${ar.size} (${(returnSize * 100 / ar.size)}%)");
    println(s"${ar.size} + ${returnSize - ar.size} tail values = $returnSize (${(returnSize - ar.size) * 100 / returnSize}% _ added)")
//    val tail = new List[Double]()
//    ar.splitAt(returnSize)._1
    ar ::: List.fill(returnSize - ar.size)(0d)
  }

  def play(file: File) = {
    AudioPlayer.player.start(new AudioStream(new FileInputStream(file)))
  }
}