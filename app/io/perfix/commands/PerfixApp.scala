package io.perfix.commands

import picocli.CommandLine

object PerfixApp {
  def main(args: Array[String]): Unit = {
    new CommandLine(new PerfixCommand()).execute(args: _*)
  }
}

