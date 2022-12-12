import org.bouncycastle.crypto.engines.DESEngine
import org.bouncycastle.crypto.modes.*
import org.bouncycastle.crypto.params.KeyParameter
import org.bouncycastle.jce.provider.BouncyCastleProvider

import java.security.spec.KeySpec
import java.security.{SecureRandom, Security}
import java.util.Base64
import javax.crypto.spec.{IvParameterSpec, PBEKeySpec, SecretKeySpec}
import javax.crypto.{Cipher, KeyGenerator, SecretKey, SecretKeyFactory}
import scala.util.Try

object SecretSharing {

  val defaultSalt: String = "someSalt"

  def encode(data: Array[Byte]): String = {
    Base64.getEncoder.encodeToString(data)
  }

  def decode(data: String): Array[Byte] = {
    Base64.getDecoder.decode(data)
  }

  def generateSecretKey(password: String): SecretKeySpec = {
    val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
    val spec = PBEKeySpec(password.toCharArray, defaultSalt.getBytes, 65536, 256)
    val secret = factory.generateSecret(spec)
    val keySecret = SecretKeySpec(secret.getEncoded, "AES")
    keySecret
  }

  def encryptStr(n: Int, t: Int, data: String): (String, String, Seq[(BigInt, BigInt)]) = {
    encrypt(n, t, data.getBytes)
  }

  def encrypt(n: Int, t: Int, data: Array[Byte]): (String, String, Seq[(BigInt, BigInt)]) = {
    Security.addProvider(new BouncyCastleProvider)

    val scheme = Shamir()

    val (secretPwd, shares) = scheme.createSecretAndShares(n, t)

    val keySecret = generateSecretKey(secretPwd.toString)

    val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding", "BC")
    cipher.init(Cipher.ENCRYPT_MODE, keySecret)

    val iv = cipher.getParameters.getParameterSpec(classOf[IvParameterSpec]).getIV
    val cipherText = cipher.doFinal(data)

    (encode(cipherText), encode(iv), shares)
  }

  def decryptStr(cipherText: String, iv: String, shares: Seq[(BigInt, BigInt)]): Try[String] = {
    decrypt(cipherText, iv, shares).map(String(_))
  }

  def decrypt(cipherText: String, iv: String, shares: Seq[(BigInt, BigInt)]): Try[Array[Byte]] = {
    Security.addProvider(new BouncyCastleProvider)

    val scheme = Shamir()
    val secretPwd = scheme.decrypt(shares)

    val keySecret = generateSecretKey(secretPwd.toString)
    val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding", "BC")
    cipher.init(Cipher.DECRYPT_MODE, keySecret, new IvParameterSpec(decode(iv)))

    Try(cipher.doFinal(decode(cipherText)))
  }
}
