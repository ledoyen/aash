package com.ledoyen.scala.aash.malbolge.core

import com.ledoyen.scala.aash.malbolge.VM

object Operation {
  def parse(value: Int, strict: Boolean = false) = value match {
    case 4 => JumpOperation
    case 5 => OutOperation
    case 23 => InOperation
    case 39 => RotateOperation
    case 40 => CopyOperation
    case 62 => CrazyOperation
    case 68 => NopOperation
    case 81 => EndOperation
    case _ => if(!strict) NopOperation else throw new IllegalArgumentException(s"value ${value.toChar} ($value) is not mapped to any Malbolge operation")
  }
}

abstract class Operation {
  def apply(vm: VM)
}

// 4
case object JumpOperation extends Operation {
  def apply(vm: VM) = vm.c.innerValue = vm.memory(vm.d.innerValue) + 1
}

// 5
case object OutOperation extends Operation {
  def apply(vm: VM) = {
    vm.out.write(vm.a.innerValue)
  }
}

// 23
case object InOperation extends Operation {
  def apply(vm: VM) = ???
}

// 39
case object RotateOperation extends Operation {
  def apply(vm: VM) = ???
}

// 40
case object CopyOperation extends Operation {
  def apply(vm: VM) = {
    // Copies the value at [d] to d
    vm.d.innerValue = vm.memory(vm.d.innerValue)
  }
}

// 62
case object CrazyOperation extends Operation {
  def apply(vm: VM) =  {
    val memoryAtD = vm.memory(vm.d.innerValue)
    val valueInA = vm.a.innerValue
    
    val computedValue = Crazy(memoryAtD, valueInA)
    
    vm.memory(vm.d.innerValue) = computedValue
    vm.a.innerValue = computedValue
  }
}

// 68
case object NopOperation extends Operation {
  def apply(vm: VM) = {}
}

// 81
case object EndOperation extends Operation {
  def apply(vm: VM) = ???
}

object Crazy {

  def apply(t1: Int, t2: Int) = {
    Trinary.fromTrinary(crazyOp(Trinary.toTrinary(t1), Trinary.toTrinary(t2)))
  }

  def crazyOp(l1D: Long, l2A: Long) = {
    var remaining1 = l1D
    var remaining2 = l2A
    var result: Long = 0

    for (pos <- 9 to 0 by -1) {
      val pow = Math.pow(10, pos).toInt

      val digit1 = remaining1 / pow
      if (digit1 != 0) {
        remaining1 = remaining1 - digit1 * pow
      }
      val digit2 = remaining2 / pow
      if (digit2 != 0) {
        remaining2 = remaining2 - digit2 * pow
      }

      val crazyDigit =
        if (digit1 == 0) {
          if (digit2 == 0) 1 else 0
        } else if (digit1 == 1) {
          if (digit2 == 0) 1 else if(digit2 == 1) 0 else 2
        } else {
          if (digit2 == 2) 1 else 2
        }
      
      result = result + crazyDigit * Math.pow(10, pos).toInt
    }
    result
  }
}