package io.perfix.forms

import io.perfix.model.{FormInputType, StringType}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class FormSeriesTest extends AnyFlatSpec with Matchers {

  case class MockForm(label: String, shouldAskResult: Boolean) extends Form {
    override val mapping: Map[Form.FormInputName, FormInputType] = Map(label -> FormInputType(StringType))
    override val formParams: FormParams = new FormParams {}
    override def shouldAsk(): Boolean = shouldAskResult
    override def setAnswers(answers: Map[Form.FormInputName, Any]): Unit = ???
  }

  // Test class that extends FormSeries
  class TestFormSeries extends FormSeries {
    override val forms: Iterator[Form] = Iterator(
    MockForm("Q1", shouldAskResult = true),
    MockForm("Q2", shouldAskResult = true),
    MockForm("Q3", shouldAskResult = true)
    )
  }

  it should "iterate over the formSeries and return the next form that should be asked" in {

    val formSeries = new TestFormSeries
    val iter = formSeries.iterator

    iter.hasNext shouldEqual true
    iter.next().asInstanceOf[MockForm].label shouldEqual "Q1"

    iter.hasNext shouldEqual true
    iter.next().asInstanceOf[MockForm].label shouldEqual "Q2"

    iter.hasNext shouldEqual true
    iter.next().asInstanceOf[MockForm].label shouldEqual "Q3"

    iter.hasNext shouldEqual false
  }
}