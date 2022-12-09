import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

class CRTSpec extends AnyFlatSpec with should.Matchers {
  behavior of "CRT"

  it should "successfully create secret and shares" in {

    val scheme = CRT()
    val (secret, shares) = scheme.createSecretAndShares(5, 3)

    println(secret)
    println(shares)

    shares.length shouldBe 5
    shares.foreach((m, r) => {
      secret mod m shouldBe r
    })
  }

  it should "successfully decrypt shares" in {
    val scheme = CRT()

    val shares = Seq((3, 2), (5, 3), (7, 2)).map((x, y) => BigInt(x) -> BigInt(y))
    scheme.decrypt(shares) shouldBe 23

    val shares2 = Seq((4, 1), (5, 2), (11, 7)).map((x, y) => BigInt(x) -> BigInt(y))
    scheme.decrypt(shares2) shouldBe 117
  }

  it should "successfully create and decrypt shares" in {
    val scheme = CRT()

    val (secret, shares) = scheme.createSecretAndShares(7, 3)

    shares.length shouldBe 7
    shares.foreach((m, r) => {
      secret mod m shouldBe r
    })

    // successfully decrypt
    scheme.decrypt(shares.take(3)) shouldBe secret
    scheme.decrypt(shares.takeRight(3)) shouldBe secret
    scheme.decrypt(List(1, 4, 5).map(shares)) shouldBe secret

    // insufficient shares
    scheme.decrypt(shares.take(2)) should not be secret
    scheme.decrypt(shares.takeRight(2)) should not be secret
  }
}
