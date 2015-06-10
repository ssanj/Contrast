package net.ssanj

import java.util.Map.Entry

import com.typesafe.config.{Config, ConfigValue}
import net.ssanj.ContrastTypes._


object ContrastOps {

//  @SuppressWarnings(Array("org.brianmckenna.wartremover.warts.Throw"))
  def contrast(config1:Config, config2:Config): Contrast = {
    val configL: Set[ConfigEntry] = toConfigEntrySet(config1)
    val configR: Set[ConfigEntry] = toConfigEntrySet(config2)

    val (leftSame, leftOnly) = configL.partition(lce => configR.exists(rce => lce.key == rce.key))
    val (rightSame, rightOnly) = configR.partition(rce => configL.exists(lce => lce.key == rce.key))

    //Seq((configL, configR), (l2, r2) ...(ln, rn))
    val diff = leftSame.zip(rightSame).
      filter(c => c._1.key == c._2.key && c._1.value != c._2.value).
      map(c => DiffEntry(c._1.key, ConfigEntryPair(c._1, c._2)))

    val lOp:Option[OnlyInLeft] = whenNotEmpty(OnlyInLeft)(leftOnly)
    val rOp:Option[OnlyInRight] = whenNotEmpty(OnlyInRight)(rightOnly)
    val diffOp:Option[DifferentValues] = whenNotEmpty(DifferentValues)(diff)

    if (atLeastOneOf(lOp, rOp, diffOp)) Contrast.Different(lOp, rOp, diffOp) else Contrast.Identical
  }
  
  
  def atLeastOneOf[A,B,C](optA:Option[A], optB:Option[B], optC:Option[C]): Boolean =
    optA.isDefined || optB.isDefined || optC.isDefined

  private def whenNotEmpty[A, B] = conditional[Set[A], B](_.nonEmpty) _

  //this looks like a scalaz primitive
  private def conditional[A, B](p:A => Boolean)(f:A => B): A => Option[B] = a => if (p(a)) Some(f(a)) else None

  //we need to optimise this
  private def toConfigEntrySet: Config => Set[ConfigEntry] = c => {
    import ContrastTypes.configEntrySorter

    import scala.collection.JavaConverters.asScalaSetConverter
    val sortedSet = collection.immutable.TreeSet.empty[ConfigEntry]
    val y = sortedSet ++ c.entrySet().asScala.toSet[Entry[String, ConfigValue]].map[ConfigEntry, Set[ConfigEntry]](x => new ConfigEntry(x))
    y
  }
}
