import util.{MathUtil, Util}

import scala.util.Random
import breeze.linalg.*

case class Blakley() extends SecretSharingScheme {
  override type Share = Seq[BigInt] // coefficients

  val pointCoordBits: Int = 20
  val coefficientBits: Int = 50

  override def createSecretAndShares(w: Int, t: Int): (BigInt, Seq[Share]) = {
    val point = Range(0, t)
      .map(_ => BigInt.probablePrime(pointCoordBits, Random))

    val secret = point(0)

    secret -> createSharesUsingPoint(w, t, point)
  }

  def createSharesUsingPoint(w: Int, t: Int, point: Seq[BigInt]): Seq[Share] = {
    val pointVec: Vector[BigInt] = Vector(point:_*)

    val planeGen = () => {
      Range(0, t).map(_ => BigInt.probablePrime(coefficientBits, Random))
    }

    val planes = Util.generateUniqueList(w, planeGen)(Blakley.planeEq)
      .map(coefficients => {
        // Ax + By + Cz + ... = constant
        val constant = Vector(coefficients:_*) dot pointVec

        coefficients :+ constant
      })

    planes
  }

  override def decrypt(shares: Seq[Share]): BigInt = {
    // TODO - change the trait to be option?
    if shares.head.length != shares.length + 1 then return -1

    val (planes, constants) = shares.map(share => {
      val plane = share.dropRight(1)
      val constant = share.takeRight(1)
      (plane, constant)
    }).unzip

    // breeze library limit: didn't implement solving algorithms for BigInt
    // TODO - TO BE IMPROVED !!! - precision lost
    val planesMatrix: DenseMatrix[Double] = DenseMatrix(planes.map(_.map(_.toDouble)):_*)
    val constantVec: DenseVector[Double] = DenseVector(constants.flatten.map(_.toDouble):_*)

    val point = planesMatrix \ constantVec

    Math.round(point(0))
  }
}

object Blakley {
  def planeEq(planeA: Seq[BigInt], planeB: Seq[BigInt]): Boolean = {
    val lcm = MathUtil.lcm(planeA.head, planeB.head)
    val factorA = lcm / planeA.head
    val factorB = lcm / planeB.head
    (planeA zip planeB)
      .forall((a, b) => {
        a * factorA == b * factorB 
          || a == b // to avoid singular matrix
      })
  }
}