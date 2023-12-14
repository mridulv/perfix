package io.perfix.context

import io.perfix.model.DataType
import io.perfix.question.Question.QuestionLabel

trait QuestionExecutionContext {

  def findAnswer(questionLabel: QuestionLabel,
                 dataType: DataType): Any

}