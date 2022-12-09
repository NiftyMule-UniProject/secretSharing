import util.{MathUtil, Util}

import scala.util.Random

// CRT has some restriction that it cannot be used to encrypt an arbitrary number
// see: https://www.wikiwand.com/en/Secret_sharing_using_the_Chinese_remainder_theorem#Secret_sharing_using_the_CRT
case class CRT(primeBits: Int = 50) extends SecretSharingScheme {
  override type Share = (BigInt, BigInt)  // (modulus, remainder)

  override def createSecretAndShares(w: Int, t: Int): (BigInt, Seq[Share]) = {
    val primes = LazyList
      .continually(Util.generateUniqueList(w, () => BigInt.probablePrime(primeBits, Random))(_ == _))
      .filter(ps => {
        val sorted = ps.sorted
        sorted.take(t).product > sorted.takeRight(t - 1).product
      })
      .head

    val sorted = primes.sorted
    val max = sorted.take(t).product
    val min = sorted.takeRight(t - 1).product

    val secret = LazyList
      .continually(BigInt.probablePrime(primeBits * (t - 1) + 1, Random))
      .filter(x => {
        x > min && x < max
      })
      .head

    val shares = Range(0, w)
      .map(i => {
        (primes(i), secret mod primes(i))
      })

    secret -> shares
  }

  override def decrypt(shares: Seq[Share]): BigInt = {
    val (ms, as) = shares.unzip

    val M = ms.product
    Range(0, shares.length)
      .map(i => (MathUtil.extendedGCD(M / ms(i), ms(i))._1 * M / ms(i) * as(i)) mod M)
      .sum
      .mod(M)
  }
}
