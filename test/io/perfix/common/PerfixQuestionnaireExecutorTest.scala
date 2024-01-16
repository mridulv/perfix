package io.perfix.common

import io.perfix.model.{IntType, QuestionType, StringType}
import io.perfix.question.Question.QuestionLabel
import io.perfix.question.{Question, QuestionParams, Questionnaire}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class PerfixQuestionnaireExecutorTest extends AnyFlatSpec with Matchers {
  def mockQuestionnaire: Questionnaire = new Questionnaire {
    override val questions: Iterator[Question] = Iterator(
      new Question {
        override val mapping: Map[QuestionLabel, QuestionType] = Map("Q1" -> QuestionType(StringType))
        override val storeQuestionParams: QuestionParams = new QuestionParams {}
        override def shouldAsk(): Boolean = true
        override def setAnswers(answers: Map[QuestionLabel, Any]): Unit = {}
      },
      new Question {
        override val mapping: Map[QuestionLabel, QuestionType] = Map("Q2" -> QuestionType(IntType))
        override val storeQuestionParams: QuestionParams = new QuestionParams {}
        override def shouldAsk(): Boolean = true
        override def setAnswers(answers: Map[QuestionLabel, Any]): Unit = {}
      }
    )
  }

  "A PerfixQuestionnaireExecutor" should  "return the next question"  in {
    val executor = new PerfixQuestionnaireExecutor(mockQuestionnaire)

    executor.hasNext
    val question1 = executor.next()
    question1 shouldBe Map("Q1" -> QuestionType(StringType))

    executor.hasNext
    val question2 = executor.next()
    question2 shouldBe Map("Q2" -> QuestionType(IntType))
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
