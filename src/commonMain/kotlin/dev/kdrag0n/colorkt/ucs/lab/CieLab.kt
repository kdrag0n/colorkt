package dev.kdrag0n.colorkt.ucs.lab

import dev.kdrag0n.colorkt.tristimulus.CieXyz
import dev.kdrag0n.colorkt.data.Illuminants
import dev.kdrag0n.colorkt.conversion.ConversionGraph
import dev.kdrag0n.colorkt.util.math.cbrt
import dev.kdrag0n.colorkt.util.math.cube
import kotlin.jvm.JvmName
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic
import kotlin.jvm.JvmSynthetic

/**
 * A color in the CIE L*a*b* uniform color space, which represents colors in [dev.kdrag0n.colorkt.ucs.lab.Lab] form.
 * This is the most well-known uniform color space, but more modern alternatives such as
 * [dev.kdrag0n.colorkt.ucs.lab.Oklab] tend to be more perceptually uniform.
 *
 * Note that this implementation uses a white point of D65, like sRGB.
 * It does not implement CIELAB D50.
 *
 * @see <a href="https://en.wikipedia.org/wiki/CIELAB_color_space">Wikipedia</a>
 */
public data class CieLab @JvmOverloads constructor(
    override val L: Double,
    override val a: Double,
    override val b: Double,

    /**
     * Reference white for CIELAB calculations. This affects the converted color.
     */
    val referenceWhite: CieXyz = Illuminants.D65,
) : Lab {
    /**
     * Convert this color to the CIE XYZ color space.
     *
     * @see dev.kdrag0n.colorkt.tristimulus.CieXyz
     * @return Color in XYZ
     */
    public fun toXyz(): CieXyz {
        val lp = (L + 16.0) / 116.0

        return CieXyz(
            x = referenceWhite.x * fInv(lp + (a / 500.0)),
            y = referenceWhite.y * fInv(lp),
            z = referenceWhite.z * fInv(lp - (b / 200.0)),
        )
    }

    public companion object {
        @JvmSynthetic
        internal fun register() {
            ConversionGraph.add<CieXyz, CieLab> { it.toCieLab() }
            ConversionGraph.add<CieLab, CieXyz> { it.toXyz() }
        }

        private fun f(x: Double) = if (x > 216.0/24389.0) {
            cbrt(x)
        } else {
            x / (108.0/841.0) + 4.0/29.0
        }

        private fun fInv(x: Double) = if (x > 6.0/29.0) {
            cube(x)
        } else {
            (108.0/841.0) * (x - 4.0/29.0)
        }

        /**
         * Convert this color to the CIE L*a*b* uniform color space.
         *
         * @see dev.kdrag0n.colorkt.ucs.lab.CieLab
         * @return Color in CIE L*a*b* UCS
         */
        @JvmStatic
        @JvmOverloads
        @JvmName("fromXyz")
        public fun CieXyz.toCieLab(refWhite: CieXyz = Illuminants.D65): CieLab {
            return CieLab(
                L = 116.0 * f(y / refWhite.y) - 16.0,
                a = 500.0 * (f(x / refWhite.x) - f(y / refWhite.y)),
                b = 200.0 * (f(y / refWhite.y) - f(z / refWhite.z)),
                referenceWhite = refWhite,
            )
        }
    }
}
