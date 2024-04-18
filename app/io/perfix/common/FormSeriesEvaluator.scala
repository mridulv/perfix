package io.perfix.common

import io.perfix.model.FormInputType
import io.perfix.forms.Form.FormInputName
import io.perfix.forms.{Form, FormSeries}

class FormSeriesEvaluator(formSeries: FormSeries)
  extends Iterator[Map[FormInputName, FormInputType]] {

  private var currentOpt: Option[Form] = None
  private val forms: Iterator[Form] = formSeries.iterator

  override def hasNext: Boolean = {
    forms.hasNext
  }

  override def next(): Map[FormInputName, FormInputType] = {
    val form = forms.next()
    currentOpt = Some(form)
    form.mapping
  }

  def submit(answers: Map[FormInputName, Any]): Unit = {
    currentOpt match {
      case Some(current) => current.setAnswers(answers)
      case None => throw new UnsupportedOperationException("Submit can be called only on a defined formInput")
    }
  }
}
