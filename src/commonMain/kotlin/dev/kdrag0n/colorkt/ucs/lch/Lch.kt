package dev.kdrag0n.colorkt.ucs.lch

import dev.kdrag0n.colorkt.Color
import dev.kdrag0n.colorkt.util.math.toDegrees
import dev.kdrag0n.colorkt.util.math.toRadians
import dev.kdrag0n.colorkt.ucs.lab.Lab
import dev.kdrag0n.colorkt.util.math.square
import kotlin.jvm.JvmSynthetic
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Common interface for the polar representation of [dev.kdrag0n.colorkt.ucs.lab.Lab] color spaces.
 *
 * This represents Lab colors with the following 3 components:
 *   - L: perceived lightness
 *   - C: chroma (amount of color)
 *   - h: hue angle, in degrees (which color, e.g. green/blue)
 *
 * @see dev.kdrag0n.colorkt.ucs.lab.Lab
 */
public interface Lch : Color {
    /**
     * Perceived lightness component.
     */
    public val lightness: Double

    /**
     * Chroma component (amount of color).
     */
    public val chroma: Double

    /**
     * Hue angle component in degrees (which color, e.g. green/blue).
     */
    public val hue: Double
}

@JvmSynthetic
internal fun Lab.calcLchC() = sqrt(square(a) + square(b))
@JvmSynthetic
internal fun Lab.calcLchH(): Double {
    val hDeg = atan2(b, a).toDegrees()
    return if (hDeg < 0) hDeg + 360 else hDeg
}

@JvmSynthetic
internal fun Lch.calcLabA() = chroma * cos(hue.toRadians())
@JvmSynthetic
internal fun Lch.calcLabB() = chroma * sin(hue.toRadians())
