package dev.kdrag0n.colorkt.ucs.lab

import dev.kdrag0n.colorkt.tristimulus.CieXyz
import dev.kdrag0n.colorkt.data.Illuminants
import dev.kdrag0n.colorkt.util.conversion.ConversionGraph
import dev.kdrag0n.colorkt.util.conversion.ConversionProvider
import dev.kdrag0n.colorkt.util.math.cbrt
import dev.kdrag0n.colorkt.util.math.cube

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
public data class CieLab(
    override val L: Double,
    override val a: Double,
    override val b: Double,
) : Lab {
    /**
     * Convert this color to the CIE 1931 XYZ color space.
     *
     * @see dev.kdrag0n.colorkt.tristimulus.CieXyz
     * @return Color in XYZ
     */
    public fun toXyz(): CieXyz {
        val lp = (L + 16.0) / 116.0

        return CieXyz(
            x = Illuminants.D65.x * fInv(lp + (a / 500.0)),
            y = Illuminants.D65.y * fInv(lp),
            z = Illuminants.D65.z * fInv(lp - (b / 200.0)),
        )
    }

    public companion object : ConversionProvider {
        override fun register() {
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
        public fun CieXyz.toCieLab(): CieLab {
            return CieLab(
                L = 116.0 * f(y / Illuminants.D65.y) - 16.0,
                a = 500.0 * (f(x / Illuminants.D65.x) - f(y / Illuminants.D65.y)),
                b = 200.0 * (f(y / Illuminants.D65.y) - f(z / Illuminants.D65.z)),
            )
        }
    }
}
