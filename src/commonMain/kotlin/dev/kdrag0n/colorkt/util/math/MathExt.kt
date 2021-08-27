// These simple math functions should always be inlined for performance
@file:Suppress("NOTHING_TO_INLINE")

package dev.kdrag0n.colorkt.util.math

import kotlin.jvm.JvmSynthetic
import kotlin.math.PI

@JvmSynthetic
internal inline fun cube(x: Double) = x * x * x
@JvmSynthetic
internal inline fun square(x: Double) = x * x

// Use native cbrt where possible, otherwise simulate it with pow
@JvmSynthetic
internal expect fun cbrt(x: Double): Double

@JvmSynthetic
internal fun Double.toRadians() = this * PI / 180.0
@JvmSynthetic
internal fun Double.toDegrees() = this * 180.0 / PI
