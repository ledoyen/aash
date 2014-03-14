package com.ledoyen.scala.feed4work.json

class JsonType[T] { def unapply(a:Any):Option[T] = Some(a.asInstanceOf[T]) }

object M extends JsonType[Map[String, Any]]
object L extends JsonType[List[Any]]
object S extends JsonType[String]
object D extends JsonType[Double]
object B extends JsonType[Boolean]