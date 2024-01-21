package io.perfix.question.documentdb

import io.perfix.exceptions.ParamsAlreadyDefinedException
import io.perfix.model.{QuestionType, StringType}
import io.perfix.question.Question
import io.perfix.question.Question.QuestionLabel
import io.perfix.question.documentdb.DocumentDBTableParamsQuestions.COLLECTION_NAME
import io.perfix.stores.documentdb.{DocumentDBParams, DocumentDBTableParams}

class DocumentDBTableParamsQuestions(override val storeQuestionParams: DocumentDBParams) extends Question {

  override val mapping: Map[QuestionLabel, QuestionType] = Map(
    COLLECTION_NAME -> QuestionType(StringType)
  )

  override def shouldAsk(): Boolean = {
    storeQuestionParams.documentDBTableParams.isEmpty
  }

  override def setAnswers(answers: Map[QuestionLabel, Any]): Unit = {
    import storeQuestionParams._
    documentDBTableParams match {
      case Some(_) => throw ParamsAlreadyDefinedException("mySQLTableParams")
      case None => documentDBTableParams = Some(DocumentDBTableParams(answers(COLLECTION_NAME).toString))
    }
  }

}

object DocumentDBTableParamsQuestions {
  val COLLECTION_NAME = "collectionName"

  def apply(documentDBParams: DocumentDBParams): DocumentDBTableParamsQuestions = {
    new DocumentDBTableParamsQuestions(documentDBParams)
  }
}


