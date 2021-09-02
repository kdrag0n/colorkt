package dev.kdrag0n.colorkt.tests

import dev.kdrag0n.colorkt.cam.Zcam
import dev.kdrag0n.colorkt.cam.Zcam.Companion.toZcam
import dev.kdrag0n.colorkt.data.Illuminants
import dev.kdrag0n.colorkt.gamut.LchGamut.clipToLinearSrgb
import dev.kdrag0n.colorkt.gamut.OklabGamut.clipToLinearSrgb
import dev.kdrag0n.colorkt.rgb.LinearSrgb.Companion.toLinear
import dev.kdrag0n.colorkt.rgb.Srgb
import dev.kdrag0n.colorkt.tristimulus.CieXyzAbs
import dev.kdrag0n.colorkt.tristimulus.CieXyzAbs.Companion.DEFAULT_SDR_WHITE_LUMINANCE
import dev.kdrag0n.colorkt.tristimulus.CieXyzAbs.Companion.toAbs
import dev.kdrag0n.colorkt.ucs.lab.CieLab
import dev.kdrag0n.colorkt.ucs.lch.Oklch
import dev.kdrag0n.colorkt.conversion.ConversionGraph.convert
import dev.kdrag0n.colorkt.gamut.LchGamut
import dev.kdrag0n.colorkt.gamut.OklabGamut
import dev.kdrag0n.colorkt.rgb.LinearSrgb
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

private const val EPSILON = 0.001

class GamutTests {
    private val cond = Zcam.ViewingConditions(
        surroundFactor = Zcam.ViewingConditions.SURROUND_AVERAGE,
        adaptingLuminance = 0.4 * DEFAULT_SDR_WHITE_LUMINANCE,
        backgroundLuminance = CieLab(50.0, 0.0, 0.0).toXyz().toAbs().y,
        referenceWhite = Illuminants.D65.toAbs(),
    )

    private val srgbRgbkw = listOf(
        0xff0000,
        0x00ff00,
        0x0000ff,
        0x000000,
        0xffffff,
    ).map { Srgb(it) }

    @Test
    fun oklabClipRgbkw() {
        for (src in srgbRgbkw) {
            val srcLinear = src.toLinear()
            val lch = src.convert<Oklch>()

            // Boost the chroma and clip lightness
            val clipped = lch.copy(chroma = lch.chroma * 5).toOklab().clipToLinearSrgb()

            // Make sure it's the same
            assertApprox(clipped.r, srcLinear.r)
            assertApprox(clipped.g, srcLinear.g)
            assertApprox(clipped.b, srcLinear.b)

            // Now test all the methods and make sure they're reasonable: not NaN, 0, or out-of-gamut
            OklabGamut.ClipMethod.values().forEach { method ->
                val clippedM = lch.copy(chroma = lch.chroma * 5).toOklab().clipToLinearSrgb(method)
                assertInGamut(clippedM)
            }
        }
    }

    @Test
    fun zcamClipRgbkw() {
        for (src in srgbRgbkw) {
            val srcLinear = src.toLinear()
            val zcam = src.convert<CieXyzAbs>().toZcam(cond, include2D = false)

            // Boost the chroma
            val clipped = zcam.copy(chroma = zcam.chroma * 5).clipToLinearSrgb()

            // Now check
            assertApprox(clipped.r, srcLinear.r)
            assertApprox(clipped.g, srcLinear.g)
            assertApprox(clipped.b, srcLinear.b)

            // Now test all the methods and make sure they're reasonable: not NaN, 0, or out-of-gamut
            LchGamut.ClipMethod.values().forEach { method ->
                val clippedM = zcam.copy(chroma = zcam.chroma * 5).clipToLinearSrgb(method)
                assertInGamut(clippedM)
            }
        }
    }

    @Test
    fun zcamClipZeroChroma() {
        val zcam = Zcam(
            lightness = 50.0,
            chroma = 0.0,
            hue = 180.0,
            viewingConditions = cond,
        )

        val clipped = zcam.clipToLinearSrgb()
        assertInGamut(clipped)
    }

    @Test
    fun zcamClipNegativeLightness() {
        val zcam = Zcam(
            lightness = -10.0,
            chroma = 0.0,
            hue = 0.0,
            viewingConditions = cond,
        )

        val clipped = zcam.clipToLinearSrgb()
        assertInGamut(clipped)
    }

    private fun assertInGamut(rgb: LinearSrgb) {
        assertInGamut(rgb.r)
        assertInGamut(rgb.g)
        assertInGamut(rgb.b)
    }

    private fun assertInGamut(component: Double) {
        assertTrue(component in (0.0 - EPSILON)..(1.0 + EPSILON), "$component is out of gamut")
    }
}
