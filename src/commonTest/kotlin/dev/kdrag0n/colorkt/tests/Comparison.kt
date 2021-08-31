package dev.kdrag0n.colorkt.tests

import kotlin.math.abs
import kotlin.test.assertTrue

private const val TEST_EPSILON = 0.001

fun Double.approx(x: Double, epsilon: Double = TEST_EPSILON) = abs(this - x) <= epsilon

fun assertApprox(actual: Double, expected: Double, comment: String? = null, epsilon: Double = TEST_EPSILON) {
    val msgBase = "Expected $expected, got $actual"
    val msg = if (comment != null) "$msgBase ($comment)" else msgBase
    assertTrue(actual.approx(expected, epsilon), msg)
}
