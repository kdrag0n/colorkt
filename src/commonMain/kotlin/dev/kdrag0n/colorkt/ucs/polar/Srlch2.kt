package dev.kdrag0n.colorkt.ucs.polar

import dev.kdrag0n.colorkt.core.Color
import dev.kdrag0n.colorkt.ucs.polar.Lch.Companion.toLab
import dev.kdrag0n.colorkt.ucs.polar.Lch.Companion.toLch
import dev.kdrag0n.colorkt.ucs.lab.Srlab2

/**
 * Polar (LCh) representation of [dev.kdrag0n.colorkt.ucs.lab.Srlab2].
 *
 * @see dev.kdrag0n.colorkt.ucs.polar.Lch
 */
data class Srlch2(
    override val L: Double,
    override val C: Double,
    override val h: Double,
) : Color, Lch {
    override fun toLinearSrgb() = toSrlab2().toLinearSrgb()

    /**
     * Convert this color to the Cartesian (Lab) representation of SRLAB2.
     *
     * @see dev.kdrag0n.colorkt.ucs.lab.Lab
     * @return Color in SRLAB2 representation
     */
    fun toSrlab2(): Srlab2 {
        val (l, a, b) = toLab()
        return Srlab2(l, a, b)
    }

    companion object {
        /**
         * Convert this color to the polar (LCh) representation of SRLAB2.
         *
         * @see dev.kdrag0n.colorkt.ucs.polar.Lch
         * @return Color in SRLCh2 representation
         */
        fun Srlab2.toSrlch2(): Srlch2 {
            val (l, c, h) = toLch()
            return Srlch2(l, c, h)
        }
    }
}
