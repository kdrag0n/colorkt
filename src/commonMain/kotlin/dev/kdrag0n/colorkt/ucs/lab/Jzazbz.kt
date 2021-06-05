package dev.kdrag0n.colorkt.ucs.lab

import dev.kdrag0n.colorkt.tristimulus.CieXyz
import kotlin.math.pow

/**
 * A color in the Jzazbz uniform color space, which represents colors in [dev.kdrag0n.colorkt.ucs.lab.Lab] form.
 * This color space is designed for HDR use cases, but it also works reasonably well for LDR (e.g. sRGB).
 *
 * Note that this implementation maps sRGB white to an absolute luminance of 100 cd/m².
 * CIE 1931 XYZ is used as the intermediate color space.
 *
 * @see <a href="https://doi.org/10.1364/OE.25.015131">Perceptually uniform color space for image signals including high dynamic range and wide gamut</a>
 */
data class Jzazbz(
    override val L: Double,
    override val a: Double,
    override val b: Double,
) : Lab {
    override fun toLinearSrgb() = toCieXyz().toLinearSrgb()

    /**
     * Convert this color to the CIE 1931 XYZ color space.
     *
     * @see dev.kdrag0n.colorkt.tristimulus.CieXyz
     * @return Color in CIE 1931 XYZ
     */
    fun toCieXyz(): CieXyz {
        val jz = L + 1.6295499532821566e-11
        val iz = jz / (0.44 + 0.56*jz)

        val l = pqInv(iz + 1.386050432715393e-1*a + 5.804731615611869e-2*b)
        val m = pqInv(iz - 1.386050432715393e-1*a - 5.804731615611891e-2*b)
        val s = pqInv(iz - 9.601924202631895e-2*a - 8.118918960560390e-1*b)

        return CieXyz(
            +1.661373055774069e+00 * l - 9.145230923250668e-01 * m + 2.313620767186147e-01 * s,
            -3.250758740427037e-01 * l + 1.571847038366936e+00 * m - 2.182538318672940e-01 * s,
            -9.098281098284756e-02 * l - 3.127282905230740e-01 * m + 1.522766561305260e+00 * s,
        )
    }

    companion object {
        // Perceptual Quantizer transfer function
        private fun pq(x: Double): Double {
            val xp = (x * 1e-4).pow(0.1593017578125)
            return ((0.8359375 + 18.8515625 * xp) / (1 + 18.6875 * xp)).pow(134.034375)
        }

        // Inverse PQ transfer function
        private fun pqInv(x: Double): Double {
            val xp = x.pow(7.460772656268214e-03)
            return 1e4 * ((0.8359375 - xp) / (18.6875 * xp - 18.8515625)).pow(6.277394636015326)
        }

        // Individual steps of conversion to share with ZCAM
        internal fun xyzToLmsp(x: Double, y: Double, z: Double): DoubleArray {
            val lp = pq(0.674207838*x + 0.382799340*y - 0.047570458*z)
            val mp = pq(0.149284160*x + 0.739628340*y + 0.083327300*z)
            val sp = pq(0.070941080*x + 0.174768000*y + 0.670970020*z)

            return doubleArrayOf(lp, mp, sp)
        }

        internal fun lmspToIzazbz(lp: Double, mp: Double, sp: Double): DoubleArray {
            val iz = 0.5 * (lp + mp)
            val az = 3.524000*lp - 4.066708*mp + 0.542708*sp
            val bz = 0.199076*lp + 1.096799*mp - 1.295875*sp

            return doubleArrayOf(iz, az, bz)
        }

        /**
         * Convert this color to the Jzazbz uniform color space.
         *
         * @see dev.kdrag0n.colorkt.ucs.lab.Jzazbz
         * @return Color in Jzazbz UCS
         */
        fun CieXyz.toJzazbz(): Jzazbz {
            val (lp, mp, sp) = xyzToLmsp(x, y, z)
            val (iz, az, bz) = lmspToIzazbz(lp, mp, sp)
            val jz = (0.44 * iz) / (1 - 0.56*iz) - 1.6295499532821566e-11

            return Jzazbz(
                L = jz,
                a = az,
                b = bz,
            )
        }
    }
}
