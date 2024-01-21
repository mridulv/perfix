package io.perfix.question.documentdb

import io.perfix.exceptions.ParamsAlreadyDefinedException
import io.perfix.model.{QuestionType, StringType}
import io.perfix.question.Question
import io.perfix.question.Question.QuestionLabel
import io.perfix.question.documentdb.DocumentDBConnectionParamsQuestion._
import io.perfix.stores.documentdb.{DocumentDBConnectionParams, DocumentDBParams}

class DocumentDBConnectionParamsQuestion(override val storeQuestionParams: DocumentDBParams) extends Question {

  override val mapping: Map[QuestionLabel, QuestionType] = Map(
    URL -> QuestionType(StringType),
    DATABASE -> QuestionType(StringType)
  )

  override def shouldAsk(): Boolean = {
    import storeQuestionParams._
    documentDBConnectionParams.isEmpty
  }

  override def setAnswers(answers: Map[QuestionLabel, Any]): Unit = {
    import storeQuestionParams._
    documentDBConnectionParams match {
      case Some(_) => throw ParamsAlreadyDefinedException("mySQLConnectionParams")
      case None =>
        documentDBConnectionParams = Some(DocumentDBConnectionParams(answers(URL).toString, answers(DATABASE).toString))
    }
  }
}

object DocumentDBConnectionParamsQuestion {
  val URL = "url"
  val DATABASE = "database"

  def apply(documentDBParams: DocumentDBParams): DocumentDBConnectionParamsQuestion = {
    new DocumentDBConnectionParamsQuestion(documentDBParams)
  }
}
