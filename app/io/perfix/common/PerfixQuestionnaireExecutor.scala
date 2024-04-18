package io.perfix.common

import io.perfix.model.QuestionType
import io.perfix.question.Form.QuestionLabel
import io.perfix.question.{Form, Questionnaire}

class PerfixQuestionnaireExecutor(questionnaire: Questionnaire)
  extends Iterator[Map[QuestionLabel, QuestionType]] {

  private var currentOpt: Option[Form] = None
  private val allQuestionsIterator: Iterator[Form] = questionnaire.iterator

  override def hasNext: Boolean = {
    allQuestionsIterator.hasNext
  }

  override def next(): Map[QuestionLabel, QuestionType] = {
    val ques = allQuestionsIterator.next()
    currentOpt = Some(ques)
    ques.mapping
  }

  def submit(answers: Map[QuestionLabel, Any]): Unit = {
    currentOpt match {
      case Some(current) => current.setAnswers(answers)
      case None => throw new UnsupportedOperationException("Submit can be called only on a defined question")
    }
  }
}
