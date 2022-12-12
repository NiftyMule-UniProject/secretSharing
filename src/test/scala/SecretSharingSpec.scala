import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

import scala.util.{Failure, Success}

class SecretSharingSpec extends AnyFlatSpec with should.Matchers {
  behavior of "Shamir secret sharing scheme"

  it should "encrypt and decrypt messages" in {
    val (encrypted, iv, shares) = SecretSharing.encryptStr(5, 3, "hello world")

    SecretSharing.decryptStr(encrypted, iv, shares.take(3)) shouldBe Success("hello world")
    SecretSharing.decryptStr(encrypted, iv, shares.takeRight(3)) shouldBe Success("hello world")
  }

  it should "not decrypt message if given incorrect shares" in {
    val (encrypted, iv, shares) = SecretSharing.encryptStr(5, 3, "hello world")

    val changedThirdShare = shares(2).swap

    SecretSharing.decryptStr(encrypted, iv, shares.take(2)) shouldBe a [Failure[_]]
    SecretSharing.decryptStr(encrypted, iv, shares.take(2) :+ changedThirdShare) shouldBe a [Failure[_]]
  }
}
