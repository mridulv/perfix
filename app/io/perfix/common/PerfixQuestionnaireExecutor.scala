package io.perfix.common

import io.perfix.model.DataType
import io.perfix.question.Question.QuestionLabel
import io.perfix.question.{Question, Questionnaire}

class PerfixQuestionnaireExecutor(questionnaire: Questionnaire) extends Iterator[Map[QuestionLabel, DataType]] {

  private var currentOpt: Option[Question] = None
  private val allQuestionsIterator: Iterator[Question] = questionnaire.getQuestions

  override def hasNext: Boolean = {
    questionnaire.getQuestions.hasNext
  }

  override def next(): Map[QuestionLabel, DataType] = {
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
