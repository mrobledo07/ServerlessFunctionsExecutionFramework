package Tests.test_invoke

import FaaS.Controller
import Strategy.{BigGroup, GreedyGroup, UniformGroup}

import java.util.function.Function
import java.util.{List => JavaList, Map => JavaMap}
import scala.jdk.CollectionConverters._

object TestGroupAsync extends App {
  val controller = new Controller(4, 1024)
  val addAction: Function[JavaMap[String, Integer], Integer] = (x: JavaMap[String, Integer]) => {
    x.getOrDefault("x", 0) + x.getOrDefault("y", 0)
  }

  controller.registerAction("addAction", addAction, 512)

  val inputScala = List(
    Map("x" -> 2, "y" -> 3),
    Map("x" -> 9, "y" -> 1),
    Map("x" -> 8, "y" -> 8),
    Map("x" -> 5, "y" -> 2),
    Map("x" -> 12, "y" -> 66),
    Map("x" -> 15, "y" -> 8)
  )
  val inputJava: JavaList[JavaMap[String, Int]] = inputScala.map(_.asJava).asJava

  println("--------Testing group async invoke with RoundRobin policy--------")
  var results = controller.invoke_async("addAction", inputJava)
  try {
    println(results.get().toString)
  } catch {
    case e: Exception => println(e.getMessage)
  }

  println("--------Testing group async invoke with GreedyGroup policy--------")
  controller.setPolicy(new GreedyGroup())
  results = controller.invoke_async("addAction", inputJava)
  try {
    println(results.get().toString)
  } catch {
    case e: Exception => println(e.getMessage)
  }

  println("--------Testing group async invoke with UniformGroup policy ERROR--------")
  controller.setPolicy(new UniformGroup(3))
  results = controller.invoke_async("addAction", inputJava)
  try {
    println(results.get().toString)
  } catch {
    case e: Exception => println(e.getMessage)
  }

  println("--------Testing group async invoke with UniformGroup policy GOOD--------")
  controller.registerAction("addAction256", addAction, 256)
  results = controller.invoke_async("addAction256", inputJava)
  try {
    println(results.get().toString)
  } catch {
    case e: Exception => println(e.getMessage)
  }

  println("--------Testing group async invoke with BigGroup policy--------")
  controller.setPolicy(new BigGroup(2))
  results = controller.invoke_async("addAction256", inputJava)
  try {
    println(results.get().toString)
  } catch {
    case e: Exception => println(e.getMessage)
  }

  controller.shutdown()
}
