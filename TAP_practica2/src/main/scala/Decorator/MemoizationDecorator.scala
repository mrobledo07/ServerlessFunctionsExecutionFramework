package Decorator


class MemoizationDecorator[T, R](function: T => R) extends (T => R) {
  private val cache = scala.collection.mutable.Map[T, R]()

  override def apply(args: T): R = cache.get(args) match {
    case Some(result) =>
      println("Cache hit!")
      result
    case None =>
      val result = function(args)
      cache.put(args, result)
      result
  }
}
