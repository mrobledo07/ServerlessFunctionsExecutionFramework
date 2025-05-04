package MapReduce

import scala.collection.mutable

class Reduce {
  def reduceWordCount(wordCountList: List[mutable.Map[String, Int]]): Map[String, Int] = {
    wordCountList.flatten
      .groupBy(_._1)
      .view.mapValues(_.map(_._2).sum)
      .toMap
  }

  def reduceCountWords(countWordsList: List[Int]): Int = {
    countWordsList.sum
  }
}
