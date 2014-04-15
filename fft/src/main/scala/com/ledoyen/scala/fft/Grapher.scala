package com.ledoyen.scala.fft

import javax.swing.JFrame
import java.io.File
import java.awt.FlowLayout
import javax.swing.BoxLayout
import javax.swing.JButton
import java.awt.event.ActionListener
import java.awt.event.ActionEvent
import javax.swing.JLabel

// TODO generate proper signal
// @see http://arachnoid.com/JSigGen/
// @see https://code.google.com/p/audiotools/
object Grapher {

  def main(args: Array[String]) = {
    val frame = new JFrame("FFT analysis")
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
    frame.getContentPane.setLayout(new BoxLayout(frame.getContentPane, BoxLayout.PAGE_AXIS))

    val fileName = "1-welcome.wav"
    // spacemusic.au
    frame.getContentPane.add(new JLabel("File : " + fileName))

    val soundFile = new File(getClass.getResource(s"/$fileName").getFile)
    val soundValues = Sounds.analyze(soundFile)

    frame.getContentPane.add(new JLabel("Frequency : " + soundValues._1 + " Hz"))

    val soundChart = Chart.newListChart("Sound", soundValues._2, soundValues._1, "Sec.")
    frame.getContentPane.add(soundChart)

    val fftChart = Chart.newChart("FFT", Sounds.fft(soundValues._2), 1)
    frame.getContentPane.add(fftChart)

    val b1 = new JButton("Play")
    b1.addActionListener(new ActionListener {
      def actionPerformed(e: ActionEvent) = Sounds.play(soundFile)
    })
    frame.getContentPane.add(b1)

    frame.pack
    frame.setVisible(true)
  }
}