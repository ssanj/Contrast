package net.ssanj

import org.scalatest.{EitherValues, Matchers, WordSpecLike}

final class ConfigLoaderSpec extends WordSpecLike with Matchers with EitherValues {

  "A ConfigLoader" should {
    "return a ConfigExceptionError" when {
      "the config file to load does not exists" in {
        val result = ConfigLoader.loadConfigFromFile("src/test/resources/unknown.conf")
        result.left.value.fold(_.getMessage should include regex "java.io.FileNotFoundException") (x =>
          fail(s"Expected ConfigExceptionError but got: $x"))
      }
    }

    "return a Config" when {
      "the config file to load exists" in {
        val result = ConfigLoader.loadConfigFromFile("src/test/resources/main1.conf")
        result should be (ConfigLoader.createConfigFromMap(Map("db.default.driver" -> "com.mysql.jdbc.Driver")))
      }
    }
  }

}
