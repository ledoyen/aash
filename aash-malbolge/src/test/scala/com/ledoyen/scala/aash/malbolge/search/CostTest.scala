package com.ledoyen.scala.aash.malbolge.search

import org.junit.Test

object CostTest {

  def main(args: Array[String]) {
    import com.ledoyen.scala.aash.malbolge.search.BeamSearch2._

    val normalized = "jpp<ppppp<pppp<<pp<ppp<pppp<ppppp<pp<ioooj/ojji</oiivoooi<ojvpoj/pvojj<j/o*jov/<ojjj*o/jj/oo/oooooopp<pppp<pppp<pp<v"

    for (
      i <- 1 to normalized.length();
      prog = normalized.take(i)
    ) yield println((prog, cost_fn(prog), resolve(prog)._2))
  }
}