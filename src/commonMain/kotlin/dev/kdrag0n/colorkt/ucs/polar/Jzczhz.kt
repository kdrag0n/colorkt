package dev.kdrag0n.colorkt.ucs.polar

import dev.kdrag0n.colorkt.ucs.polar.Lch.Companion.toLab
import dev.kdrag0n.colorkt.ucs.polar.Lch.Companion.toLch
import dev.kdrag0n.colorkt.ucs.lab.Jzazbz

/**
 * Polar (LCh) representation of [dev.kdrag0n.colorkt.ucs.lab.Jzazbz].
 *
 * @see dev.kdrag0n.colorkt.ucs.polar.Lch
 */
data class Jzczhz(
    override val L: Double,
    override val C: Double,
    override val h: Double = 0.0,
) : Lch {
    override fun toLinearSrgb() = toJzazbz().toLinearSrgb()

    /**
     * Convert this color to the Cartesian (Lab) representation of Jzazbz.
     *
     * @see dev.kdrag0n.colorkt.ucs.lab.Lab
     * @return Color in Jzazbz representation
     */
    fun toJzazbz(): Jzazbz {
        val (l, a, b) = toLab()
        return Jzazbz(l, a, b)
    }

    companion object {
        /**
         * Convert this color to the polar (LCh) representation of Jzazbz.
         *
         * @see dev.kdrag0n.colorkt.ucs.polar.Lch
         * @return Color in JzCzhz representation
         */
        fun Jzazbz.toJzczhz(): Jzczhz {
            val (l, c, h) = toLch()
            return Jzczhz(l, c, h)
        }
    }
}
