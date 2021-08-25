package dev.kdrag0n.colorkt.ucs.polar

import dev.kdrag0n.colorkt.ucs.polar.Lch.Companion.toLab
import dev.kdrag0n.colorkt.ucs.polar.Lch.Companion.toLch
import dev.kdrag0n.colorkt.ucs.lab.CieLab
import dev.kdrag0n.colorkt.util.conversion.ConversionGraph
import dev.kdrag0n.colorkt.util.conversion.ConversionProvider
import kotlin.jvm.JvmName
import kotlin.jvm.JvmStatic

/**
 * Polar (LCh) representation of [dev.kdrag0n.colorkt.ucs.lab.CieLab].
 *
 * @see dev.kdrag0n.colorkt.ucs.polar.Lch
 */
public data class CieLch(
    override val L: Double,
    override val C: Double,
    override val h: Double = 0.0,
) : Lch {
    /**
     * Convert this color to the Cartesian (Lab) representation of CIE L*a*b*.
     *
     * @see dev.kdrag0n.colorkt.ucs.lab.Lab
     * @return Color represented as CIELAB
     */
    public fun toCieLab(): CieLab {
        val (l, a, b) = toLab()
        return CieLab(l, a, b)
    }

    public companion object : ConversionProvider {
        override fun register() {
            ConversionGraph.add<CieLab, CieLch> { it.toCieLch() }
            ConversionGraph.add<CieLch, CieLab> { it.toCieLab() }
        }

        /**
         * Convert this color to the polar (LCh) representation of CIE L*a*b*.
         *
         * @see dev.kdrag0n.colorkt.ucs.polar.Lch
         * @return Color represented as CIELCh
         */
        @JvmStatic
        @JvmName("fromCieLab")
        public fun CieLab.toCieLch(): CieLch {
            val (l, c, h) = toLch()
            return CieLch(l, c, h)
        }
    }
}
