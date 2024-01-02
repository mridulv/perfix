package io.perfix.question

import io.perfix._
import Question.QuestionLabel
import io.perfix.context.QuestionExecutionContext
import io.perfix.model.{BooleanType, DataType, DoubleType, IntType, NumericType, StringType}

trait Question {

  val mapping: Map[QuestionLabel, DataType]

  val storeQuestionParams: QuestionParams

  val questionExecutionContext: QuestionExecutionContext

  def shouldAsk(): Boolean

  def askQuestions: Map[QuestionLabel, Any] = {
    mapping.flatMap { case (question, dataType) =>
      questionExecutionContext.findAnswer(question, dataType) match {
        case Some(v) => Some(question -> v)
        case None => None
      }
    }
  }

  def evaluateQuestion(): Unit

}

object Question {
  type QuestionLabel = String
}
