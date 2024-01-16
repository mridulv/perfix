package io.perfix.question

import io.perfix.model.{QuestionType, StringType}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class QuestionnaireTest extends AnyFlatSpec with Matchers {

  case class MockQuestion(label: String, shouldAskResult: Boolean) extends Question {
    override val mapping: Map[Question.QuestionLabel, QuestionType] = Map(label -> QuestionType(StringType))
    override val storeQuestionParams: QuestionParams = new QuestionParams {}
    override def shouldAsk(): Boolean = shouldAskResult
    override def setAnswers(answers: Map[Question.QuestionLabel, Any]): Unit = ???
  }

  // Test class that extends Questionnaire
  class TestQuestionnaire extends Questionnaire {
    override val questions: Iterator[Question] = Iterator(
    MockQuestion("Q1", shouldAskResult = true),
    MockQuestion("Q2", shouldAskResult = true),
    MockQuestion("Q3", shouldAskResult = true)
    )
  }

  it should "iterate over questions and return the next question that should be asked" in {

    val questionnaire = new TestQuestionnaire
    val iter = questionnaire.iterator

    iter.hasNext shouldEqual true
    iter.next().asInstanceOf[MockQuestion].label shouldEqual "Q1"

    iter.hasNext shouldEqual true
    iter.next().asInstanceOf[MockQuestion].label shouldEqual "Q2"

    iter.hasNext shouldEqual true
    iter.next().asInstanceOf[MockQuestion].label shouldEqual "Q3"

    iter.hasNext shouldEqual false
  }
}