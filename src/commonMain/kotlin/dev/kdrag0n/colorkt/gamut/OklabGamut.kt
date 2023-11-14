package dev.kdrag0n.colorkt.gamut

import dev.kdrag0n.colorkt.rgb.LinearSrgb
import dev.kdrag0n.colorkt.ucs.lab.Oklab
import dev.kdrag0n.colorkt.util.math.cube
import dev.kdrag0n.colorkt.util.math.square
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic
import kotlin.jvm.JvmSynthetic
import kotlin.math.*

/**
 * sRGB gamut clipping using Oklab.
 *
 * Out-of-gamut colors are mapped using gamut intersection in a 2D plane, and hue is always preserved. Lightness and
 * chroma are changed depending on the clip method; see [ClipMethod] for details.
 *
 * [LchGamut] has the same goal, but this is a numerical solution created specifically for Oklab. As a result, this runs
 * faster and has more clip methods (based on the lightness of the maximum chroma in the hue plane), but is otherwise
 * the same.
 *
 * Ported from the original C++ implementation:
 *
 * Copyright (c) 2021 Björn Ottosson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 * @see <a href="https://bottosson.github.io/posts/gamutclipping/">sRGB gamut clipping</a>
 */
// Renaming variables hurts the readability of math code
@Suppress("LocalVariableName")
public object OklabGamut {
    private const val CLIP_EPSILON = 0.00001

    // Finds the maximum saturation possible for a given hue that fits in sRGB
    // Saturation here is defined as S = C/L
    // a and b must be normalized so a^2 + b^2 == 1
    private fun computeMaxSaturation(a: Double, b: Double): Double {
        // Max saturation will be when one of r, g or b goes below zero.

        // Select different coefficients depending on which component goes below zero first
        val coeff = when {
            -1.88170328 * a - 0.80936493 * b > 1 -> SaturationCoefficients.RED
            1.81444104 * a - 1.19445276 * b > 1 -> SaturationCoefficients.GREEN
            else -> SaturationCoefficients.BLUE
        }

        // Approximate max saturation using a polynomial:
        val S = coeff.k0 + coeff.k1 * a + coeff.k2 * b + coeff.k3 * a * a + coeff.k4 * a * b

        // Do one step Halley's method to get closer
        // this gives an error less than 10e6, except for some blue hues where the dS/dh is close to infinite
        // this should be sufficient for most applications, otherwise do two/three steps

        val k_l = +0.3963377774 * a + 0.2158037573 * b
        val k_m = -0.1055613458 * a - 0.0638541728 * b
        val k_s = -0.0894841775 * a - 1.2914855480 * b

        run {
            val l_ = 1 + S * k_l
            val m_ = 1 + S * k_m
            val s_ = 1 + S * k_s

            val l = cube(l_)
            val m = cube(m_)
            val s = cube(s_)

            val l_dS = 3 * k_l * square(l_)
            val m_dS = 3 * k_m * square(m_)
            val s_dS = 3 * k_s * square(s_)

            val l_dS2 = 6 * square(k_l) * l_
            val m_dS2 = 6 * square(k_m) * m_
            val s_dS2 = 6 * square(k_s) * s_

            val f  = coeff.wl * l     + coeff.wm * m     + coeff.ws * s
            val f1 = coeff.wl * l_dS  + coeff.wm * m_dS  + coeff.ws * s_dS
            val f2 = coeff.wl * l_dS2 + coeff.wm * m_dS2 + coeff.ws * s_dS2

            return S - f * f1 / (f1*f1 - 0.5 * f * f2)
        }
    }

    // finds L_cusp and C_cusp for a given hue
    // a and b must be normalized so a^2 + b^2 == 1
    private fun findCusp(a: Double, b: Double): LC {
        // First, find the maximum saturation (saturation S = C/L)
        val S_cusp = computeMaxSaturation(a, b)

        // Convert to linear sRGB to find the first point where at least one of r,g or b >= 1:
        val rgb_at_max = Oklab(1.0, S_cusp * a, S_cusp * b).toLinearSrgb()
        val L_cusp = cbrt(1.0 / max(max(rgb_at_max.r, rgb_at_max.g), rgb_at_max.b))
        val C_cusp = L_cusp * S_cusp

        return LC(L_cusp, C_cusp)
    }

