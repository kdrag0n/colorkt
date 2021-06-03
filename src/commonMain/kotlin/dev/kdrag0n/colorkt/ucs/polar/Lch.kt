package dev.kdrag0n.colorkt.ucs.polar

import dev.kdrag0n.colorkt.util.toDegrees
import dev.kdrag0n.colorkt.util.toRadians
import dev.kdrag0n.colorkt.ucs.lab.Lab
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
interface Lch {
    /**
     * Perceived lightness component.
     */
    val L: Double

    /**
     * Chroma component (amount of color).
     */
    val C: Double

    /**
     * Hue angle component in degrees (which color, e.g. green/blue).
     */
    val h: Double

    companion object {
        internal fun Lab.toLch(): Triple<Double, Double, Double> {
            val hDeg = atan2(b, a).toDegrees()

            return Triple(
                L,
                sqrt(a.pow(2) + b.pow(2)),
                // Normalize the angle, as many will be negative
                if (hDeg < 0) hDeg + 360 else hDeg,
            )
        }

        internal fun Lch.toLab(): Triple<Double, Double, Double> {
            val hRad = h.toRadians()

            return Triple(
                L,
                C * cos(hRad),
                C * sin(hRad),
            )
        }
    }
}
