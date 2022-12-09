package util

import scala.annotation.tailrec

object MathUtil {

  def lcm(a: BigInt, b: BigInt): BigInt = {
    a * b / a.gcd(b)
  }

  // From wiki: https://www.wikiwand.com/en/Shamir%27s_Secret_Sharing#Python_code
  def extendedGCD(a: BigInt, b: BigInt): (BigInt, BigInt) = {
    @tailrec
    def inner(a: BigInt, b: BigInt, x: BigInt, lastX: BigInt, y: BigInt, lastY: BigInt): (BigInt, BigInt) = {
      if b != 0 then {
        val quot = a / b
        inner(b, a mod b, lastX - quot * x, x, lastY - quot * y, y)
      } else {
        (lastX, lastY)
      }
    }

    inner(a, b, 0, 1, 1, 0)
  }

  // From wiki: https://www.wikiwand.com/en/Shamir%27s_Secret_Sharing#Python_code
  def lagrangeModulo(x: BigInt, xs: Seq[BigInt], ys: Seq[BigInt], p: BigInt): BigInt = {
    def divMod(a: BigInt, b: BigInt, p: BigInt): BigInt = {
      // This function was supposed to calculate (a / b) mod p
      // but it doesn't return correct result
      a * extendedGCD(b, p)._1
    }

    val k = xs.length

    val (nums, dens) = Range(0, k)
      .map(i => {
        val others = xs.take(i) ++ xs.drop(i + 1)
        val cur = xs(i)
        val num = others.map(x - _).product
        val den = others.map(cur - _).product
        (num, den)
      }).unzip
    val den = dens.product
    val num = Range(0, k).map(i => divMod((nums(i) * den * ys(i)) mod p, dens(i), p)).sum

    divMod(num, den, p) mod p
  }
}
