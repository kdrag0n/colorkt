package dev.kdrag0n.colorkt.ucs.polar

import dev.kdrag0n.colorkt.Color
import dev.kdrag0n.colorkt.util.math.toDegrees
import dev.kdrag0n.colorkt.util.math.toRadians
import dev.kdrag0n.colorkt.ucs.lab.Lab
import dev.kdrag0n.colorkt.util.math.square
import kotlin.math.*

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
    public val L: Double

    /**
     * Chroma component (amount of color).
     */
    public val C: Double

    /**
     * Hue angle component in degrees (which color, e.g. green/blue).
     */
    public val h: Double

    public companion object {
        internal fun Lab.toLch(): DoubleArray {
            val hDeg = atan2(b, a).toDegrees()

            return doubleArrayOf(
                L,
                sqrt(square(a) + square(b)),
                // Normalize the angle, as many will be negative
                if (hDeg < 0) hDeg + 360 else hDeg,
            )
        }

        internal fun Lch.toLab(): DoubleArray {
            val hRad = h.toRadians()

            return doubleArrayOf(
                L,
                C * cos(hRad),
                C * sin(hRad),
            )
        }
    }
}
