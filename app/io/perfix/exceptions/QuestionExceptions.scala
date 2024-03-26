package io.perfix.exceptions

trait QuestionExceptions extends Exception

case class ParamsAlreadyDefinedException(field: String) extends QuestionExceptions

case class InvalidQuestionParameterExceptions(field: String) extends QuestionExceptions