    private fun halleyTerm(
        l: Double, m: Double, s: Double,
        ldt: Double, mdt: Double, sdt: Double,
        ldt2: Double, mdt2: Double, sdt2: Double,
        coeff1: Double, coeff2: Double, coeff3: Double,
    ): Double {
        val n = coeff1 * l + coeff2 * m + coeff3 * s - 1
        val n1 = coeff1 * ldt + coeff2 * mdt + coeff3 * sdt
        val n2 = coeff1 * ldt2 + coeff2 * mdt2 + coeff3 * sdt2

        val u_n = n1 / (n1 * n1 - 0.5 * n * n2)
        val t_n = -n * u_n

        return if (u_n >= 0) t_n else Double.MAX_VALUE
    }

    // Finds intersection of the line defined by
    // L = L0 * (1 - t) + t * L1
    // C = t * C1
    // a and b must be normalized so a^2 + b^2 == 1
    private fun findGamutIntersection(
        cusp: LC,
        a: Double, b: Double,
        l1: Double, c1: Double,
        l0: Double,
    ): Double {
        // Find the intersection for upper and lower half separately
        if (((l1 - l0) * cusp.C - (cusp.L - l0) * c1) <= 0) {
            // Lower half
            return cusp.C * l0 / (c1 * cusp.L + cusp.C * (l0 - l1))
        }

        // Upper half

        // First intersect with triangle
        val t = cusp.C * (l0 - 1) / (c1 * (cusp.L - 1) + cusp.C * (l0 - l1))

        // Then one step Halley's method
        run {
            val dL = l1 - l0
            val dC = c1

            val k_l = +0.3963377774 * a + 0.2158037573 * b
            val k_m = -0.1055613458 * a - 0.0638541728 * b
            val k_s = -0.0894841775 * a - 1.2914855480 * b

            val l_dt = dL + dC * k_l
            val m_dt = dL + dC * k_m
            val s_dt = dL + dC * k_s

            // If higher accuracy is required, 2 or 3 iterations of the following block can be used:
            run {
                val L = l0 * (1.0 - t) + t * l1
                val C = t * c1

                val l_ = L + C * k_l
                val m_ = L + C * k_m
                val s_ = L + C * k_s

                val l = cube(l_)
                val m = cube(m_)
                val s = cube(s_)

                val ldt = 3 * l_dt * square(l_)
                val mdt = 3 * m_dt * square(m_)
                val sdt = 3 * s_dt * square(s_)

                val ldt2 = 6 * square(l_dt) * l_
                val mdt2 = 6 * square(m_dt) * m_
                val sdt2 = 6 * square(s_dt) * s_

                val t_r = halleyTerm(
                    l, m, s, ldt, mdt, sdt, ldt2, mdt2, sdt2,
                    4.0767416621, -3.3077115913, 0.2309699292,
                )
                val t_g = halleyTerm(
                    l, m, s, ldt, mdt, sdt, ldt2, mdt2, sdt2,
                    -1.2681437731, 2.6097574011, -0.3413193965,
                )
                val t_b = halleyTerm(
                    l, m, s, ldt, mdt, sdt, ldt2, mdt2, sdt2,
                    -0.0041960863, -0.7034186147, 1.7076147010,
                )

                return t + min(t_r, min(t_g, t_b))
            }
        }
    }

    @JvmSynthetic
    internal fun calcAdaptiveMidL(L: Double, C: Double, alpha: Double): Double {
        val Ld = L - 0.5
        val e1 = 0.5 + abs(Ld) + alpha * C
        return 0.5*(1.0 + sign(Ld)*(e1 - sqrt(e1*e1 - 2.0 *abs(Ld))))
    }

