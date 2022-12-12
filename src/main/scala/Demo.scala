import scala.util.{Failure, Success}

object Demo extends App {
  // to encrypt and generate shares
  val msg = "What can change the nature of a man? [Planescape: Torment]"
  val (encrypted, iv, shares) = SecretSharing.encryptStr(3, 2, msg)
  println(encrypted)
  println(iv)
  println(shares)
  println("***************************************")

  // to decrypt
  val sharesToBeUse = List(
    (1, "1425810230568402"),
    (2, "2198518825940441")
  ).map(x => BigInt(x._1) -> BigInt(x._2))

  val ivToBeUse = "uz1bQoQJnNVVRRH8egfnzA=="
  val encryptedMsg = "Xi3TFh5/ALF7n/awMqRoqO/WbsVQHo/mdMJsJI4PWvZOcOKb86gDDRvRaQQMQPjRGGm3vMh7Ljw9h8D/tOqJkg=="

  val decrypted = SecretSharing.decryptStr(encryptedMsg, ivToBeUse, sharesToBeUse)
  decrypted match
    case Success(msg) => println(msg)
    case Failure(e) => println("Cannot decrypt message! Please check your shares again")
}
