package com.ledoyen.scala.fft

import org.jfree.data.time.TimeSeriesCollection
import org.jfree.data.time.TimeSeries
import org.jfree.data.time.Millisecond
import org.jfree.chart.ChartFactory
import org.jfree.chart.ChartPanel
import java.util.Date
import org.jfree.data.xy.XYSeries
import org.jfree.data.xy.XYSeriesCollection
import org.jfree.data.xy.XYDataset
import org.jfree.data.category.DefaultCategoryDataset
import org.jfree.chart.plot.PlotOrientation

object Chart {

  def newListChart(title: String, values: List[Double], frequency: Double, xLabel: String = "X"): ChartPanel = {
    newChart(title, values.toArray, frequency, xLabel)
  }

  def newChart(title: String, values: Array[Double], frequency: Double, xLa: String = "X") = {
    val series = new XYSeries("Data")

    var i = 0
    for(value <- values) {
      series.add(i / frequency, value)
      i = i + 1
    }

    val dataset = new XYSeriesCollection(series)

    val chart = ChartFactory.createXYLineChart(
        title,
        xLa,
        "Y",
        dataset,
        PlotOrientation.VERTICAL,
        false,
        false,
        false)
    val plot = chart.getXYPlot
    val axis = plot.getDomainAxis
    axis.setAutoRange(true)
//    axis.setFixedAutoRange(60000.0)
    
    new ChartPanel(chart)
  }
}