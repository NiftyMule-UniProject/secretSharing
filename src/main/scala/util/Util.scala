package util

import scala.annotation.tailrec

object Util {
  def generateUniqueList[T](n: Int, gen: () => T)(eq: (T, T) => Boolean): Seq[T] = {
    @tailrec
    def inner(count: Int, curr: Seq[T]): Seq[T] = {
      count match
        case 0 => curr
        case _ =>
          val randomItem = LazyList
            .continually(gen())
            .filterNot(x => curr.exists(y => eq(x, y)))
            .head
          inner(count - 1, curr :+ randomItem)
    }

    inner(n, Seq())
  }
}
