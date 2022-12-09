import breeze.linalg.{DenseMatrix, DenseVector}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

class BlakleyTest extends AnyFlatSpec with should.Matchers {

  behavior of "Blakley"

  it should "check if two planes are the same" in {
    val plane1 = Seq(1, 2, 3).map(BigInt(_))
    val plane2 = Seq(2, 4, 6).map(BigInt(_))

    Blakley.planeEq(plane1, plane2) shouldBe true

    val plane3 = Seq(1, 2, 4).map(BigInt(_))

    Blakley.planeEq(plane1, plane3) shouldBe false
  }

  it should "successfully create shares" in {
    val scheme = Blakley()

    val (secret, shares) = scheme.createSecretAndShares(5, 3)

    println(secret)
    println(shares)

    shares.length shouldBe 5
    shares.foreach(share => {
      share.length shouldBe 4
    })
  }

  it should "successfully decrypt shares" in {
    val scheme = Blakley()

    val shares = Seq(
      Seq(1, 1, 1, 18),
      Seq(1, 1, 2, 23),
      Seq(1, 2, 1, 28)
    ).map(_.map(BigInt(_)))

    scheme.decrypt(shares) shouldBe 3
  }

  it should "successfully encrypt and decrypt shares" in {
    val scheme = Blakley()

    val (secret, shares) = scheme.createSecretAndShares(6, 4)

    shares.length shouldBe 6
    shares.foreach(share => {
      share.length shouldBe 5
    })

    scheme.decrypt(shares.take(4)) shouldBe secret
    scheme.decrypt(shares.takeRight(4)) shouldBe secret
    scheme.decrypt(Seq(1, 2, 4, 5).map(shares)) shouldBe secret

    scheme.decrypt(shares.take(2)) should not be secret
  }
}
