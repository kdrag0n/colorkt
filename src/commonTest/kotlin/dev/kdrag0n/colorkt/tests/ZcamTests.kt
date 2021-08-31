package dev.kdrag0n.colorkt.tests

import dev.kdrag0n.colorkt.cam.Zcam
import dev.kdrag0n.colorkt.cam.Zcam.Companion.toZcam
import dev.kdrag0n.colorkt.data.Illuminants
import dev.kdrag0n.colorkt.rgb.LinearSrgb.Companion.toLinear
import dev.kdrag0n.colorkt.rgb.Srgb
import dev.kdrag0n.colorkt.tristimulus.CieXyz
import dev.kdrag0n.colorkt.tristimulus.CieXyz.Companion.toXyz
import dev.kdrag0n.colorkt.tristimulus.CieXyzAbs
import dev.kdrag0n.colorkt.tristimulus.CieXyzAbs.Companion.toAbs
import dev.kdrag0n.colorkt.ucs.lab.CieLab
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ZcamTests {
    private val defaultCond = Zcam.ViewingConditions(
        surroundFactor = Zcam.ViewingConditions.SURROUND_AVERAGE,
        adaptingLuminance = 0.4 * CieXyzAbs.DEFAULT_SDR_WHITE_LUMINANCE,
        backgroundLuminance = CieLab(50.0, 0.0, 0.0).toXyz().toAbs().y,
        referenceWhite = Illuminants.D65.toAbs(),
    )

    @Test
    fun zcamExample1() {
        val cond = Zcam.ViewingConditions(
            surroundFactor = Zcam.ViewingConditions.SURROUND_AVERAGE,
            adaptingLuminance = 264.0,
            backgroundLuminance = 100.0,
            //referenceWhite = CieXyzAbs(256.0, 264.0, 202.0),
            referenceWhite = CieXyzAbs(250.92408, 264.0, 287.45112), // adapted to D65
        )

        //val sample = CieXyzAbs(185.0, 206.0, 163.0)
        val sample = CieXyzAbs(182.232347, 206.57991269, 231.87358528) // adapted to D65
        val zcam = sample.toZcam(cond)

        // TODO: assert for more exact values once the paper authors provide a clarification
        zcam.apply {
            assertSimilar(hz, 196.3524)
            assertSimilar(Qz, 321.3464)
            assertSimilar(Jz, 92.2520)
            assertSimilar(Mz, 10.5252)
            assertSimilar(Cz, 3.0216)
            assertSimilar(Sz, 19.1314)
            assertSimilar(Vz, 34.7022)
            assertSimilar(Kz, 25.2994)
            assertSimilar(Wz, 91.6837)
        }

        // Test against luxpy
        zcam.apply {
            assertApprox(hz, 197.26438822)
            assertApprox(Qz, 321.37946798)
            assertApprox(Jz, 91.45992645)
            assertApprox(Mz, 10.55298634)
            assertApprox(Cz, 3.00322656)
            assertApprox(Sz, 19.15565806)
            assertApprox(Vz, 33.91507829)
            assertApprox(Kz, 26.51716672)
            assertApprox(Wz, 90.94725313)
        }

        // Now invert it using all combinations of methods
        val invertedResults = Zcam.LuminanceSource.values()
            .flatMap { ls -> Zcam.ChromaSource.values().map { cs -> ls to cs } }
            .map { (ls, cs) -> Triple(ls, cs, zcam.toXyzAbs(ls, cs)) }
        invertedResults.forEach { (ls, cs, inverted) ->
            val comment = "Inverted with $ls, $cs"
            println("$ls $cs $inverted")
            assertApprox(inverted.y, sample.y, comment)
            assertApprox(inverted.x, sample.x, comment)
            assertApprox(inverted.z, sample.z, comment)
        }
    }

    @Test
    fun testZcamAliases() {
        val zcam = Srgb(0xff00ff).toLinear().toXyz().toAbs().toZcam(defaultCond)
        zcam.apply {
            assertEquals(Qz, brightness)
            assertEquals(Jz, lightness)
            assertEquals(Mz, colorfulness)
            assertEquals(Cz, chroma)
            assertEquals(hz, hue)
            assertEquals(Sz, saturation)
            assertEquals(Vz, vividness)
            assertEquals(Kz, blackness)
            assertEquals(Wz, whiteness)
        }
    }

    @Test
    fun testViewingConditionParams() {
        defaultCond.apply {
            assertEquals(surroundFactor, Zcam.ViewingConditions.SURROUND_AVERAGE)
            assertEquals(referenceWhite, Illuminants.D65.toAbs())
            assertEquals(backgroundLuminance, CieLab(50.0, 0.0, 0.0).toXyz().toAbs().y)
            assertEquals(adaptingLuminance, CieXyz(0.0, 0.4, 0.0).toAbs().y)
        }
    }
}

private fun assertSimilar(actual: Double, expected: Double) {
    assertTrue(abs(actual - expected) <= 1.25, "Expected $expected, got $actual")
}
