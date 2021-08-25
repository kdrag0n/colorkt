package dev.kdrag0n.colorkt.tristimulus

import dev.kdrag0n.colorkt.Color
import dev.kdrag0n.colorkt.util.ConversionGraph
import dev.kdrag0n.colorkt.util.ConversionProvider

data class CieXyzAbs(
    val x: Double,
    val y: Double,
    val z: Double,
) : Color {
    fun toCieXyz() = CieXyz(
        x = x / y,
        y = y / y,
        z = z / y,
    )

    companion object : ConversionProvider {
        const val DEFAULT_SRGB_WHITE_LUMINANCE = 200.0 // cd/m^2

        override fun register() {
            ConversionGraph.add<CieXyz, CieXyzAbs> { it.toCieXyzAbs(DEFAULT_SRGB_WHITE_LUMINANCE) }
            ConversionGraph.add<CieXyzAbs, CieXyz> { it.toCieXyz() }
        }

        fun CieXyz.toCieXyzAbs(luminance: Double) = CieXyzAbs(
            x = x * luminance,
            y = y * luminance,
            z = z * luminance,
        )
    }
}
