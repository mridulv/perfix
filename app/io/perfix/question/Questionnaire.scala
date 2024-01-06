package io.perfix.question

trait Questionnaire {

  val questions: Iterator[Question]

  def iterator: Iterator[Question] = {
    new Iterator[Question] {
      private var nextQuestion: Option[Question] = None

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

      override def next(): Question = {
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
