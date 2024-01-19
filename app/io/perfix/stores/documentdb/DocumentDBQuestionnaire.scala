package io.perfix.stores.documentdb

import io.perfix.question.{Question, Questionnaire}

case class DocumentDBQuestionnaire(params: DocumentDBParams) extends Questionnaire {
  override val questions: Iterator[Question] = Iterator()
}
