package net.ssanj

import net.ssanj.ConfigLoader.ConfigLoaderError
import net.ssanj.ContrastTypes._

import scala.collection.immutable.Iterable

object ContrastPrinter {

  def printContrast: ( => String) => Contrast =>  String = lineSep => con => {
    con.fold[String](new StringBuilder(lineSep).append("Identical").toString()) { (lo, ro, dv) =>
      val lefts = lo.map(s => printConfigEntries(lineSep)(s.values)).getOrElse("-")
      val rights = ro.map(s => printConfigEntries(lineSep)(s.values)).getOrElse("-")
      val diffs = dv.map(d => printDiffEntries(lineSep)(d.values)).getOrElse("-")
      val builder = new StringBuilder
      builder.append(lineSep).append("Only in left config:").
        append(lineSep).append(lefts).
        append(lineSep).append(lineSep).append("Only in the right config:").
        append(lineSep).append(rights).
        append(lineSep).append(lineSep).append("Same key but differing values:").
        append(lineSep).append(diffs).
        toString()
    }
  }

  def printConfigEntry: ConfigEntry => String = ce => s"${ce.key} -> ${ce.value.render()}"

  def printDiffEntry: DiffEntry => String = de => s"${de.key}->${de.pair.left.value.render}|${de.pair.right.value.render}"

  def printError: ConfigLoaderError => String = clr => clr.unify(e1 => e1.toString)

  private def printConfigEntries: (=> String) => Iterable[ConfigEntry] => String = lineSep => it => it.map(printConfigEntry).mkString(lineSep)

  private def printDiffEntries: (=> String) => Iterable[DiffEntry] => String = lineSep => it => it.map(printDiffEntry).mkString(lineSep)

}
