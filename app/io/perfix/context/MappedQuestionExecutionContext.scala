package io.perfix.context

import io.perfix.model.{DataType, _}
import io.perfix.question.Question.QuestionLabel

class MappedQuestionExecutionContext(mappedValues: Map[String, Any]) extends QuestionExecutionContext {
  override def findAnswer(questionLabel: QuestionLabel, dataType: DataType): Any = {
    mappedValues(questionLabel)
  }
}
