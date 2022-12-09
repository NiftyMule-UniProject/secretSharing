package util

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

import breeze.linalg.DenseVector

import util.MathUtil

import scala.util.Random

class UtilSpec extends AnyFlatSpec with should.Matchers
{
    behavior of "unique random generator"

    it should "generate unique integers" in {
        val gen = () => Random.nextInt(10)
        val nums = Util.generateUniqueList(10, gen)(_ == _)

        println(nums)
        nums.distinct.length shouldBe 10
    }

    it should "generate unique lines" in {
        val lineGen = () => Range(0, 3).map(_ => Random.nextInt(10)) // Ax + By + C = 0, generate A, B, C
        val eq = (x: Seq[Int], y: Seq[Int]) => {
            val factor = x.head.toFloat / y.head
            (x zip y)
              .forall((a, b) => {
                  a == b * factor
              })
        }
        val lines = Util.generateUniqueList(10, lineGen)(eq)

        println(lines)
        lines.distinct.length shouldBe 10
        lines.forall(a => lines.forall(x => !eq(a, x) || a == x))
    }
}