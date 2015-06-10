package net.ssanj

import net.ssanj.ContrastOps._
import net.ssanj.ContrastPrinter._

object Main {

  def main(args: Array[String]) {
    val newLine = System.getProperty("line.separator")

    val resourcePath = "src/main/resources"
    val result = (for {
     c1 <- ConfigLoader.loadConfigFromFile(s"$resourcePath/driver1.conf").right
     c2 <- ConfigLoader.loadConfigFromFile(s"$resourcePath/driver2.conf").right
    } yield contrast(c1, c2)).fold(printError, printContrast(newLine))

    println(result)
  }
}
