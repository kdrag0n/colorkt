// These simple math functions should always be inlined for performance
@file:Suppress("NOTHING_TO_INLINE")

package dev.kdrag0n.colorkt.util.math

import kotlin.math.PI
import kotlin.math.pow

internal inline fun cube(x: Double) = x * x * x
internal inline fun square(x: Double) = x * x

internal expect fun cbrt(x: Double): Double

internal fun Double.toRadians() = this * PI / 180.0
internal fun Double.toDegrees() = this * 180.0 / PI
