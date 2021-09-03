package dev.kdrag0n.colorkt.tristimulus

import dev.kdrag0n.colorkt.Color
import dev.kdrag0n.colorkt.rgb.LinearSrgb
import dev.kdrag0n.colorkt.conversion.ConversionGraph
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
        // See LinearSrgb.toXyz for info about the source of this matrix.
        return LinearSrgb(
            r =  3.2404541621141045  * x + -1.5371385127977162 * y + -0.4985314095560159  * z,
            g = -0.969266030505187   * x +  1.8760108454466944 * y +  0.04155601753034983 * z,
            b =  0.05564343095911474 * x + -0.2040259135167538 * y +  1.0572251882231787  * z,
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
            // This matrix (along with the inverse above) has been optimized to minimize chroma in CIELCh
            // when converting neutral sRGB colors to CIELAB. The maximum chroma for sRGB neutral colors 0-255 is
            // 5.978733960281817e-14.
            //
            // Calculated with https://github.com/facelessuser/coloraide/blob/master/tools/calc_xyz_transform.py
            // Using D65 xy chromaticities from the sRGB spec: x = 0.3127, y = 0.3290
            // Always keep in sync with Illuminants.D65.
            return CieXyz(
                x = 0.41245643908969226  * r + 0.357576077643909   * g + 0.18043748326639897 * b,
                y = 0.21267285140562256  * r + 0.715152155287818   * g + 0.07217499330655959 * b,
                z = 0.019333895582329303 * r + 0.11919202588130297 * g + 0.950304078536368   * b,
            )
        }
    }
}
