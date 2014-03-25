package com.ledoyen.scala.aash.httpserver

object ExtendedStringMap {
  def apply[V](map: Map[String, V]) = new ExtendedStringMap[V](map)

  implicit def fromMap[V](map: Map[String, V]) = ExtendedStringMap(map)
}

class ExtendedStringMap[V](map: Map[String, V]) {
  def getIC(value: String): Option[V] = map.filter((entry) => entry._1.equalsIgnoreCase(value)).headOption.map(_._2)
}
