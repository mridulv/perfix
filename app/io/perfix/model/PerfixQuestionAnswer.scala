package io.perfix.model

import io.perfix.question.Question.QuestionLabel
import play.api.libs.json.{JsBoolean, JsError, JsNumber, JsString, JsSuccess, Json, Reads, Writes}

case class PerfixQuestionAnswer(questionLabel: QuestionLabel, answer: Any)

object PerfixQuestionAnswer {
  implicit val anyReads: Reads[Any] = Reads {
    case JsString(str) => JsSuccess(str)
    case JsNumber(num) if num.isValidInt => JsSuccess(num.toInt)
    case JsBoolean(b) => JsSuccess(b)
    case _ => JsError("Invalid Any type")
  }

  // Custom Writes for serializing Any
  implicit val anyWrites: Writes[Any] = Writes {
    case str: String => JsString(str)
    case int: Int => JsNumber(int)
    case b: Boolean => JsBoolean(b)
    case _ => throw new UnsupportedOperationException("Serialization of this Any type is not supported")
  }

  implicit val PerfixQuestionAnswerWrites: Writes[PerfixQuestionAnswer] = Json.writes[PerfixQuestionAnswer]
  implicit val PerfixQuestionAnswerReads: Reads[PerfixQuestionAnswer] = Json.reads[PerfixQuestionAnswer]
}
