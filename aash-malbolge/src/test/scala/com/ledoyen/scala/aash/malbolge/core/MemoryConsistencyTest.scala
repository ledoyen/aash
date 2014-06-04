package com.ledoyen.scala.aash.malbolge.core

import org.junit.Test
import scala.io.Source

object MemoryConsistencyTest {
  val program = {
    // 99bottles
    // helloWorld
    val stream = getClass.getClassLoader.getResourceAsStream("99bottles.mlb")
    val program = Source.fromInputStream(stream).mkString //.replaceAll("\\s", "")
    program
  }
}

/**
 * Test consistency between Eager and Lazy memories
 */
class MemoryConsistencyTest {

  @Test
  def testConsitency = {
    import scala.util.control.Breaks._

    val eagerMemVm = new VM(memory=new EagerMemory)
    val lazyMemVm = new VM(memory=new LazyMemory)
    
    eagerMemVm.init(MemoryConsistencyTest.program, false)
    lazyMemVm.init(MemoryConsistencyTest.program, false)
    
    breakable {
      while (true) {
        val end = eagerMemVm.processOneInstruction
        eagerMemVm.instructionCount.incrementAndGet
        lazyMemVm.processOneInstruction
        lazyMemVm.instructionCount.incrementAndGet
        
        val usedMemory = lazyMemVm.memory.usedMemory
        val eagerMem = eagerMemVm.memory.take(usedMemory)
        val lazyMem = lazyMemVm.memory.take(usedMemory)
        
        val es = view(eagerMem)
        val ls = view(lazyMem)
        
        if(eagerMem.deep != lazyMem.deep) {
          throw new IllegalStateException(s"memories are inconsistent at ${eagerMemVm.instructionCount.longValue} instructions")
        }
        
        if(end) break
      }
    }
  }

  def view(l: Array[Int]) = {
    val indexed = (0 to l.length).toList.zip(l).take(l.length + 2)
    indexed.take(30).mkString("\n") + "\n...\n\n" + indexed.takeRight(30).mkString("\n")
  }
}