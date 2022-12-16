import util.Benchmark

object Experiment extends App {
  val t = 100000

  // benchmarking

  // Shamir's encrypt
  val shamirScheme = Shamir()
  val shamirEncrypt = Benchmark.times(t)((5, 3))(shamirScheme.createSecretAndShares)
  println(s"Shamir's encrypt: $shamirEncrypt")
  // Shamir's decrypt
  val shamirDecrypt = Benchmark.times(t)({
    val (secret, shares) = shamirScheme.createSecretAndShares(5, 3)
    shares.take(3)
  })(shamirScheme.decrypt)
  println(s"Shamir's decrypt: $shamirDecrypt")

  // Blakley's encrypt
  val blakleyScheme = Blakley()
  val blakleyEncrypt = Benchmark.times(t)((5, 3))(blakleyScheme.createSecretAndShares)
  println(s"Blakley's encrypt: $blakleyEncrypt")
  // Blakley's decrypt
  val blakleyDecrypt = Benchmark.times(t)({
    val (secret, shares) = blakleyScheme.createSecretAndShares(5, 3)
    shares.take(3)
  })(blakleyScheme.decrypt)
  println(s"Blakley's decrypt: $blakleyDecrypt")

  // CRT's encrypt
  val crtScheme = CRT()
  val crtEncrypt = Benchmark.times(t)((5, 3))(crtScheme.createSecretAndShares)
  println(s"CRT's encrypt: $crtEncrypt")
  // CRT's decrypt
  val crtDecrypt = Benchmark.times(t)({
    val (secret, shares) = crtScheme.createSecretAndShares(5, 3)
    shares.take(3)
  })(crtScheme.decrypt)
  println(s"CRT's decrypt: $crtDecrypt")
}
