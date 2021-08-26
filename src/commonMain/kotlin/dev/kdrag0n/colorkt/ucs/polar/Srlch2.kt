package dev.kdrag0n.colorkt.ucs.polar

import dev.kdrag0n.colorkt.ucs.lab.Srlab2
import dev.kdrag0n.colorkt.ucs.polar.Lch.Companion.calcLabA
import dev.kdrag0n.colorkt.ucs.polar.Lch.Companion.calcLabB
import dev.kdrag0n.colorkt.ucs.polar.Lch.Companion.calcLchC
import dev.kdrag0n.colorkt.ucs.polar.Lch.Companion.calcLchH
import dev.kdrag0n.colorkt.util.conversion.ConversionGraph
import dev.kdrag0n.colorkt.util.conversion.ConversionProvider
import kotlin.jvm.JvmName
import kotlin.jvm.JvmStatic

/**
 * Polar (LCh) representation of [dev.kdrag0n.colorkt.ucs.lab.Srlab2].
 *
 * @see dev.kdrag0n.colorkt.ucs.polar.Lch
 */
public data class Srlch2(
    override val L: Double,
    override val C: Double,
    override val h: Double,
) : Lch {
    /**
     * Convert this color to the Cartesian (Lab) representation of SRLAB2.
     *
     * @see dev.kdrag0n.colorkt.ucs.lab.Lab
     * @return Color represented as SRLAB2
     */
    public fun toSrlab2(): Srlab2 = Srlab2(
        L = L,
        a = calcLabA(),
        b = calcLabB(),
    )

    public companion object : ConversionProvider {
        override fun register() {
            ConversionGraph.add<Srlab2, Srlch2> { it.toSrlch2() }
            ConversionGraph.add<Srlch2, Srlab2> { it.toSrlab2() }
        }

        /**
         * Convert this color to the polar (LCh) representation of SRLAB2.
         *
         * @see dev.kdrag0n.colorkt.ucs.polar.Lch
         * @return Color represented as SRLCh2
         */
        @JvmStatic
        @JvmName("fromSrlab2")
        public fun Srlab2.toSrlch2(): Srlch2 = Srlch2(
            L = L,
            C = calcLchC(),
            h = calcLchH(),
        )
    }
}
