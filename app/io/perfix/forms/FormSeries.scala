package io.perfix.forms

trait FormSeries {

  val forms: Iterator[Form]

  def iterator: Iterator[Form] = {
    new Iterator[Form] {
      private var nextForm: Option[Form] = None

      private def findNextForm(): Unit = {
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
          findNextForm()
        }
        nextForm.isDefined
      }

      override def next(): Form = {
        nextForm match {
          case Some(form) =>
            nextForm = None
            form
          case None => throw new NoSuchElementException("No more forms")
        }
      }
    }
  }

}
