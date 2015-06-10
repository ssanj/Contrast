package net.ssanj

import com.typesafe.config.ConfigValueType
import net.ssanj.ConfigLoader.ConfigLoaderError
import net.ssanj.ContrastOps.contrast
import net.ssanj.ContrastTypes.{ConfigEntry, ConfigEntryPair, Contrast, DiffEntry}
import org.scalatest.{EitherValues, Matchers, OptionValues, WordSpecLike}

final class ContrastOpsSpec extends WordSpecLike with Matchers with EitherValues with OptionValues {

  private val resourcesDir = "src/test/resources"

  "A contrast" should {
    "identity differing values with the same key" in {
      val configContrast = loadFiles("main1.conf", "main2.conf")

      configContrast.fold(fail("expected differing values")){ (leftOp, rightOp, diffOp) =>
        leftOp should be (None)
        rightOp should be (None)
        diffOp.fold(fail("expected differing values")) { dv =>
          dv.values should have size 1

          assertDiffEntry("db.default.driver")(
                          ConfigValueType.STRING, "com.mysql.jdbc.Driver")(
                          ConfigValueType.STRING, "com.oracle.jdbc.Driver")(dv.values.iterator.next())
        }
      }
    }
  }

  private def loadFiles(file1:String, file2:String): Contrast = {
    val result:Either[ConfigLoaderError, Contrast] = for {
      c1 <- ConfigLoader.loadConfigFromFile(s"$resourcesDir/main1.conf").right
      c2 <- ConfigLoader.loadConfigFromFile(s"$resourcesDir/main2.conf").right
    } yield contrast(c1, c2)

    result.right.value
  }

  private def assertDiffEntry: String => (ConfigValueType, AnyRef) => (ConfigValueType, AnyRef) => DiffEntry => Unit =
    key => (lvt, lv) => (rvt, rv) => de => {
      de.key should be (key)
      val ConfigEntryPair(left, right) = de.pair
      assertConfigValue(key, lvt, lv)(left)
      assertConfigValue(key, rvt, rv)(right)
  }

  private def assertConfigValue: (String, ConfigValueType, AnyRef) => ConfigEntry => Unit = (key, vtype, value) => ce => {
    ce.key should be (key)
    ce.value.valueType() should be (vtype)
    ce.value.unwrapped() should be (value)
  }

}
