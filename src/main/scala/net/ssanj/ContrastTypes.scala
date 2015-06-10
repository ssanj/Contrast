package net.ssanj

import java.util.Map.Entry

import com.typesafe.config.ConfigValue

object ContrastTypes {

  trait Contrast {
    def fold[X](identical: => X)(different: (Option[OnlyInLeft], Option[OnlyInRight], Option[DifferentValues]) => X): X
  }

  object Contrast {

    val Identical = new Contrast {
      override def fold[X](same: => X)(
        different: (Option[OnlyInLeft], Option[OnlyInRight], Option[DifferentValues]) => X) = same
    }

    def Different(lefts: Option[OnlyInLeft], rights: Option[OnlyInRight], diff: Option[DifferentValues]) = new Contrast {
      override def fold[X](same: => X)(
        different: (Option[OnlyInLeft], Option[OnlyInRight], Option[DifferentValues]) => X) =
        different(lefts, rights, diff)
    }
  }

  final case class DiffEntry(key: String, pair: ConfigEntryPair)

  final case class OnlyInLeft(values: Set[ConfigEntry])

  final case class OnlyInRight(values: Set[ConfigEntry])

  final case class DifferentValues(values: Set[DiffEntry])

  /**
   * ConfigValue#origin
   * ConfigValue#valueType
   * ConfigValue#unwrapped
   * ConfigValue#render
   */

  //should we type the values returned? Or should we use ConfigValue?
  //we may need access to methods on ConfigValue from the result?

  //we also want to do proper diffs on Maps/Objects. Show only the differing/missing/extra keys
  //we also want to do proper diffs on lists. Show only the differing/missing/extra values
  //we need to use a non-empty set for contrast

    final case class ConfigEntry(key: String, value: ConfigValue) {
      def this(entry: Entry[String, ConfigValue]) = this(entry.getKey, entry.getValue)
    }

    final case class ConfigEntryPair(left: ConfigEntry, right: ConfigEntry)

    implicit val configEntrySorter = new Ordering[ConfigEntry] {
      override def compare(x: ConfigEntry, y: ConfigEntry): Int = x.key.compareTo(y.key)
    }
}

