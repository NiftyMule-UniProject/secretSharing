package util

object Benchmark {

  // from CSYE7200 repo: https://github.com/rchillyard/CSYE7200/blob/Fall2022/lab-sorted/src/main/scala/edu/neu/coe/csye7200/labsorted/benchmark/package.scala
  def times[P, T](n: Int)(pre: => P)(f: P => T): Double = {

    // Warmup phase: do at least 20% of repetitions before starting the clock
    1 to (1 + n / 5) foreach (_ => {
      val input = pre
      f(input)
    })

    var total: Long = 0

    1 to n foreach (_ => {
      val input = pre

      val start = System.nanoTime()
      f(input)
      val end = System.nanoTime()

      total = total + end - start
    })
    total / n.toDouble
  }
}
