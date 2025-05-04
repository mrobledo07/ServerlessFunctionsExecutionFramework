package Tests.test_decorator

import Decorator.TimerFunctionDecorator
import FaaS.Controller

import java.util.function.Function
import java.util.{HashMap => JavaHashMap, List => JavaList, Map => JavaMap}
import scala.jdk.CollectionConverters._
import scala.jdk.FunctionConverters._


object TestTimer extends scala.App {

  val controller = new Controller(4, 1024)

  // Función original
  val originalFunction: Function[JavaMap[String, Integer], Integer] = (x: JavaMap[String, Integer]) => {
    x.getOrDefault("x", 0) + x.getOrDefault("y", 0)
  }

  // Conversión de Java Function a Scala Function
  val scalaOriginalFunction = originalFunction.asScala

  // Aplicar TimerDecorator
  val timedScalaFunction = new TimerFunctionDecorator(scalaOriginalFunction)

  // Conversión de Scala Function a Java Function
  val javaTimedFunction = timedScalaFunction.asJava

  // Registrar la función cronometrada en el controlador
  controller.registerAction("timedFunction", javaTimedFunction, 256)

  // Datos de entrada para probar la función memoizada
  val inputScala = List(
    Map("x" -> 2, "y" -> 3),
    Map("x" -> 9, "y" -> 1),
    Map("x" -> 8, "y" -> 8),
    Map("x" -> 2, "y" -> 3),
    Map("x" -> 12, "y" -> 66),
    Map("x" -> 9, "y" -> 1)
  )


  // Pasamos datos de entrada a Java
  val inputJava: JavaList[JavaMap[String, Integer]] = inputScala.map(mapScala => {
    val mapJava: JavaMap[String, Integer] = new JavaHashMap[String, Integer]()
    mapScala.foreach { case (key, value) => mapJava.put(key, Integer.valueOf(value)) }
    mapJava
  }).asJava


  // Invocar la función memoizada de manera asíncrona
  var results = controller.invoke_async("timedFunction", inputJava)

  // Manejar los resultados obtenidos de manera asíncrona
  try {
    println(results.get().toString)
  } catch {
    case e: Exception => println(e.getMessage)
  }

  // Apagar el controlador
  controller.shutdown()
}
