package dev.kdrag0n.colorkt.tests

import kotlin.math.abs
import kotlin.test.assertTrue

private const val TEST_EPSILON = 0.001

infix fun Double.approx(x: Double) = abs(this - x) <= TEST_EPSILON

fun assertApprox(actual: Double, expected: Double) {
    assertTrue(actual approx expected, "Expected $expected, got $actual")
}
