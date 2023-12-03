package io.perfix.exceptions

trait QuestionExceptions extends Exception

case class ParamsAlreadyDefinedException(field: String) extends QuestionExceptions