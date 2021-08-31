package dev.kdrag0n.colorkt.tests

import kotlin.math.abs
import kotlin.test.assertTrue

private const val TEST_EPSILON = 0.001

infix fun Double.approx(x: Double) = abs(this - x) <= TEST_EPSILON

fun assertApprox(actual: Double, expected: Double, comment: String? = null) {
    val msgBase = "Expected $expected, got $actual"
    val msg = if (comment != null) "$msgBase ($comment)" else msgBase
    assertTrue(actual approx expected, msg)
}
