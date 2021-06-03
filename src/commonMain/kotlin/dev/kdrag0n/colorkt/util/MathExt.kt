package dev.kdrag0n.colorkt.util

import kotlin.math.PI
import kotlin.math.pow

fun cbrt(x: Double) = when {
    x > 0 -> x.pow(1.0 / 3.0)
    x < 0 -> -(-x).pow(1.0 / 3.0)
    else -> 0.0
}

fun Double.toRadians() = this * PI / 180.0
fun Double.toDegrees() = this * 180.0 / PI
