package MapReduce

class WordCount {
  def wordCount(text: List[String]): Map[String, Int] = {
    text.flatMap(_.toLowerCase.replaceAll("[^a-zA-Z]", "").split(" "))
      .filter(_.nonEmpty)
      .groupBy(identity)
      .view.mapValues(_.length)
      .toMap
  }
}
