package dev.kdrag0n.colorkt.ucs.lab

import dev.kdrag0n.colorkt.rgb.LinearSrgb
import dev.kdrag0n.colorkt.tristimulus.CieXyz
import dev.kdrag0n.colorkt.conversion.ConversionGraph
import dev.kdrag0n.colorkt.util.math.cbrt
import dev.kdrag0n.colorkt.util.math.cube
import kotlin.jvm.JvmName
import kotlin.jvm.JvmStatic
import kotlin.jvm.JvmSynthetic

/**
 * A color in the Oklab uniform color space, which represents colors in [dev.kdrag0n.colorkt.ucs.lab.Lab] form.
 * This color space is designed for overall uniformity and does not assume viewing conditions.
 *
 * Note that this implementation uses a white point of D65, like sRGB.
 * Linear sRGB is used as the intermediate color space.
 *
 * @see <a href="https://bottosson.github.io/posts/oklab/">A perceptual color space for image processing</a>
 */
public data class Oklab(
    override val L: Double,
    override val a: Double,
    override val b: Double,
) : Lab {
    /**
     * Convert this color to the linear sRGB color space.
     *
     * @see dev.kdrag0n.colorkt.rgb.LinearSrgb
     * @return Color in linear sRGB
     */
    public fun toLinearSrgb(): LinearSrgb {
        val l = labToL()
        val m = labToM()
        val s = labToS()

        return LinearSrgb(
            r = +4.0767416621 * l - 3.3077115913 * m + 0.2309699292 * s,
            g = -1.2684380046 * l + 2.6097574011 * m - 0.3413193965 * s,
            b = -0.0041960863 * l - 0.7034186147 * m + 1.7076147010 * s,
        )
    }
    /**
     * Convert this color to the CIE XYZ color space.
     *
     * @see dev.kdrag0n.colorkt.tristimulus.CieXyz
     * @return Color in XYZ
     */
    public fun toXyz(): CieXyz {
        val l = labToL()
        val m = labToM()
        val s = labToS()

        return CieXyz(
            x = +1.2270138511 * l - 0.5577999807 * m + 0.2812561490 * s,
            y = -0.0405801784 * l + 1.1122568696 * m - 0.0716766787 * s,
            z = -0.0763812845 * l - 0.4214819784 * m + 1.5861632204 * s,
        )
    }

    // Avoid arrays to minimize garbage
    private fun labToL() = cube(L + 0.3963377774 * a + 0.2158037573 * b)
    private fun labToM() = cube(L - 0.1055613458 * a - 0.0638541728 * b)
    private fun labToS() = cube(L - 0.0894841775 * a - 1.2914855480 * b)

    public companion object {
        @JvmSynthetic
        internal fun register() {
            ConversionGraph.add<LinearSrgb, Oklab> { it.toOklab() }
            ConversionGraph.add<Oklab, LinearSrgb> { it.toLinearSrgb() }

            ConversionGraph.add<CieXyz, Oklab> { it.toOklab() }
            ConversionGraph.add<Oklab, CieXyz> { it.toXyz() }
        }

        private fun lmsToOklab(l: Double, m: Double, s: Double): Oklab {
            val lp = cbrt(l)
            val mp = cbrt(m)
            val sp = cbrt(s)

            return Oklab(
                L = 0.2104542553 * lp + 0.7936177850 * mp - 0.0040720468 * sp,
                a = 1.9779984951 * lp - 2.4285922050 * mp + 0.4505937099 * sp,
                b = 0.0259040371 * lp + 0.7827717662 * mp - 0.8086757660 * sp,
            )
        }

        /**
         * Convert this color to the Oklab uniform color space.
         *
         * @see dev.kdrag0n.colorkt.ucs.lab.Oklab
         * @return Color in Oklab UCS
         */
        @JvmStatic
        @JvmName("fromLinearSrgb")
        public fun LinearSrgb.toOklab(): Oklab = lmsToOklab(
            l = 0.4122214708 * r + 0.5363325363 * g + 0.0514459929 * b,
            m = 0.2119034982 * r + 0.6806995451 * g + 0.1073969566 * b,
            s = 0.0883024619 * r + 0.2817188376 * g + 0.6299787005 * b,
        )

        /**
         * Convert this color to the Oklab uniform color space.
         *
         * @see dev.kdrag0n.colorkt.ucs.lab.Oklab
         * @return Color in Oklab UCS
         */
        @JvmStatic
        @JvmName("fromXyz")
        public fun CieXyz.toOklab(): Oklab = lmsToOklab(
            l = 0.8189330101 * x + 0.3618667424 * y - 0.1288597137 * z,
            m = 0.0329845436 * x + 0.9293118715 * y + 0.0361456387 * z,
            s = 0.0482003018 * x + 0.2643662691 * y + 0.6338517070 * z,
        )
    }
}
