package dev.kdrag0n.colorkt.rgb

import dev.kdrag0n.colorkt.conversion.ConversionGraph
import kotlin.jvm.JvmName
import kotlin.jvm.JvmStatic
import kotlin.jvm.JvmSynthetic
import kotlin.math.pow

/**
 * Linear representation of [dev.kdrag0n.colorkt.rgb.Srgb].
 * This is useful as an intermediate color space for conversions.
 *
 * The sRGB non-linearity and its inverse are applied accurately, including the linear part of the piecewise function.
 *
 * @see <a href="https://en.wikipedia.org/wiki/SRGB">Wikipedia</a>
 */
public data class LinearSrgb(
    override val r: Double,
    override val g: Double,
    override val b: Double,
) : Rgb {
    /**
     * Convert this color to standard sRGB.
     * This delinearizes the sRGB components.
     *
     * @see dev.kdrag0n.colorkt.rgb.Srgb
     * @return Color in standard sRGB
     */
    public fun toSrgb(): Srgb {
        return Srgb(
            r = f(r),
            g = f(g),
            b = f(b),
        )
    }

    /**
     * Check whether this color is within the sRGB gamut.
     * This will return false if any component is either NaN or is not within the 0-1 range.
     *
     * @return true if color is in gamut, false otherwise
     */
    public fun isInGamut(): Boolean = r in 0.0..1.0 && g in 0.0..1.0 && b in 0.0..1.0

    public companion object {
        @JvmSynthetic
        internal fun register() {
            ConversionGraph.add<Srgb, LinearSrgb> { it.toLinear() }
            ConversionGraph.add<LinearSrgb, Srgb> { it.toSrgb() }
        }

        // Linear -> sRGB
        private fun f(x: Double) = if (x >= 0.0031308) {
            1.055 * x.pow(1.0 / 2.4) - 0.055
        } else {
            12.92 * x
        }

        // sRGB -> linear
        private fun fInv(x: Double) = if (x >= 0.04045) {
            ((x + 0.055) / 1.055).pow(2.4)
        } else {
            x / 12.92
        }

        /**
         * Convert this color to linear sRGB.
         * This linearizes the sRGB components.
         *
         * @see dev.kdrag0n.colorkt.rgb.LinearSrgb
         * @return Color in linear sRGB
         */
        @JvmStatic
        @JvmName("fromSrgb")
        public fun Srgb.toLinear(): LinearSrgb {
            return LinearSrgb(
                r = fInv(r),
                g = fInv(g),
                b = fInv(b),
            )
        }
    }
}
