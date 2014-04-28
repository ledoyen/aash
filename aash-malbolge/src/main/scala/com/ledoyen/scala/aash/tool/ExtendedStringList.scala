package com.ledoyen.scala.aash.tool

object ExtendedStringList {
  
  implicit def fromList(list: List[String]) = new ExtendedStringList(list)
}

class ExtendedStringList(list: List[String]) {
	def containsIC(value: String): Boolean = list.filter(_.equalsIgnoreCase(value)).size > 0
}