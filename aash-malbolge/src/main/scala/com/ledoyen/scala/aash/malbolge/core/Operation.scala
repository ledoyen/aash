package com.ledoyen.scala.aash.malbolge.core

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

    case _ => if (!strict) NopOperation else throw new IllegalArgumentException(s"Value ${value.toChar} ($value) is not mapped to any Malbolge operation")
  }

  def parseNormalized(value: Char) = value match {
    case 'i' => JumpOperation
    case '<' => OutOperation
    case '/' => InOperation
    case '*' => RotateOperation
    case 'j' => CopyOperation
    case 'p' => CrazyOperation
    case 'o' => NopOperation
    case 'v' => EndOperation

    case _ => throw new IllegalArgumentException(s"Normalized value ${value} is not mapped to any Malbolge operation")
  }
}

abstract class Operation {
  def apply(vm: VM)
  def toNormalizedCode: Char
  def value: Int
  def toBaseValue(pos: Int): Int = {
    var potentialValue = value - pos
    if(potentialValue > 126) potentialValue % 94 else {
      while(potentialValue < 33) {
        potentialValue = potentialValue + 94
      }
      potentialValue
    }
  }
}

// 4
// Set code pointer to the value pointed to by the current data pointer.
// C = [D]
// i
case object JumpOperation extends Operation {
  def apply(vm: VM) = vm.c.innerValue = vm.memory(vm.d.innerValue)
  def toNormalizedCode = 'i'
  def value = 4
}

// 5
// Output the character in A, modulo 256, to standard output.
// PRINT(A%256)
// <
case object OutOperation extends Operation {
  def apply(vm: VM) = {
    vm.out.write(vm.a.innerValue % 256)
  }
  def toNormalizedCode = '<'
  def value = 5
}

// 23
// Input a character from standard input and store it in A.
// A = INPUT
// /
case object InOperation extends Operation {
  def apply(vm: VM) = {
    // TODO handle cases of line feeds and EOF
    vm.a.innerValue = vm.in.read
  }
  def toNormalizedCode = '/'
  def value = 23
}

// 39
// Tritwise rotate right.
// A = [D] = ROTATE_RIGHT([D])
// *
case object RotateOperation extends Operation {
  def apply(vm: VM) = {
    val memoryAtD = vm.memory(vm.d.innerValue)

    val rotated = Rotate(memoryAtD)

    vm.memory(vm.d.innerValue) = rotated
    vm.a.innerValue = rotated
  }
  def toNormalizedCode = '*'
  def value = 39
}

// 40
// Set data pointer to the value pointed to by the current data pointer
// D = [D]
// j
case object CopyOperation extends Operation {
  def apply(vm: VM) = {
    // Copies the value at [d] to d
    vm.d.innerValue = vm.memory(vm.d.innerValue)
  }
  def toNormalizedCode = 'j'
  def value = 40
}

// 62
// Tritwise "crazy" operation.
// A = [D] = CRAZY_OP(A, [D])
// p
case object CrazyOperation extends Operation {
  def apply(vm: VM) = {
    val memoryAtD = vm.memory(vm.d.innerValue)
    val valueInA = vm.a.innerValue

    val computedValue = Crazy(memoryAtD, valueInA)

    vm.memory(vm.d.innerValue) = computedValue
    vm.a.innerValue = computedValue
  }
  def toNormalizedCode = 'p'
  def value = 62
}

// 68
// No operation.
// NOP
// o
case object NopOperation extends Operation {
  def apply(vm: VM) = {}
  def toNormalizedCode = 'o'
  def value = 68
}

// 81
// Stop execution of the current program.
// STOP
// v
case object EndOperation extends Operation {
  def apply(vm: VM) = ???
  def toNormalizedCode = 'v'
  def value = 81
}
