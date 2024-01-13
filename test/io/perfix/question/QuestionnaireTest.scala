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
  class TestQuestionnaire(q: Seq[Question]) extends Questionnaire {
    override val questions: Iterator[Question] = q.iterator
  }

  it should "iterate over questions and return the next question that should be asked" in {
    val question1 = MockQuestion("Q1", shouldAskResult = false)
    val question2 = MockQuestion("Q2", shouldAskResult = true)
    val question3 = MockQuestion("Q3", shouldAskResult = true)

    val questionnaire = new TestQuestionnaire(Seq(question1, question2, question3))

    questionnaire.iterator.next().asInstanceOf[MockQuestion].label shouldEqual "Q2"
    questionnaire.iterator.next().asInstanceOf[MockQuestion].label shouldEqual "Q3"

    questionnaire.iterator.hasNext shouldEqual false
  }
}