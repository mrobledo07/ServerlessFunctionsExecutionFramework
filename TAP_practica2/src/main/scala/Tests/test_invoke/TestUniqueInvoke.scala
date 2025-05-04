package Tests.test_invoke

import FaaS.Controller

import java.util.function.Function
import java.util.{HashMap => JavaHashMap, Map => JavaMap}

object TestUniqueInvoke extends App {
  val controller = new Controller(4, 1024)

  // Acción para sumar dos enteros.
  val addAction: Function[JavaMap[String, Integer], Integer] = (x: JavaMap[String, Integer]) => {
    x.getOrDefault("x", 0) + x.getOrDefault("y", 0)
  }
  controller.registerAction("addAction", addAction, 256)

  // Acción para concatenar dos cadenas.
  val concatAction: Function[JavaMap[String, String], Any] = (x: JavaMap[String, String]) => {
    x.getOrDefault("x", "") + x.getOrDefault("y", "")
  }
  controller.registerAction("concatAction", concatAction, 256)

  // Intento de invocar la acción addAction varias veces.
  try {
    var res: Any = controller.invoke("addAction", new JavaHashMap[String, Integer]() {
      {
        put("x", 6); put("y", 2)
      }
    })
    res = controller.invoke("addAction", new JavaHashMap[String, Integer]() {
      {
        put("x", 6); put("y", 2)
      }
    })
    res = controller.invoke("addAction", new JavaHashMap[String, Integer]() {
      {
        put("x", 6); put("y", 2)
      }
    })
    res = controller.invoke("addAction", new JavaHashMap[String, Integer]() {
      {
        put("x", 6); put("y", 2)
      }
    })
    res = controller.invoke("addAction", new JavaHashMap[String, Integer]() {
      {
        put("x", 6); put("y", 2)
      }
    })
    println(res.asInstanceOf[Integer])
  } catch {
    case e: RuntimeException => println(e.getCause.getMessage)
  }

  // Intento de invocar la acción concatAction.
  try {
    val res = controller.invoke("concatAction", new JavaHashMap[String, String]() {
      {
        put("x", "Hola "); put("y", "Mundo")
      }
    }).asInstanceOf[String]
    println(res)
  } catch {
    case e: RuntimeException => println(e.getMessage)
  }
}
