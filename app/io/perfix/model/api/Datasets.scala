package io.perfix.model.api

import play.api.libs.json.{Format, Json}

case class Datasets(datasets: Seq[Dataset]) {

  def sampleDataset(numRows: Int): Datasets = {
    Datasets(datasets.map(_.sampleDataset(numRows)))
  }

}

object Datasets {
  implicit val DatasetsFormatter: Format[Datasets] = Json.format[Datasets]
}