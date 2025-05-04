package Tests.test_mapreduce

import FaaS.Controller
import FilesReader.FilesReader
import MapReduce._

import java.util.concurrent.CompletableFuture
import scala.jdk.CollectionConverters._
import scala.jdk.FunctionConverters.enrichAsJavaFunction
import java.util.{List => JavaList, Map => JavaMap}
import scala.collection.mutable
import scala.util.{Failure, Success, Try}


object TestMapReduce extends App {
  val controller = new Controller(10, 1024)

  val wordCount = new WordCount()
  val countWords = new CountWords()
  val reduce = new Reduce()

  val wordCountFunc: JavaList[String] => JavaMap[String, Integer] =
    (list: JavaList[String]) => {
      val scalaList = list.asScala.toList
      val wordCountResult = wordCount.wordCount(scalaList)
      wordCountResult.map { case (word, count) => (word, count: Integer) }.asJava
    }

  controller.registerAction("wordCount", wordCountFunc.asJava, 512)

  val countWordsFunc: JavaList[String] => Integer =
    (list: JavaList[String]) => {
      val scalaList = list.asScala.toList
      val countWordsResult = countWords.countWords(scalaList)
      countWordsResult: Integer
    }


  controller.registerAction("countWords", countWordsFunc.asJava, 512)

  val textCollections = FilesReader.readFiles()

  val wordCountResultsFuture: CompletableFuture[JavaList[JavaMap[String, Int]]] =
    controller.invoke_async("wordCount", textCollections)

  val countWordsResultsFuture: CompletableFuture[JavaList[Int]] =
    controller.invoke_async("countWords", textCollections)

  Try {
    val wordCountResults: JavaList[JavaMap[String, Int]] = wordCountResultsFuture.get()
    val countWordsResults: JavaList[Int] = countWordsResultsFuture.get()

    val scalaWordCountResults: List[mutable.Map[String, Int]] = wordCountResults.asScala.map(_.asScala).toList
    val scalaCountWordsResults: List[Int] = countWordsResults.asScala.toList



    val finalWordCountResult = reduce.reduceWordCount(scalaWordCountResults)
    val finalCountWordsResult = reduce.reduceCountWords(scalaCountWordsResults)

    val javaWordCountResults: JavaMap[String, Integer] = finalWordCountResult.map { case (k, v) => (k, v: Integer) }.asJava

    println(scalaCountWordsResults)
    println(finalCountWordsResult)
    FilesReader.saveWordCountsToFile(javaWordCountResults, "src/main/scala/Tests/test_mapreduce/results/mapReduceResults.txt")
  } match {
    case Success(_) =>
    case Failure(exception) => println(exception.getMessage)
  }

  controller.shutdown()
}
