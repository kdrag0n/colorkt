package dev.kdrag0n.colorkt.ucs.polar

import dev.kdrag0n.colorkt.ucs.polar.Lch.Companion.toLab
import dev.kdrag0n.colorkt.ucs.polar.Lch.Companion.toLch
import dev.kdrag0n.colorkt.ucs.lab.CieLab
import dev.kdrag0n.colorkt.util.ConversionGraph
import dev.kdrag0n.colorkt.util.ConversionProvider

/**
 * Polar (LCh) representation of [dev.kdrag0n.colorkt.ucs.lab.CieLab].
 *
 * @see dev.kdrag0n.colorkt.ucs.polar.Lch
 */
data class CieLch(
    override val L: Double,
    override val C: Double,
    override val h: Double = 0.0,
) : Lch {
    /**
     * Convert this color to the Cartesian (Lab) representation of CIE L*a*b*.
     *
     * @see dev.kdrag0n.colorkt.ucs.lab.Lab
     * @return Color in CIELAB representation
     */
    fun toCieLab(): CieLab {
        val (l, a, b) = toLab()
        return CieLab(l, a, b)
    }

    companion object : ConversionProvider {
        override fun register() {
            ConversionGraph.add<CieLab, CieLch> { it.toCieLch() }
            ConversionGraph.add<CieLch, CieLab> { it.toCieLab() }
        }

        /**
         * Convert this color to the polar (LCh) representation of CIE L*a*b*.
         *
         * @see dev.kdrag0n.colorkt.ucs.polar.Lch
         * @return Color in CIELCh representation
         */
        fun CieLab.toCieLch(): CieLch {
            val (l, c, h) = toLch()
            return CieLch(l, c, h)
        }
    }
}
