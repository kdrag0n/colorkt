package dev.kdrag0n.colorkt.tests

import dev.kdrag0n.colorkt.Color
import kotlin.math.abs
import kotlin.test.assertTrue

private const val TEST_EPSILON = 0.001

infix fun Double.approx(x: Double) = abs(this - x) <= TEST_EPSILON

fun assertApprox(actual: Double, expected: Double) {
    assertTrue(actual approx expected, "Expected $expected, got $actual")
}

private fun isApprox(actual: Color?, expected: Color?): Boolean {
    return when {
        actual == null || expected == null -> actual == expected
        actual::class != expected::class -> false
        else -> actual::class.qualifiedName
    }
}

fun assertApprox(actual: Color?, expected: Color?) {

}
