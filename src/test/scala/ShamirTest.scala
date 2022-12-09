import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

class ShamirTest extends AnyFlatSpec with should.Matchers {

  behavior of "Shamir"

  it should "successfully create shares" in {
    val scheme = Shamir()

    val shares = scheme.createSharesUsingSecret(5, 3, 13)
    println(shares)

    scheme.p shouldBe (BigInt(1) << 127) - 1
    shares.length shouldBe 5

    val xs = shares.map(_._1)
    val ys = shares.map(_._2)
    xs shouldBe List(1, 2, 3, 4, 5).map(BigInt(_))
    ys.forall(_ < scheme.p) shouldBe true
  }

  it should "successfully decrypt shares" in {
    val scheme = Shamir(17)

    val shares = Seq(1, 2, 3).map(BigInt(_)) zip Seq(8, 7, 10).map(BigInt(_))
    scheme.decrypt(shares) shouldBe 13
  }

  it should "successfully encrypt and decrypt shares" in {
    val scheme = Shamir()

    val secret = 13

    val shares = scheme.createSharesUsingSecret(5, 3, secret)

    scheme.decrypt(shares.take(3)) shouldBe secret
    scheme.decrypt(shares.takeRight(3)) shouldBe secret
    scheme.decrypt(Seq(0, 2, 4).map(shares)) shouldBe secret

    scheme.decrypt(shares.take(2)) should not be secret
    scheme.decrypt(shares.takeRight(2)) should not be secret
  }

  it should "successfully create secret" in {
    val scheme = Shamir()

    val (secret, shares) = scheme.createSecretAndShares(7, 6)

    scheme.decrypt(shares.take(6)) shouldBe secret
    scheme.decrypt(shares.takeRight(6)) shouldBe secret

    scheme.decrypt(shares.take(2)) should not be secret
  }
}
