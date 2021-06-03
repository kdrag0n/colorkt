package dev.kdrag0n.colorkt.ucs.polar

import dev.kdrag0n.colorkt.ucs.polar.Lch.Companion.toLab
import dev.kdrag0n.colorkt.ucs.polar.Lch.Companion.toLch
import dev.kdrag0n.colorkt.ucs.lab.Oklab

/**
 * Polar (LCh) representation of [dev.kdrag0n.colorkt.ucs.lab.Oklab].
 *
 * @see dev.kdrag0n.colorkt.ucs.polar.Lch
 */
data class Oklch(
    override val L: Double,
    override val C: Double,
    override val h: Double = 0.0,
) : Lch {
    override fun toLinearSrgb() = toOklab().toLinearSrgb()

    /**
     * Convert this color to the Cartesian (Lab) representation of Oklab.
     *
     * @see dev.kdrag0n.colorkt.ucs.lab.Lab
     * @return Color in Oklab representation
     */
    fun toOklab(): Oklab {
        val (l, a, b) = toLab()
        return Oklab(l, a, b)
    }

    companion object {
        /**
         * Convert this color to the polar (LCh) representation of Oklab.
         *
         * @see dev.kdrag0n.colorkt.ucs.polar.Lch
         * @return Color in OkLCh representation
         */
        fun Oklab.toOklch(): Oklch {
            val (l, c, h) = toLch()
            return Oklch(l, c, h)
        }
    }
}
