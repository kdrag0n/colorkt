package dev.kdrag0n.colorkt.rgb

import dev.kdrag0n.colorkt.Color

/**
 * Cylindrical representation of [dev.kdrag0n.colorkt.rgb.Srgb] with the following 3 components:
 *   - H: hue
 *   - S: saturation
 *   - L: lightness
 *
 * All components are in the set [0, 1].
 * Note that HSL does not account for human color perception, i.e. changing saturation will affect perceived lightness.
 * For perceptual uniformity, consider a uniform color space: [dev.kdrag0n.colorkt.ucs.lab.Lab]
 *
 * @see <a href="https://en.wikipedia.org/wiki/HSL_and_HSV">Wikipedia</a>
 */
data class Hsl(
    /**
     * Hue component between 0 and 1.
     */
    val h: Double,

    /**
     * Saturation component between 0 and 1.
     */
    val s: Double,

    /**
     * Lightness component between 0 and 1.
     */
    val l: Double,
) : Color {
    override fun toLinearSrgb() = toSrgb().toLinearSrgb()

    /**
     * Convert this color to the standard RGB representation of sRGB.
     *
     * @see dev.kdrag0n.colorkt.rgb.Srgb
     * @return Color in standard sRGB
     */
    fun toSrgb(): Srgb {
        if (s == 0.0) {
            return Srgb(l, l, l)
        }

        val q = if (l < 0.5) l * (1 + s) else l + s - l * s
        val p = 2 * l - q

        return Srgb(
            hueToRgb(p, q, h + 1.0 / 3.0),
            hueToRgb(p, q, h),
            hueToRgb(p, q, h - 1.0 / 3.0),
        )
    }

    companion object {
        private fun hueToRgb(p: Double, q: Double, t: Double): Double {
            val tp = when {
                t < 0 -> t + 1
                t > 1 -> t - 1
                else -> t
            }

            return when {
                tp < 1.0 / 6.0 -> p + (q - p) * 6 * tp
                tp < 1.0 / 2.0 -> q
                tp < 2.0 / 3.0 -> p + (q - p) * (2.0 / 3.0 - tp) * 6
                else -> p
            }
        }

        /**
         * Convert this color to the cylindrical (HSL) representation of sRGB.
         *
         * @see dev.kdrag0n.colorkt.rgb.Hsl
         * @return Color in HSL representation
         */
        fun Srgb.toHsl(): Hsl {
            val max = maxOf(r, g, b)
            val min = minOf(r, g, b)
            val l = (max + min) / 2.0

            val (h, s) = if (min == max) {
                0.0 to 0.0
            } else {
                val d = max - min
                val s = if (l > 0.5) {
                    d / (2 - max - min)
                } else {
                    d / (max + min)
                }

                val h = when (max) {
                    r -> (g - b) / d + (if (g < b) 6 else 0)
                    g -> (b - r) / d + 2
                    b -> (r - g) / d + 4
                    else -> error("Invalid max value")
                } / 6.0

                h to s
            }

            return Hsl(h, s, l)
        }
    }
}
