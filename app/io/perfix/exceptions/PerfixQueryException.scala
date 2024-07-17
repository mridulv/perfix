package io.perfix.exceptions

case class PerfixQueryException(msg: String) extends Exception

case class WrongQueryException(msg: String) extends Exception
