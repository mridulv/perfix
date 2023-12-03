package io.perfix.stores.question

import io.perfix._

trait Question {

  type QuestionLabel = String

  val storeQuestionParams: StoreQuestionParams

  def shouldAsk(): Boolean

  def askQuestions(mapping: Map[QuestionLabel, DataType]): Map[QuestionLabel, Any] = {
    mapping.map { case (question, dataType) =>
      println("Please answer: " + question)
      val answer = dataType match {
        case StringType => scala.io.StdIn.readLine()
        case BooleanType => scala.io.StdIn.readBoolean()
        case NumericType => scala.io.StdIn.readLong()
        case DoubleType => scala.io.StdIn.readDouble()
      }
      (question -> answer)
    }
  }

  def evaluateQuestions(): Unit

}