    private fun clip(
        rgb: LinearSrgb,
        method: ClipMethod,
        alpha: Double,
        lab: Oklab,
    ): LinearSrgb {
        if (rgb.isInGamut()) {
            return rgb
        }

        val L = lab.L
        val C = max(CLIP_EPSILON, sqrt(lab.a * lab.a + lab.b * lab.b))
        val a_ = lab.a / C
        val b_ = lab.b / C

        val cusp = findCusp(a_, b_)

        val l0 = when (method) {
            // l0 = target L
            ClipMethod.PRESERVE_LIGHTNESS -> L.coerceIn(0.0, 1.0)

            // l0 = 0.5 (mid grayscale)
            ClipMethod.PROJECT_TO_MID -> 0.5
            // l0 = L_cusp
            ClipMethod.PROJECT_TO_LCUSP -> cusp.L

            // Adaptive l0 towards l0=0.5
            ClipMethod.ADAPTIVE_TOWARDS_MID -> calcAdaptiveMidL(L, C, alpha)
            // Adaptive l0 towards l0=L_cusp
            ClipMethod.ADAPTIVE_TOWARDS_LCUSP -> {
                val Ld = L - cusp.L
                val k = 2.0 * (if (Ld > 0) 1.0 - cusp.L else cusp.L)

                val e1 = 0.5*k + abs(Ld) + alpha * C/k
                cusp.L + 0.5 * (sign(Ld) * (e1 - sqrt(e1 * e1 - 2.0 * k * abs(Ld))))
            }
        }

        val t = findGamutIntersection(cusp, a_, b_, L, C, l0)
        val L_clipped = l0 * (1 - t) + t * L
        val C_clipped = t * C

        return Oklab(L_clipped, C_clipped * a_, C_clipped * b_).toLinearSrgb()
    }

    /**
     * Gamut clipping method to use.
     * Hue is always preserved, regardless of the method chosen here.
     */
    public enum class ClipMethod {
        /**
         * Preserve the target lightness (e.g. for contrast) by reducing chroma.
         */
        PRESERVE_LIGHTNESS,

        /**
         * Project towards neutral 50% gray.
         */
        PROJECT_TO_MID,
        /**
         * Project towards neutral gray at the lightness with the most possible chroma for this hue.
         */
        PROJECT_TO_LCUSP,

        /**
         * A mix of lightness-preserving chroma reduction and projecting towards neutral 50% gray.
         */
        ADAPTIVE_TOWARDS_MID,
        /**
         * A mix of lightness-preserving chroma reduction and projecting towards neutral gray with max chroma.
         */
        ADAPTIVE_TOWARDS_LCUSP,
    }

    private data class LC(
        val L: Double,
        val C: Double,
    )

    private enum class SaturationCoefficients(
        val k0: Double,
        val k1: Double,
        val k2: Double,
        val k3: Double,
        val k4: Double,
        val wl: Double,
        val wm: Double,
        val ws: Double,
    ) {
        RED(
            k0 = +1.19086277,
            k1 = +1.76576728,
            k2 = +0.59662641,
            k3 = +0.75515197,
            k4 = +0.56771245,
            wl = +4.0767416621,
            wm = -3.3077115913,
            ws = +0.2309699292,
        ),

        GREEN(
            k0 = +0.73956515,
            k1 = -0.45954404,
            k2 = +0.08285427,
            k3 = +0.12541070,
            k4 = +0.14503204,
            wl = -1.2681437731,
            wm = +2.6097574011,
            ws = -0.3413193965,
        ),

        BLUE(
            k0 = +1.35733652,
            k1 = -0.00915799,
            k2 = -1.15130210,
            k3 = -0.50559606,
            k4 = +0.00692167,
            wl = -0.0041960863,
            wm = -0.7034186147,
            ws = +1.7076147010,
        ),
    }

    /**
     * Convert this Oklab color to linear sRGB, and clip it to sRGB gamut boundaries if it's not already within gamut.
     *
     * Out-of-gamut colors are mapped using gamut intersection in a 2D plane, and hue is always preserved. Lightness and
     * chroma are changed depending on the clip method; see [ClipMethod] for details.
     *
     * @see <a href="https://bottosson.github.io/posts/gamutclipping/">sRGB gamut clipping</a>
     * @return clipped color in linear sRGB
     */
    @JvmStatic
    @JvmOverloads
    public fun Oklab.clipToLinearSrgb(
        /**
         * Gamut clipping method to use. Different methods preserve different attributes and make different trade-offs.
         * @see [ClipMethod]
         */
        method: ClipMethod = ClipMethod.PRESERVE_LIGHTNESS,
        /**
         * For adaptive clipping methods only: the extent to which lightness should be preserved rather than chroma.
         * Larger numbers will preserve chroma more than lightness, and vice versa.
         *
         * This value is ignored when using other (non-adaptive) clipping methods.
         */
        alpha: Double = 0.05,
    ): LinearSrgb = clip(
        rgb = toLinearSrgb(),
        method = method,
        alpha = alpha,
        lab = this,
    )
}
