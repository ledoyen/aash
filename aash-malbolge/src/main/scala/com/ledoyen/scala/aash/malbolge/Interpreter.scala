package com.ledoyen.scala.aash.malbolge

import scala.util.control.Breaks._
import com.ledoyen.scala.aash.malbolge.core.Crazy
import com.ledoyen.scala.aash.malbolge.core.Operation
import com.ledoyen.scala.aash.malbolge.core.Encrypt
import com.ledoyen.scala.aash.malbolge.core.EndOperation
import com.ledoyen.scala.aash.malbolge.core.NopOperation

/**
 * @see https://github.com/b441berith/MalbolgeInterpreter
 */
object Interpreter {

  def main(args: Array[String]): Unit = {
    val i = new VM

    i.execute("(=<`:9876Z4321UT.-Q+*)M'&%$H\"!~}|Bzy?=|{z]KwZY44Eq0/{mlk** hKs_dG5[m_BA{?-Y;;Vb'rR5431M}/.zHGwEDCBA@98\\6543W10/.R,+O<")
  }
}

class VM {

  val MEMORY_SIZE = 59049

  val in = System.in
  val out = System.out

  val a = new Register("a")
  val c = new Register("c")
  val d = new Register("d")

  val memory = Array.fill(MEMORY_SIZE) { 0 } //Array[Int](59049)

  def state = s"State [$a $c $d]"

  def execute(program: String) = {
    // init memory
    init(program)
    displayState

    breakable {
      while (true) {
        if(processOneInstruction) break
//        displayState
      }
    }
  }

  def processOneInstruction = {
    val toTearInstruction = memory(c.innerValue) + c.innerValue
    val instructionCode = toTearInstruction % 94
    val op = Operation.parse(instructionCode)
    if(op != NopOperation) println(s"$op $instructionCode")
    	
    if(op == EndOperation) true else {
	    op.apply(this)
	
	    // Encrypt used instruction
	    memory(c.innerValue) = Encrypt.encrypt(memory(c.innerValue) % 94)
	
	    // TODO end of program if op match EndOperation
	    false
    }
  }

  def displayState = println(state)
  def displayMemory = println(memory.toList)

  def init(program: String) = {
    val programAsIntArray = program.toCharArray.map(_.toInt)
    Array.copy(programAsIntArray, 0, memory, 0, program.toCharArray.length)

    for (memoryAdress <- programAsIntArray.length to (MEMORY_SIZE - 1)) {
      memory(memoryAdress) = Crazy(memory(memoryAdress - 2), memory(memoryAdress - 1))
    }
  }
}

class Register(val name: String) {
  var innerValue = 0

  override def toString = s"$name=$innerValue"
}