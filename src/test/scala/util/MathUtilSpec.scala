package util

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import util.MathUtil

class MathUtilSpec extends AnyFlatSpec with should.Matchers
{
    behavior of "LCM"

    it should "return correct result" in {
        MathUtil.lcm(60, 24) shouldBe 120
        MathUtil.lcm(5, 7) shouldBe 35
        MathUtil.lcm(3, 27) shouldBe 27
    }

    behavior of "extended GCD"

    it should "return correct result" in {
        MathUtil.extendedGCD(240, 46) shouldBe (-9, 47)
        MathUtil.extendedGCD(81, 57) shouldBe (-7, 10)
        MathUtil.extendedGCD(2, 13) shouldBe (-6, 1)
    }

    behavior of "lagrange interpolation with modulo"

    it should "return correct result" in {
        MathUtil.lagrangeModulo(0, List(1, 2, 3), List(8, 7, 10), 17) shouldBe 13
        MathUtil.lagrangeModulo(0, List(1, 2, 3), List(7426, 9351, 7009), 13441) shouldBe 1234
        MathUtil.lagrangeModulo(0, List(1, 4, 6), List(16610, 114379, 36849), 123829) shouldBe 1234
    }
}