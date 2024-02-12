package io.perfix.commands

import picocli.CommandLine

@CommandLine.Command(name = "perfix", mixinStandardHelpOptions = true, description = Array("Run performance experiments against different stores"))
class PerfixCommand extends Runnable {

  @CommandLine.Spec
  private var spec: CommandLine.Model.CommandSpec = _

  @CommandLine.Command(name = "mysql", description = Array("Run performance experiment on MySQL"))
  val mysqlCommand: MySQLCommand = new MySQLCommand()

  @CommandLine.Command(name = "redis", description = Array("Run performance experiment on MySQL"))
  val redisCommand: RedisCommand = new RedisCommand()

  @CommandLine.Command(name = "mongodb", description = Array("Run performance experiment on MySQL"))
  val mongoDBCommand: MongoDBCommand = new MongoDBCommand()

  @CommandLine.Command(name = "dynamodb", description = Array("Run performance experiment on MySQL"))
  val dynamoDBCommand: DynamoDBCommand = new DynamoDBCommand()

  override def run(): Unit = {
    throw new CommandLine.ParameterException(spec.commandLine(), "Specify a subcommand")
  }
}

object PerfixCommand {
  def main(args: Array[String]): Unit = {
    System.exit(new CommandLine(new PerfixCommand()).execute(args: _*))
  }
}
