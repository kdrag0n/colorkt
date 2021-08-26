package dev.kdrag0n.colorkt.util.math

import kotlin.math.pow

internal actual fun cbrt(x: Double) = when {
    x > 0 -> x.pow(1.0 / 3.0)
    x < 0 -> -(-x).pow(1.0 / 3.0)
    else -> 0.0
}
