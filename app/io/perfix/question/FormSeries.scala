package io.perfix.question

trait FormSeries {

  val forms: Iterator[Form]

  def iterator: Iterator[Form] = {
    new Iterator[Form] {
      private var nextForm: Option[Form] = None

      private def findNextQuestion(): Unit = {
        while (forms.hasNext) {
          val potentialNextForm = forms.next()
          if (potentialNextForm.shouldAsk()) {
            nextForm = Some(potentialNextForm)
            return
          }
        }
        nextForm = None
      }

      override def hasNext: Boolean = {
        if (nextForm.isEmpty) {
          findNextQuestion()
        }
        nextForm.isDefined
      }

      override def next(): Form = {
        nextForm match {
          case Some(ques) =>
            nextForm = None
            ques
          case None => throw new NoSuchElementException("No more forms")
        }
      }
    }
  }

}
