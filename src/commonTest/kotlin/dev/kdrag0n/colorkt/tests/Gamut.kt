package dev.kdrag0n.colorkt.tests

import dev.kdrag0n.colorkt.Color.Companion.convert
import dev.kdrag0n.colorkt.cam.Zcam
import dev.kdrag0n.colorkt.cam.Zcam.Companion.toZcam
import dev.kdrag0n.colorkt.data.Illuminants
import dev.kdrag0n.colorkt.gamut.LchGamut.clipToLinearSrgb
import dev.kdrag0n.colorkt.gamut.OklabGamut.clipToLinearSrgb
import dev.kdrag0n.colorkt.rgb.LinearSrgb.Companion.toLinearSrgb
import dev.kdrag0n.colorkt.rgb.Srgb
import dev.kdrag0n.colorkt.tristimulus.CieXyzAbs
import dev.kdrag0n.colorkt.tristimulus.CieXyzAbs.Companion.DEFAULT_SDR_WHITE_LUMINANCE
import dev.kdrag0n.colorkt.tristimulus.CieXyzAbs.Companion.toAbs
import dev.kdrag0n.colorkt.ucs.lab.CieLab
import dev.kdrag0n.colorkt.ucs.polar.Oklch
import kotlin.test.Test

class Gamut {
    @Test
    fun testOklabClip() {
        // R, G, B
        for (channel in 2 downTo 0) {
            val src = Srgb(0xff shl (channel * 8))
            val srcLinear = src.toLinearSrgb()
            val lch = src.convert<Oklch>()

            // Boost the chroma
            val clipped = lch.copy(C = lch.C * 5).toOklab().clipToLinearSrgb()

            // Now check
            assertApprox(clipped.r, srcLinear.r)
            assertApprox(clipped.g, srcLinear.g)
            assertApprox(clipped.b, srcLinear.b)
        }
    }

    @Test
    fun testZcamClip() {
        val cond = Zcam.ViewingConditions(
            surroundFactor = Zcam.ViewingConditions.SURROUND_AVERAGE,
            adaptingLuminance = 0.4 * DEFAULT_SDR_WHITE_LUMINANCE,
            backgroundLuminance = CieLab(50.0, 0.0, 0.0).toXyz().y * DEFAULT_SDR_WHITE_LUMINANCE,
            referenceWhite = Illuminants.D65.toAbs(DEFAULT_SDR_WHITE_LUMINANCE),
        )

        // R, G, B
        for (channel in 2 downTo 0) {
            val src = Srgb(0xff shl (channel * 8))
            val srcLinear = src.toLinearSrgb()
            val zcam = src.convert<CieXyzAbs>().toZcam(cond, include2D = false)

            // Boost the chroma
            val clipped = zcam.copy(chroma = zcam.chroma * 5).clipToLinearSrgb()

            // Now check
            assertApprox(clipped.r, srcLinear.r)
            assertApprox(clipped.g, srcLinear.g)
            assertApprox(clipped.b, srcLinear.b)
        }
    }
}
