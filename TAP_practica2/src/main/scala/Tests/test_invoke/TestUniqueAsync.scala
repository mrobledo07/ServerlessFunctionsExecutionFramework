package Tests.test_invoke

import FaaS.Controller
import Strategy.{BigGroup, GreedyGroup, UniformGroup}

import java.util.function.Function
import java.util.{Map => JavaMap}
import scala.jdk.CollectionConverters._

object TestUniqueAsync extends App {
  val controller = new Controller(4, 1024)

  val addAction: Function[JavaMap[String, Integer], Integer] = (t: JavaMap[String, Integer]) => {
    t.getOrDefault("x", 0) + t.getOrDefault("y", 0)
  }

  controller.registerAction("addAction", addAction, 256)


  // Ejemplo para RoundRobin policy
  val roundRobinResults = (1 to 6).map(_ => controller.invoke_async("addAction", Map("x" -> 6, "y" -> 2).asJava))
  roundRobinResults.foreach(f => println(f.get()))

  controller.setPolicy(new GreedyGroup())

  // Ejemplo para GreedyGroup policy
  val greedyGroupResults = (1 to 9).map(_ => controller.invoke_async("addAction", Map("x" -> 6, "y" -> 2).asJava))
  greedyGroupResults.foreach(f => println(f.get()))

  controller.setPolicy(new UniformGroup(4))

  // Ejemplo para UniformGroup policy
  val uniformGroupResults = (1 to 8).map(_ => controller.invoke_async("addAction", Map("x" -> 6, "y" -> 2).asJava))
  uniformGroupResults.foreach(f => println(f.get()))

  controller.setPolicy(new BigGroup(2))

  // Ejemplo para BigGroup policy
  val bigGroupResults = (1 to 8).map(_ => controller.invoke_async("addAction", Map("x" -> 6, "y" -> 2).asJava))
  bigGroupResults.foreach(f => println(f.get()))

  controller.shutdown()
}
