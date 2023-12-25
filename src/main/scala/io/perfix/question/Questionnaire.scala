package io.perfix.question

trait Questionnaire {

  val questions: Iterator[Question]

  def getQuestions: Iterator[Question] = {
    new Iterator[Question] {
      private var nextQuestion: Option[Question] = findNextQuestion()

      private def findNextQuestion(): Option[Question] = {
        while (questions.hasNext) {
          val potentialQuestion = questions.next()
          if (potentialQuestion.shouldAsk()) {
            return Some(potentialQuestion)
          }
        }
        None
      }

      override def hasNext: Boolean = nextQuestion.isDefined

      override def next(): Question = {
        nextQuestion match {
          case Some(question) =>
            nextQuestion = findNextQuestion() // Prepare the next question for future calls to next
            question
          case None =>
            throw new NoSuchElementException("No more questions to ask")
        }
      }
    }
  }

}
