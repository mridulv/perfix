package io.perfix.exceptions

trait StoreException extends Exception

case class InvalidStateException(msg: String) extends StoreException
