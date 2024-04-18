package io.perfix.question.mysql

import io.perfix.exceptions.ParamsAlreadyDefinedException
import io.perfix.model.{QuestionType, StringType}
import io.perfix.question.Form
import io.perfix.question.Form.QuestionLabel
import io.perfix.question.mysql.TableIndicesDetailForm.{PRIMARY_INDEX_COLUMN, SECONDARY_INDEX_COLUMNS}
import io.perfix.stores.mysql.{MySQLParams, MySQLTableIndexesParams}

class TableIndicesDetailForm(override val storeQuestionParams: MySQLParams) extends Form {
  override val mapping: Map[QuestionLabel, QuestionType] = Map(
    PRIMARY_INDEX_COLUMN -> QuestionType(StringType, isRequired = false),
    SECONDARY_INDEX_COLUMNS -> QuestionType(StringType, isRequired = false)
  )

  override def shouldAsk(): Boolean = {
    storeQuestionParams.mySQLTableIndexesParams.isEmpty
  }

  override def setAnswers(answers: Map[QuestionLabel, Any]): Unit = {
    import storeQuestionParams._
    mySQLTableIndexesParams match {
      case Some(_) => throw ParamsAlreadyDefinedException("mySQLTableIndexesParams")
      case None =>
        mySQLTableIndexesParams = Some(
          MySQLTableIndexesParams(
            primaryIndexColumn = answers.get(PRIMARY_INDEX_COLUMN).map(_.toString),
            secondaryIndexesColumn = answers.get(SECONDARY_INDEX_COLUMNS).map(_.toString.split(",").map(_.strip()))
          )
        )
    }
  }
}

object TableIndicesDetailForm {
  val PRIMARY_INDEX_COLUMN = "primary_index"
  val SECONDARY_INDEX_COLUMNS = "secondary_indexes"

  def apply(mySQLParams: MySQLParams): TableIndicesDetailForm = {
    new TableIndicesDetailForm(mySQLParams)
  }
}