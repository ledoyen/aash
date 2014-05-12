package com.ledoyen.scala.aash.malbolge.core

import scala.io.Source
import org.junit.Assert._
import org.junit.Test
import org.hamcrest.CoreMatchers
import java.io.ByteArrayOutputStream
import java.io.InputStream

object NormalizerTest {
  val program = {
    // 99bottles
    val stream = getClass.getClassLoader.getResourceAsStream("helloWorld.mlb")
    val program = Source.fromInputStream(stream).mkString //.replaceAll("\\s", "")
    program
  }
}

class NormalizerTest {

  @Test
  def testNormalizeBijection = {
    val normalizedProgram = Normalizer.normalize(NormalizerTest.program)
    val unNormalizedProgram = Normalizer.unNormalize(normalizedProgram)
    println(unNormalizedProgram)
    println("")
    println(normalizedProgram)
    assertThat(unNormalizedProgram, CoreMatchers.equalTo(NormalizerTest.program))
    assertThat(Normalizer.normalize(unNormalizedProgram), CoreMatchers.equalTo(normalizedProgram))
  }

  @Test
  def testRunningNormalizedProgram = {
    val output = new ByteArrayOutputStream
    val vm = new VM(new InputStream {
      def read: Int = throw new IllegalStateException(s"Input used !")
    }, output)
    
    vm.execute(program = Normalizer.normalize(NormalizerTest.program), normalized = true, quiet = true)
    
    assertThat(new String(output.toByteArray), CoreMatchers.equalTo("Hello, world."))
  }
}