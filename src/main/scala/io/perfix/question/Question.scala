package io.perfix.question

import io.perfix._
import io.perfix.model.{BooleanType, DataType, DoubleType, IntType, NumericType, StringType}

trait Question {

  type QuestionLabel = String

  val storeQuestionParams: QuestionParams

  def shouldAsk(): Boolean

  def askQuestions(mapping: Map[QuestionLabel, DataType]): Map[QuestionLabel, Any] = {
    mapping.map { case (question, dataType) =>
      println("Please answer: " + question)
      val answer = dataType match {
        case StringType => scala.io.StdIn.readLine()
        case BooleanType => scala.io.StdIn.readBoolean()
        case IntType => scala.io.StdIn.readLong()
        case DoubleType => scala.io.StdIn.readDouble()
      }
      (question -> answer)
    }
  }

  def evaluateQuestion(): Unit

}
