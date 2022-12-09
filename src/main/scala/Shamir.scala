import scala.util.Random
import util.{Util, MathUtil}

case class Shamir(p: BigInt = (BigInt(1) << 127) - 1) extends SecretSharingScheme {
  override type Share = (BigInt, BigInt) // (i, y(i))

  val primeBits: Int = 50 // magic number... can be improved

  override def createSecretAndShares(w: Int, t: Int): (BigInt, Seq[Share]) = {
    val secret = BigInt.probablePrime(primeBits, Random)
    secret -> createSharesUsingSecret(w, t, secret)
  }

  def createSharesUsingSecret(w: Int, t: Int, secret: BigInt): Seq[Share] = {
    val co = Util.generateUniqueList(t - 1, () => BigInt.probablePrime(primeBits, Random))(_ == _)

    val ys: Seq[(BigInt, BigInt)] = Range.inclusive(1, w)
      .map(x => {
        // non-constant terms
        val tmp = Range(0, t - 1)
          .foldLeft[BigInt](0)((z, j) => z + co(j) * BigInt(x).pow(j + 1))

        // secret as constant terms
        BigInt(x) -> ((secret + tmp) mod p)
      })

    ys
  }

  override def decrypt(shares: Seq[(BigInt, BigInt)]): BigInt = {
    val (xs, ys) = shares.unzip

    // lagrange interpolation works in finite field
    MathUtil.lagrangeModulo(0, xs, ys, p)
  }
}