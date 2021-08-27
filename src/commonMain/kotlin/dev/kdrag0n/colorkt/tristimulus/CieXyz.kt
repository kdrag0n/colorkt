package dev.kdrag0n.colorkt.tristimulus

import dev.kdrag0n.colorkt.Color
import dev.kdrag0n.colorkt.rgb.LinearSrgb
import dev.kdrag0n.colorkt.util.conversion.ConversionGraph
import kotlin.jvm.JvmName
import kotlin.jvm.JvmStatic
import kotlin.jvm.JvmSynthetic

/**
 * A color in the CIE XYZ tristimulus color space.
 * This is often used as an intermediate color space for uniform color spaces and color appearance models.
 *
 * Note that this is *not* a uniform color space; see [dev.kdrag0n.colorkt.ucs.lab.Lab] for that.
 *
 * @see <a href="https://en.wikipedia.org/wiki/CIE_1931_color_space">Wikipedia</a>
 */
public data class CieXyz(
    /**
     * X component: mix of the non-negative CIE RGB curves.
     */
    val x: Double,

    /**
     * Y component: relative luminance.
     */
    val y: Double,

    /**
     * Z component: approximately equal to blue from CIE RGB.
     */
    val z: Double,
) : Color {
    /**
     * Convert this color to the linear sRGB color space.
     *
     * @see dev.kdrag0n.colorkt.rgb.LinearSrgb
     * @return Color in linear sRGB
     */
    public fun toLinearSrgb(): LinearSrgb {
        return LinearSrgb(
            r = +3.2404542 * x + -1.5371385 * y + -0.4985314 * z,
            g = -0.9692660 * x + +1.8760108 * y + +0.0415560 * z,
            b = +0.0556434 * x + -0.2040259 * y + +1.0572252 * z,
        )
    }

    public companion object {
        @JvmSynthetic
        internal fun register() {
            ConversionGraph.add<LinearSrgb, CieXyz> { it.toXyz() }
            ConversionGraph.add<CieXyz, LinearSrgb> { it.toLinearSrgb() }
        }

        /**
         * Convert a linear sRGB color (D65 white point) to the CIE XYZ color space.
         *
         * @return Color in XYZ
         */
        @JvmStatic
        @JvmName("fromLinearSrgb")
        public fun LinearSrgb.toXyz(): CieXyz {
            return CieXyz(
                x = 0.4124564 * r + 0.3575761 * g + 0.1804375 * b,
                y = 0.2126729 * r + 0.7151522 * g + 0.0721750 * b,
                z = 0.0193339 * r + 0.1191920 * g + 0.9503041 * b,
            )
        }
    }
}
