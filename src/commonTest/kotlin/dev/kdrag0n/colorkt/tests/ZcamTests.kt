package dev.kdrag0n.colorkt.tests

import dev.kdrag0n.colorkt.cam.Zcam
import dev.kdrag0n.colorkt.cam.Zcam.Companion.toZcam
import dev.kdrag0n.colorkt.tristimulus.CieXyzAbs
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertTrue

class ZcamTests {
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

        // Now invert it
        val inverted = zcam.toXyzAbs(Zcam.LuminanceSource.LIGHTNESS, Zcam.ChromaSource.COLORFULNESS)
        assertApprox(inverted.x, sample.x)
        assertApprox(inverted.y, sample.y)
        assertApprox(inverted.z, sample.z)
    }
}

private fun assertSimilar(actual: Double, expected: Double) {
    assertTrue(abs(actual - expected) <= 1.25, "Expected $expected, got $actual")
}
