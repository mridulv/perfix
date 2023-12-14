package io.perfix.question

import io.perfix._
import io.perfix.context.QuestionExecutionContext
import io.perfix.model.{BooleanType, DataType, DoubleType, IntType, NumericType, StringType}
import io.perfix.question.Question.QuestionLabel

trait Question {

  val storeQuestionParams: QuestionParams

  val questionExecutionContext: QuestionExecutionContext

  def shouldAsk(): Boolean

  def askQuestions(mapping: Map[QuestionLabel, DataType]): Map[QuestionLabel, Any] = {
    mapping.map { case (question, dataType) =>
      (question -> questionExecutionContext.findAnswer(question, dataType))
    }
  }

  def evaluateQuestion(): Unit

}

object Question {
  type QuestionLabel = String
}
