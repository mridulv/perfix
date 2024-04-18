package io.perfix.common

import io.perfix.model.{IntType, FormInputType, StringType}
import io.perfix.question.Form.FormInputName
import io.perfix.question.{Form, FormParams, FormSeries}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class PerfixFormSeriesExecutorTest extends AnyFlatSpec with Matchers {
  def mockQuestionnaire: FormSeries = new FormSeries {
    override val forms: Iterator[Form] = Iterator(
      new Form {
        override val mapping: Map[FormInputName, FormInputType] = Map("Q1" -> FormInputType(StringType))
        override val storeQuestionParams: FormParams = new FormParams {}
        override def shouldAsk(): Boolean = true
        override def setAnswers(answers: Map[FormInputName, Any]): Unit = {}
      },
      new Form {
        override val mapping: Map[FormInputName, FormInputType] = Map("Q2" -> FormInputType(IntType))
        override val storeQuestionParams: FormParams = new FormParams {}
        override def shouldAsk(): Boolean = true
        override def setAnswers(answers: Map[FormInputName, Any]): Unit = {}
      }
    )
  }

  "A PerfixQuestionnaireExecutor" should  "return the next question"  in {
    val executor = new PerfixQuestionnaireExecutor(mockQuestionnaire)

    executor.hasNext
    val question1 = executor.next()
    question1 shouldBe Map("Q1" -> FormInputType(StringType))

    executor.hasNext
    val question2 = executor.next()
    question2 shouldBe Map("Q2" -> FormInputType(IntType))
  }

  "A PerfixQuestionnaireExecutor" should "allow submitting answers to the current question" in {
    val executor = new PerfixQuestionnaireExecutor(mockQuestionnaire)

    executor.hasNext
    executor.next()
    executor.submit(Map("Q1" -> "Answer1"))

    executor.hasNext
    executor.next()
    executor.submit(Map("Q2" -> 42))
  }

  "A PerfixQuestionnaireExecutor" should "throw an exception when submitting answers without a defined question" in {
    val executor = new PerfixQuestionnaireExecutor(mockQuestionnaire)

    // Attempt to submit answers without a defined question
    an[UnsupportedOperationException] should be thrownBy {
      executor.submit(Map("Q1" -> "Answer1"))
    }
  }
}
