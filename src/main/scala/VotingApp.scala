import org.bouncycastle.crypto.engines.DESEngine
import org.bouncycastle.crypto.modes.*
import org.bouncycastle.crypto.params.KeyParameter
import org.bouncycastle.jce.provider.BouncyCastleProvider

import java.security.spec.KeySpec
import java.security.{SecureRandom, Security}
import javax.crypto.spec.{IvParameterSpec, PBEKeySpec, SecretKeySpec}
import javax.crypto.{Cipher, KeyGenerator, SecretKey, SecretKeyFactory}

object VotingApp extends App {

  Security.addProvider(new BouncyCastleProvider)

  // here we use secret to encrypt the voting system's DB key (using Shamir's scheme)
  val scheme = Shamir()

  val participants = 10
  val threshold = 5

  val (secretSalt, shares) = scheme.createSecretAndShares(participants, threshold)

  // key generator
  val keyGen = KeyGenerator.getInstance("DES", "BC")
  keyGen.init(SecureRandom())

  // generate DB key
  val dbKey = keyGen.generateKey()

  // create key from secretSalt generated by Shamir's scheme
  val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
  val spec = PBEKeySpec("password".toCharArray, secretSalt.toByteArray, 65536, 256)
  val tmp = factory.generateSecret(spec)
  val keySecret = SecretKeySpec(tmp.getEncoded, "AES")

  val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding", "BC")
  cipher.init(Cipher.ENCRYPT_MODE, keySecret)

  // Cipher IV configuration needs to be saved for further use
  val iv = cipher.getParameters.getParameterSpec(classOf[IvParameterSpec]).getIV
  val cipherText = cipher.doFinal(dbKey.getEncoded)

  // ...

  // reconstruct secret salt
  val reconstructedSecretSalt = scheme.decrypt(shares.takeRight(threshold))

  // construct new cipher (using same key and IV)
  val specNew = PBEKeySpec("password".toCharArray, reconstructedSecretSalt.toByteArray, 65536, 256)
  val tmpNew = factory.generateSecret(specNew)
  val keySecretNew = SecretKeySpec(tmpNew.getEncoded, "AES")
  assert(keySecret.getEncoded sameElements keySecretNew.getEncoded)

  val cipherNew = Cipher.getInstance("AES/CBC/PKCS5Padding", "BC")
  cipherNew.init(Cipher.DECRYPT_MODE, keySecretNew, new IvParameterSpec(iv))

  // decrypt the DB key
  val decrypted = cipherNew.doFinal(cipherText)

  assert(reconstructedSecretSalt == secretSalt)
  assert(decrypted sameElements dbKey.getEncoded)

  println("Successfully retrieved the DB key!")
}