package dev.kdrag0n.colorkt.ucs.polar

import dev.kdrag0n.colorkt.ucs.polar.Lch.Companion.toLab
import dev.kdrag0n.colorkt.ucs.polar.Lch.Companion.toLch
import dev.kdrag0n.colorkt.ucs.lab.Oklab
import dev.kdrag0n.colorkt.util.conversion.ConversionGraph
import dev.kdrag0n.colorkt.util.conversion.ConversionProvider

/**
 * Polar (LCh) representation of [dev.kdrag0n.colorkt.ucs.lab.Oklab].
 *
 * @see dev.kdrag0n.colorkt.ucs.polar.Lch
 */
public data class Oklch(
    override val L: Double,
    override val C: Double,
    override val h: Double,
) : Lch {
    /**
     * Convert this color to the Cartesian (Lab) representation of Oklab.
     *
     * @see dev.kdrag0n.colorkt.ucs.lab.Lab
     * @return Color represented as Oklab
     */
    public fun toOklab(): Oklab {
        val (l, a, b) = toLab()
        return Oklab(l, a, b)
    }

    public companion object : ConversionProvider {
        override fun register() {
            ConversionGraph.add<Oklab, Oklch> { it.toOklch() }
            ConversionGraph.add<Oklch, Oklab> { it.toOklab() }
        }

        /**
         * Convert this color to the polar (LCh) representation of Oklab.
         *
         * @see dev.kdrag0n.colorkt.ucs.polar.Lch
         * @return Color represented as OkLCh
         */
        public fun Oklab.toOklch(): Oklch {
            val (l, c, h) = toLch()
            return Oklch(l, c, h)
        }
    }
}
