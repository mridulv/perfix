package io.perfix.question

trait Questionnaire {

  val questions: Iterator[Form]

  def iterator: Iterator[Form] = {
    new Iterator[Form] {
      private var nextQuestion: Option[Form] = None

      private def findNextQuestion(): Unit = {
        while (questions.hasNext) {
          val potentialQuestion = questions.next()
          if (potentialQuestion.shouldAsk()) {
            nextQuestion = Some(potentialQuestion)
            return
          }
        }
        nextQuestion = None
      }

      override def hasNext: Boolean = {
        if (nextQuestion.isEmpty) {
          findNextQuestion()
        }
        nextQuestion.isDefined
      }

      override def next(): Form = {
        nextQuestion match {
          case Some(ques) =>
            nextQuestion = None
            ques
          case None => throw new NoSuchElementException("No more questions to ask")
        }
      }
    }
  }

}
