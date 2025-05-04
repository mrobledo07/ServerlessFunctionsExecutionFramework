package Tests.test_invoke

import FaaS.Controller

import java.util.function.Function
import java.util.{List => JavaList, Map => JavaMap}
import scala.jdk.CollectionConverters._

object TestGroupInvoke extends App {
  val controller = new Controller(4, 1024)
  val f: Function[JavaMap[String, Integer], Integer] = (x: JavaMap[String, Integer]) => {
    val valX = x.getOrDefault("x", 0)
    val valY = x.getOrDefault("y", 0)
    valX + valY
  }

  controller.registerAction("addAction", f, 512)

  private val inputScala = List(
    Map("x" -> 2, "y" -> 3),
    Map("x" -> 9, "y" -> 1),
    Map("x" -> 8, "y" -> 8)
  )

  // Convertimos la lista Scala de Scala Maps a una lista Java de Java Maps
  private val inputJava: JavaList[JavaMap[String, Int]] = inputScala.map(_.asJava).asJava

  try {
    val resultJava: JavaList[Integer] = controller.invoke("addAction", inputJava)
    val resultScala: List[Integer] = resultJava.asScala.toList
    println(resultScala)
  } catch {
    case e: RuntimeException => println(e.getMessage)
  }
}
