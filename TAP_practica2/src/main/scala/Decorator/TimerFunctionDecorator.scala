package Decorator

class TimerFunctionDecorator[T, R](function: T => R) extends (T => R) {
  override def apply(args: T): R = {
    val startTime = System.nanoTime()
    val result = function(args)
    val endTime = System.nanoTime()

    println(s"Tiempo de ejecución: ${endTime - startTime} ns")
    result
  }
}