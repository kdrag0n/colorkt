package dev.kdrag0n.colorkt.ucs.lch

import dev.kdrag0n.colorkt.ucs.lab.Srlab2
import dev.kdrag0n.colorkt.conversion.ConversionGraph
import kotlin.jvm.JvmName
import kotlin.jvm.JvmStatic
import kotlin.jvm.JvmSynthetic

/**
 * Polar (LCh) representation of [dev.kdrag0n.colorkt.ucs.lab.Srlab2].
 *
 * @see dev.kdrag0n.colorkt.ucs.lch.Lch
 */
public data class Srlch2(
    override val lightness: Double,
    override val chroma: Double,
    override val hue: Double,
) : Lch {
    /**
     * Convert this color to the Cartesian (Lab) representation of SRLAB2.
     *
     * @see dev.kdrag0n.colorkt.ucs.lab.Lab
     * @return Color represented as SRLAB2
     */
    public fun toSrlab2(): Srlab2 = Srlab2(
        L = lightness,
        a = calcLabA(),
        b = calcLabB(),
    )

    public companion object {
        @JvmSynthetic
        internal fun register() {
            ConversionGraph.add<Srlab2, Srlch2> { it.toSrlch2() }
            ConversionGraph.add<Srlch2, Srlab2> { it.toSrlab2() }
        }

        /**
         * Convert this color to the polar (LCh) representation of SRLAB2.
         *
         * @see dev.kdrag0n.colorkt.ucs.lch.Lch
         * @return Color represented as SRLCh2
         */
        @JvmStatic
        @JvmName("fromSrlab2")
        public fun Srlab2.toSrlch2(): Srlch2 = Srlch2(
            lightness = L,
            chroma = calcLchC(),
            hue = calcLchH(),
        )
    }
}
