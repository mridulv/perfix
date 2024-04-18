package io.perfix.common

import io.perfix.model.FormInputType
import io.perfix.question.Form.FormInputName
import io.perfix.question.{Form, FormSeries}

class FormSeriesEvaluator(questionnaire: FormSeries)
  extends Iterator[Map[FormInputName, FormInputType]] {

  private var currentOpt: Option[Form] = None
  private val allQuestionsIterator: Iterator[Form] = questionnaire.iterator

  override def hasNext: Boolean = {
    allQuestionsIterator.hasNext
  }

  override def next(): Map[FormInputName, FormInputType] = {
    val ques = allQuestionsIterator.next()
    currentOpt = Some(ques)
    ques.mapping
  }

  def submit(answers: Map[FormInputName, Any]): Unit = {
    currentOpt match {
      case Some(current) => current.setAnswers(answers)
      case None => throw new UnsupportedOperationException("Submit can be called only on a defined question")
    }
  }
}
