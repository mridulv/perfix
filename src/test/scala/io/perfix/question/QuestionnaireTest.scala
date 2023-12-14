package io.perfix.question

import io.perfix.context.{MappedQuestionExecutionContext, QuestionExecutionContext}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class QuestionnaireTest extends AnyFlatSpec with Matchers {
  case class TestStoreQuestionParams(question: String) extends QuestionParams

  case class TestQuestion(question: String) extends Question {
    def shouldAsk(): Boolean = true

    override val questionExecutionContext: QuestionExecutionContext = new MappedQuestionExecutionContext(Map.empty)

    override val storeQuestionParams: TestStoreQuestionParams = TestStoreQuestionParams(question)

    override def evaluateQuestion(): Unit = ???
  }

  class TestQuestionnaire extends Questionnaire {
    override protected val questions: Iterator[TestQuestion] = Iterator(
      TestQuestion("Question 1"),
      TestQuestion("Question 2")
    )
  }

  "Questionnaire" should "provide an iterator that only returns questions that should be asked" in {
    val questionnaire = new TestQuestionnaire()
    val questions = questionnaire.getQuestions

    val retrievedQuestions = questions.toList
    retrievedQuestions should have length 2
    retrievedQuestions.head.storeQuestionParams.isInstanceOf[TestStoreQuestionParams] should be(true)
    retrievedQuestions.head.storeQuestionParams.asInstanceOf[TestStoreQuestionParams].question should be("Question 1")
  }

  it should "throw NoSuchElementException if no more questions to ask" in {
    val questionnaire = new TestQuestionnaire()
    val questions = questionnaire.getQuestions

    while (questions.hasNext) {
      questions.next()
    }

    a[NoSuchElementException] should be thrownBy {
      questions.next()
    }
  }
}
