package dev.kdrag0n.colorkt.cam

import dev.kdrag0n.colorkt.Color
import dev.kdrag0n.colorkt.tristimulus.CieXyzAbs
import dev.kdrag0n.colorkt.ucs.lch.Lch
import dev.kdrag0n.colorkt.util.math.square
import dev.kdrag0n.colorkt.util.math.toDegrees
import dev.kdrag0n.colorkt.util.math.toRadians
import kotlin.jvm.*
import kotlin.math.*

/**
 * A color modeled by the ZCAM color appearance model, which provides a variety of perceptual color attributes.
 * This color appearance model is designed with HDR in mind so it only accepts *absolute* CIE XYZ values scaled by the
 * absolute luminance of the modeled display, unlike SDR color spaces that accept relative luminance.
 *
 * Most attributes are optional in the constructor because don't need to be present together. All ZCAM colors must have:
 *     - brightness OR lightness
 *     - colorfulness OR chroma OR saturation OR vividness OR blackness OR whiteness
 *     - hue
 *     - viewing conditions
 *
 * @see <a href="https://www.osapublishing.org/oe/viewmedia.cfm?uri=oe-29-4-6036&html=true">ZCAM, a colour appearance model based on a high dynamic range uniform colour space</a>
 */
// Math code looks better with underscores, and we want to match the paper
@Suppress("LocalVariableName", "PrivatePropertyName", "PropertyName")
public data class Zcam(
    // 1D
    /** Absolute brightness. **/
    val brightness: Double = Double.NaN,
    /** Brightness relative to the reference white, from 0 to 100. **/
    override val lightness: Double = Double.NaN,
    /** Absolute colorfulness. **/
    val colorfulness: Double = Double.NaN,
    /** Colorfulness relative to the reference white. **/
    override val chroma: Double = Double.NaN,
    /** Hue from 0 to 360 degrees. **/
    override val hue: Double,
    /* hue composition is not supported */

    // 2D
    /** Chroma relative to lightness. 2D attribute. **/
    val saturation: Double = Double.NaN,
    /** Distance from neutral black. 2D attribute. **/
    val vividness: Double = Double.NaN,
    /** Amount of black. 2D attribute. **/
    val blackness: Double = Double.NaN,
    /** Amount of white. 2D attribute. **/
    val whiteness: Double = Double.NaN,

    /** Viewing conditions used to model this color. **/
    val viewingConditions: ViewingConditions,
) : Color, Lch {
    // Aliases to match the paper
    /** Alias for [brightness]. **/
    val Qz: Double get() = brightness
    /** Alias for [lightness]. **/
    val Jz: Double get() = lightness
    /** Alias for [colorfulness]. **/
    val Mz: Double get() = colorfulness
    /** Alias for [chroma]. **/
    val Cz: Double get() = chroma
    /** Alias for [hue]. **/
    val hz: Double get() = hue
    /** Alias for [saturation]. **/
    val Sz: Double get() = saturation
    /** Alias for [vividness]. **/
    val Vz: Double get() = vividness
    /** Alias for [blackness]. **/
    val Kz: Double get() = blackness
    /** Alias for [whiteness]. **/
    val Wz: Double get() = whiteness

    /**
     * Convert this color to the CIE XYZ color space, with absolute luminance.
     *
     * @see dev.kdrag0n.colorkt.tristimulus.CieXyzAbs
     * @return Color in absolute XYZ
     */
    public fun toXyzAbs(
        luminanceSource: LuminanceSource,
        chromaSource: ChromaSource,
    ): CieXyzAbs {
        val cond = viewingConditions
        val Qz_w = cond.Qz_w

        /* Step 1 */
        // Achromatic response
        val Iz = when (luminanceSource) {
            LuminanceSource.BRIGHTNESS -> Qz / cond.Iz_coeff
            LuminanceSource.LIGHTNESS -> (Jz * Qz_w) / (cond.Iz_coeff * 100.0)
        }.pow(cond.Qz_denom / (1.6 * cond.surroundFactor))

        /* Step 2 */
        // Chroma
        val Cz = when (chromaSource) {
            ChromaSource.CHROMA -> Cz
            ChromaSource.COLORFULNESS -> Double.NaN // not used
            ChromaSource.SATURATION -> (Qz * square(Sz)) / (100.0 * Qz_w * cond.Sz_denom)
            ChromaSource.VIVIDNESS -> sqrt((square(Vz) - square(Jz - 58)) / 3.4)
            ChromaSource.BLACKNESS -> sqrt((square((100 - Kz) / 0.8) - square(Jz)) / 8)
            ChromaSource.WHITENESS -> sqrt(square(100.0 - Wz) - square(100.0 - Jz))
        }

        /* Step 3 is missing because hue composition is not supported */

        /* Step 4 */
        // ... and back to colorfulness
        val Mz = when (chromaSource) {
            ChromaSource.COLORFULNESS -> Mz
            else -> (Cz * Qz_w) / 100
        }
        val ez = hpToEz(hz)
        val Cz_p = ((Mz * cond.Mz_denom) /
                // Paper specifies pow(1.3514) but this extra precision is necessary for accurate inversion
                (100.0 * ez.pow(0.068) * cond.ez_coeff)).pow(1.0 / 0.37 / 2)
        val hzRad = hz.toRadians()
        val az = Cz_p * cos(hzRad)
        val bz = Cz_p * sin(hzRad)

        /* Step 5 */
        val I = Iz + EPSILON

        val r = pq(I + 0.2772100865*az +  0.1160946323*bz)
        val g = pq(I)
        val b = pq(I + 0.0425858012*az + -0.7538445799*bz)

        val xp =  1.9242264358*r + -1.0047923126*g +  0.0376514040*b
        val yp =  0.3503167621*r +  0.7264811939*g + -0.0653844229*b
        val z  = -0.0909828110*r + -0.3127282905*g +  1.5227665613*b

        val x = (xp + (B - 1)*z) / B
        val y = (yp + (G - 1)*x) / G

        return CieXyzAbs(x, y, z)
    }

    /**
     * ZCAM attributes that can be used to calculate luminance in the inverse model.
     */
    public enum class LuminanceSource {
        /**
         * Use the brightness attribute to calculate luminance in the inverse model.
         * Lightness will be ignored.
         */
        BRIGHTNESS,
        /**
         * Use the lightness attribute to calculate luminance in the inverse model.
         * Brightness will be ignored.
         */
        LIGHTNESS,
    }

    /**
     * ZCAM attributes that can be used to calculate chroma (colorfulness) in the inverse model.
     */
    public enum class ChromaSource {
        /**
         * Use the chroma attribute to calculate luminance in the inverse model.
         * Colorfulness, saturation, vividness, blackness, and whiteness will be ignored.
         */
        CHROMA,
        /**
         * Use the colorfulness attribute to calculate luminance in the inverse model.
         * Chroma, saturation, vividness, blackness, and whiteness will be ignored.
         */
        COLORFULNESS,
        /**
         * Use the saturation attribute to calculate luminance in the inverse model.
         * Chroma, colorfulness, vividness, blackness, and whiteness will be ignored.
         */
        SATURATION,
        /**
         * Use the vividness attribute to calculate luminance in the inverse model.
         * Chroma, colorfulness, saturation, blackness, and whiteness will be ignored.
         */
        VIVIDNESS,
        /**
         * Use the blackness attribute to calculate luminance in the inverse model.
         * Chroma, colorfulness, saturation, vividness, and whiteness will be ignored.
         */
        BLACKNESS,
        /**
         * Use the whiteness attribute to calculate luminance in the inverse model.
         * Chroma, colorfulness, saturation, vividness, and blackness will be ignored.
         */
        WHITENESS,
    }

    /**
     * The conditions under which a color modeled by ZCAM will be viewed. This is defined by the luminance of the
     * adapting field, luminance of the background, and surround factor.
     *
     * For performance, viewing conditions should be created once and reused for all ZCAM conversions unless they have
     * changed. Creating an instance of ViewingConditions performs calculations that are reused throughout the ZCAM
     * model.
     */
    public data class ViewingConditions(
        /**
         * Surround factor, which models the surround field (distant background).
         */
        val surroundFactor: Double,

        /**
         * Absolute luminance of the adapting field. This can be calculated as L_w * [backgroundLuminance] / 100 where
         * L_w is the luminance of [referenceWhite], but it is a user-controlled parameter for flexibility.
         */
        val adaptingLuminance: Double,
        /**
         * Absolute luminance of the background.
         */
        val backgroundLuminance: Double,

        /**
         * Reference white point in absolute XYZ.
         */
        val referenceWhite: CieXyzAbs,
    ) {
        @JvmSynthetic @JvmField internal val Iz_coeff: Double
        @JvmSynthetic @JvmField internal val ez_coeff: Double
        @JvmSynthetic @JvmField internal val Qz_denom: Double
        @JvmSynthetic @JvmField internal val Sz_coeff: Double
        @JvmSynthetic @JvmField internal val Sz_denom: Double
        @JvmSynthetic @JvmField internal val Mz_denom: Double
        @JvmSynthetic @JvmField internal val Qz_w: Double

        init {
            val F_b = sqrt(backgroundLuminance / referenceWhite.y)
            val F_l = 0.171 * cbrt(adaptingLuminance) * (1.0 - exp(-48.0 / 9.0 * adaptingLuminance))

            Iz_coeff = 2700.0 * surroundFactor.pow(2.2) * sqrt(F_b) * F_l.pow(0.2)
            ez_coeff = F_l.pow(0.2)
            Qz_denom = F_b.pow(0.12)
            Sz_coeff = F_l.pow(0.6)
            Sz_denom = F_l.pow(1.2)

            val Iz_w = xyzToIzazbz(referenceWhite)[0]
            Mz_denom = Iz_w.pow(0.78) * F_b.pow(0.1)

            // Depends on coefficients computed above
            Qz_w = izToQz(Iz_w, this)
        }

        public companion object {
            /**
             * Surround factor for dark viewing conditions.
             */
            public const val SURROUND_DARK: Double = 0.525
            /**
             * Surround factor for dim viewing conditions.
             */
            public const val SURROUND_DIM: Double = 0.59
            /**
             * Surround factor for average viewing conditions.
             */
            public const val SURROUND_AVERAGE: Double = 0.69
        }
    }

    public companion object {
        // Constants
        private const val B = 1.15
        private const val G = 0.66
        private const val C1 = 3424.0 / 4096
        private const val C2 = 2413.0 / 128
        private const val C3 = 2392.0 / 128
        private const val ETA = 2610.0 / 16384
        private const val RHO = 1.7 * 2523.0 / 32
        private const val EPSILON = 3.7035226210190005e-11

        // Transfer function and inverse
        private fun pq(x: Double): Double {
            val num = C1 - x.pow(1.0/RHO)
            val denom = C3*x.pow(1.0/RHO) - C2

            return 10000.0 * (num / denom).pow(1.0/ETA)
        }
        private fun pqInv(x: Double): Double {
            val num = C1 + C2*(x / 10000).pow(ETA)
            val denom = 1.0 + C3*(x / 10000).pow(ETA)

            return (num / denom).pow(RHO)
        }

        // Intermediate conversion, also used in ViewingConditions
        private fun xyzToIzazbz(xyz: CieXyzAbs): DoubleArray {
            // This equation (#4) is wrong in the paper; below is the correct version.
            // It can be derived from the inverse model (supplementary paper) or the original Jzazbz paper.
            val xp = B*xyz.x - (B-1)*xyz.z
            val yp = G*xyz.y - (G-1)*xyz.x

            val rp = pqInv(0.41478972*xp + 0.579999*yp + 0.0146480*xyz.z)
            val gp = pqInv(-0.2015100*xp + 1.120649*yp + 0.0531008*xyz.z)
            val bp = pqInv(-0.0166008*xp + 0.264800*yp + 0.6684799*xyz.z)

            val az = 3.524000*rp + -4.066708*gp +  0.542708*bp
            val bz = 0.199076*rp +  1.096799*gp + -1.295875*bp
            val Iz = gp - EPSILON

            return doubleArrayOf(Iz, az, bz)
        }

        // Shared between forward and inverse models
        private fun hpToEz(hp: Double) = 1.015 + cos((89.038 + hp).toRadians())
        private fun izToQz(Iz: Double, cond: ViewingConditions) =
            cond.Iz_coeff * Iz.pow((1.6 * cond.surroundFactor) / cond.Qz_denom)

        /**
         * Get the perceptual appearance attributes of this color using the [Zcam] color appearance model.
         * Input colors must be relative to a reference white of D65, absolute luminance notwithstanding.
         *
         * @return [Zcam] attributes
         */
        @JvmStatic
        @JvmOverloads
        @JvmName("fromXyzAbs")
        public fun CieXyzAbs.toZcam(
            /**
             * Conditions under which the color will be viewed.
             */
            cond: ViewingConditions,
            /**
             * Whether to calculate 2D color attributes (attributes that depend on the result of multiple 1D
             * attributes). This includes saturation (Sz), vividness (Vz), blackness (Kz), and whiteness (Wz).
             *
             * These attributes are unnecessary in most cases, so you can set this to false and speed up the
             * calculations.
             */
            include2D: Boolean = true,
        ): Zcam {
            /* Step 2 */
            // Raw responses (similar to Jzazbz)
            val (Iz, az, bz) = xyzToIzazbz(this)

            /* Step 3 */
            // Hue angle
            val hzRaw = atan2(bz, az).toDegrees()
            val hz = if (hzRaw < 0) hzRaw + 360 else hzRaw

            /* Step 4 */
            // Eccentricity factor
            val ez = hpToEz(hz)

            /* Step 5 */
            // Brightness
            val Qz = izToQz(Iz, cond)
            val Qz_w = cond.Qz_w

            // Lightness
            val Jz = 100.0 * (Qz / Qz_w)

            // Colorfulness
            val Mz = 100.0 * (square(az) + square(bz)).pow(0.37) *
                    ((ez.pow(0.068) * cond.ez_coeff) / cond.Mz_denom)

            // Chroma
            val Cz = 100.0 * (Mz / Qz_w)

            /* Step 6 */
            // Saturation
            val Sz = if (include2D) 100.0 * cond.Sz_coeff * sqrt(Mz / Qz) else Double.NaN

            // Vividness, blackness, whiteness
            val Vz = if (include2D) sqrt(square(Jz - 58) + 3.4 * square(Cz)) else Double.NaN
            val Kz = if (include2D) 100.0 - 0.8 * sqrt(square(Jz) + 8.0 * square(Cz)) else Double.NaN
            val Wz = if (include2D) 100.0 - sqrt(square(100.0 - Jz) + square(Cz)) else Double.NaN

            return Zcam(
                brightness = Qz,
                lightness = Jz,
                colorfulness = Mz,
                chroma = Cz,
                hue = hz,

                saturation = Sz,
                vividness = Vz,
                blackness = Kz,
                whiteness = Wz,

                viewingConditions = cond,
            )
        }
    }
}
