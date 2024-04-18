package io.perfix.exceptions

trait FormExceptions extends Exception

case class ParamsAlreadyDefinedException(field: String) extends FormExceptions

case class InvalidFormParameterExceptions(field: String) extends FormExceptions