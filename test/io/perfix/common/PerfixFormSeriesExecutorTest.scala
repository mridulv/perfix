package io.perfix.common

import io.perfix.model.{IntType, FormInputType, StringType}
import io.perfix.forms.Form.FormInputName
import io.perfix.forms.{Form, FormParams, FormSeries}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class PerfixFormSeriesExecutorTest extends AnyFlatSpec with Matchers {
  def mockFormSeries: FormSeries = new FormSeries {
    override val forms: Iterator[Form] = Iterator(
      new Form {
        override val mapping: Map[FormInputName, FormInputType] = Map("Q1" -> FormInputType(StringType))
        override val formParams: FormParams = new FormParams {}
        override def shouldAsk(): Boolean = true
        override def setAnswers(answers: Map[FormInputName, Any]): Unit = {}
      },
      new Form {
        override val mapping: Map[FormInputName, FormInputType] = Map("Q2" -> FormInputType(IntType))
        override val formParams: FormParams = new FormParams {}
        override def shouldAsk(): Boolean = true
        override def setAnswers(answers: Map[FormInputName, Any]): Unit = {}
      }
    )
  }

  "A FormSeriesEvaluator" should  "return the next form"  in {
    val executor = new FormSeriesEvaluator(mockFormSeries)

    executor.hasNext
    val form1 = executor.next()
    form1 shouldBe Map("Q1" -> FormInputType(StringType))

    executor.hasNext
    val form2 = executor.next()
    form2 shouldBe Map("Q2" -> FormInputType(IntType))
  }

  "A FormSeriesEvaluator" should "allow submitting answers to the current form" in {
    val executor = new FormSeriesEvaluator(mockFormSeries)

    executor.hasNext
    executor.next()
    executor.submit(Map("Q1" -> "Answer1"))

    executor.hasNext
    executor.next()
    executor.submit(Map("Q2" -> 42))
  }

  "A FormSeriesEvaluator" should "throw an exception when submitting answers without a defined form" in {
    val executor = new FormSeriesEvaluator(mockFormSeries)
    
    an[UnsupportedOperationException] should be thrownBy {
      executor.submit(Map("Q1" -> "Answer1"))
    }
  }
}
