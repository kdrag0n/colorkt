package dev.kdrag0n.colorkt.core.srgb

import dev.kdrag0n.colorkt.core.Color
import kotlin.math.pow

/**
 * Linear representation of [dev.kdrag0n.colorkt.core.srgb.Srgb].
 * This is useful as an intermediate color space for conversions.
 *
 * The sRGB non-linearity and its inverse are applied accurately, including the linear part of the piecewise function.
 *
 * @see <a href="https://en.wikipedia.org/wiki/SRGB">Wikipedia</a>
 */
data class LinearSrgb(
    /**
     * Red color component.
     */
    val r: Double,

    /**
     * Green color component.
     */
    val g: Double,

    /**
     * Blue color component.
     */
    val b: Double,
) : Color {
    override fun toLinearSrgb() = this

    /**
     * Convert this color to standard sRGB.
     * This delinearizes the sRGB components.
     *
     * @see dev.kdrag0n.colorkt.core.srgb.Srgb
     * @return Color in standard sRGB
     */
    fun toSrgb(): Srgb {
        return Srgb(
            r = oetf(r),
            g = oetf(g),
            b = oetf(b),
        )
    }

    companion object {
        // Opto-electrical transfer function
        // Forward transform to sRGB
        private fun oetf(x: Double): Double {
            return if (x >= 0.0031308) {
                1.055 * x.pow(1.0 / 2.4) - 0.055
            } else {
                12.92 * x
            }
        }

        // Electro-optical transfer function
        // Inverse transform to linear sRGB
        private fun eotf(x: Double): Double {
            return if (x >= 0.04045) {
                ((x + 0.055) / 1.055).pow(2.4)
            } else {
                x / 12.92
            }
        }

        /**
         * Convert this color to linear sRGB.
         * This linearizes the sRGB components.
         *
         * @see dev.kdrag0n.colorkt.core.srgb.LinearSrgb
         * @return Color in linear sRGB
         */
        fun Srgb.toLinearSrgb(): LinearSrgb {
            return LinearSrgb(
                r = eotf(r),
                g = eotf(g),
                b = eotf(b),
            )
        }
    }
}
