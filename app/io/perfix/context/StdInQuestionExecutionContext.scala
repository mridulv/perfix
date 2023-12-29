package io.perfix.context

import io.perfix.model.{BooleanType, DataType, DoubleType, IntType, StringType}
import io.perfix.question.Question.QuestionLabel

class StdInQuestionExecutionContext extends QuestionExecutionContext {
  override def findAnswer(questionLabel: QuestionLabel, dataType: DataType): Any = {
    println("Please answer: " + questionLabel)
    dataType match {
      case StringType => scala.io.StdIn.readLine()
      case BooleanType => scala.io.StdIn.readBoolean()
      case IntType => scala.io.StdIn.readLong()
      case DoubleType => scala.io.StdIn.readDouble()
    }
  }
}
