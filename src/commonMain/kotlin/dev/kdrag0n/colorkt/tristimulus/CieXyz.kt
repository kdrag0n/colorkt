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
            r =   3.2409699419045226 * x +   -1.537383177570094 * y + -0.4986107602930034 * z,
            g =  -0.9692436362808796 * x +   1.8759675015077202 * y + 0.04155505740717559 * z,
            b =  0.05563007969699366 * x + -0.20397695888897652 * y +  1.0569715142428786 * z,
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
                x = 0.41239079926595934 * r +   0.357584339383878 * g +  0.1804807884018343 * b,
                y = 0.21263900587151027 * r +   0.715168678767756 * g + 0.07219231536073371 * b,
                z = 0.01933081871559182 * r + 0.11919477979462598 * g +  0.9505321522496607 * b,
            )
        }
    }
}
