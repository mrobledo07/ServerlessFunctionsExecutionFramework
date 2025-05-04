package MapReduce

class CountWords {
  def countWords(text: List[String]): Int = {
    text.flatMap(_.toLowerCase.replaceAll("[^a-zA-Z]", "").split(" "))
      .count(_.nonEmpty)
  }
}
