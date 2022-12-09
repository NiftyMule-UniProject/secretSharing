trait SecretSharingScheme {

  type Share

  def createSecretAndShares(w: Int, t: Int): (BigInt, Seq[Share])

  def decrypt(shares: Seq[Share]): BigInt
}
