package io.perfix.examples


object MySQLStoreTest {

  def main(args: Array[String]): Unit = {
//    val mappedVariables: Map[String, Any] = Map(
//      USERNAME -> "root",
//      URL -> "jdbc:mysql://localhost:3306/perfix",
//      PASSWORD -> "test12345",
//      DBNAME -> "perfix",
//      TABLENAME -> "test",
//      SECONDARY_INDEX_COLUMNS -> "student_name",
//      INSTANCE_IDENTIFIER -> "dbinstance",
//      INSTANCE_TYPE -> "db.t4g.micro",
//      AWS_ACCESS_KEY -> "************",
//      AWS_ACCESS_SECRET -> "************************************",
//      LAUNCH_DB -> false
//    )
//    val cols = Json.parse("[{\"columnName\":\"student_name\",\"columnType\":{\"type\":\"NameType\",\"isUnique\":true},\"columnValueDistribution\":{\"value\":\"John\",\"probability\":0.1}},{\"columnName\":\"student_address\",\"columnType\":{\"type\":\"AddressType\",\"isUnique\":false}}]").as[Seq[ColumnDescription]]
//    val datasetParams = DatasetParams(None, "dataset", 100, cols)
//    val experimentParams: ExperimentParams = ExperimentParams.experimentParamsForTesting
//    val experimentExecutor = new ExperimentExecutor("mysql", experimentParams, dataset = datasetParams.dataset)
//    while (experimentExecutor.getFormSeriesEvaluator.hasNext) {
//      val form = experimentExecutor.getFormSeriesEvaluator.next()
//      val answerMapping = form.map { case (k, formInputType) =>
//        val mappedValue = if (formInputType.isRequired) {
//          Some(mappedVariables(k))
//        } else {
//          mappedVariables.get(k)
//        }
//        k -> mappedValue
//      }
//      experimentExecutor.getFormSeriesEvaluator.submit(Form.filteredAnswers(answerMapping))
//    }
//
//    experimentExecutor.runExperiment()
//    experimentExecutor.cleanUp()
  }
}
