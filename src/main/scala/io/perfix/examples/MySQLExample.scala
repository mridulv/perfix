package io.perfix.examples

import io.perfix.generator.FakeDataGenerator
import io.perfix.model.{AddressType, ColumnDescription, DataDescription, NameType}
import io.perfix.stores.mysql.{MySQLConnectionParams, MySQLStore, MySQLTableParams}
object MySQLExample {

  def main(args: Array[String]): Unit = {
    java.lang.System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2")

    val columnDescriptions = Seq(
      ColumnDescription("name", NameType),
      ColumnDescription("address", AddressType)
    )

    val dataDescription = new DataDescription()
    dataDescription.rowsOpt = Some(100)
    dataDescription.columnsOpt = Some(columnDescriptions)

    val fakeDataGenerator = new FakeDataGenerator
    val mysqlStore = new MySQLStore(fakeDataGenerator.generateData(dataDescription))

    val questionnaire = mysqlStore.questions()
    questionnaire.mySQLParams.mySQLTableParams = Some(MySQLTableParams("******", "******"))
    questionnaire.mySQLParams.mySQLConnectionParams = Some(MySQLConnectionParams("jdbc:mysql://localhost:3306?autoReconnect=true&useSSL=false", "******", "******"))

    mysqlStore.initialize()
    mysqlStore.putData()
    mysqlStore.readData("select count(*) from testing limit 10")
  }
}
