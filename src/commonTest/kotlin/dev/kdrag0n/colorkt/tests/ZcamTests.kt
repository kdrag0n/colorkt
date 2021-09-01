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
    fun zcamExample1() = testExample(
        referenceWhite = CieXyzAbs(256.0, 264.0, 202.0),
        sampleD65 = CieXyzAbs(182.25997236, 206.57412429, 231.18612283),
        surround = Zcam.ViewingConditions.SURROUND_AVERAGE,
        L_a = 264.0,
        Y_b = 100.0,

        hz = 196.3524,
        Qz = 321.3464,
        Jz = 92.2520,
        Mz = 10.5252,
        Cz = 3.0216,
        Sz = 19.1314,
        Vz = 34.7022,
        Kz = 25.2994,
        Wz = 91.6837,
    )

    @Test
    fun zcamExample2() = testExample(
        referenceWhite = CieXyzAbs(256.0, 264.0, 202.0),
        sampleD65 = CieXyzAbs(91.33742436, 97.68591995, 169.79324781),
        surround = Zcam.ViewingConditions.SURROUND_AVERAGE,
        L_a = 264.0,
        Y_b = 100.0,

        hz = 250.6422,
        Qz = 248.0394,
        Jz = 71.2071,
        Mz = 23.8744,
        Cz = 6.8539,
        Sz = 32.7963,
        Vz = 18.2796,
        Kz = 40.4621,
        Wz = 70.4026,
    )

    @Test
    fun zcamExample3() = testExample(
        referenceWhite = CieXyzAbs(256.0, 264.0, 202.0),
        sampleD65 = CieXyzAbs(77.59404245, 80.98983072, 85.36972501),
        surround = Zcam.ViewingConditions.SURROUND_DIM,
        L_a = 264.0,
        Y_b = 100.0,
        // Paper has the wrong L_a and Y_b
        /*
        L_a = 150.0,
        Y_b = 60.0,
         */

        hz = 58.7532,
        Qz = 196.7686,
        Jz = 68.8890,
        Mz = 2.7918,
        Cz = 0.9774,
        Sz = 12.5916,
        Vz = 11.0371,
        Kz = 44.4143,
        Wz = 68.8737,
    )

    @Test
    fun zcamExample4() = testExample(
        referenceWhite = CieXyzAbs(2103.0, 2259.0, 1401.0),
        sampleD65 = CieXyzAbs(910.69546926, 1107.07247243, 804.10072127),
        surround = Zcam.ViewingConditions.SURROUND_DARK,
        L_a = 359.0,
        Y_b = 16.0,

        hz = 123.9464,
        Qz = 114.7431,
        Jz = 82.6445,
        Mz = 18.1655,
        Cz = 13.0838,
        Sz = 44.7277,
        Vz = 34.4874,
        Kz = 26.8778,
        Wz = 78.2653,
    )

    @Test
    fun zcamExample5() = testExample(
        referenceWhite = CieXyzAbs(2103.0, 2259.0, 1401.0),
        sampleD65 = CieXyzAbs(94.13640377, 65.93948718, 45.16280809),
        surround = Zcam.ViewingConditions.SURROUND_DARK,
        L_a = 359.0,
        Y_b = 16.0,

        // Paper says 389.7720 because it unconditionally adds 360 degrees
        hz = 29.7720,
        Qz = 45.8363,
        Jz = 33.0139,
        Mz = 26.9446,
        Cz = 19.4070,
        Sz = 86.1882,
        Vz = 43.6447,
        Kz = 47.9942,
        Wz = 30.2593,
    )

    @Test
    fun zcamAliases() {
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
    fun viewingConditionParams() {
        defaultCond.apply {
            assertEquals(surroundFactor, Zcam.ViewingConditions.SURROUND_AVERAGE)
            assertEquals(referenceWhite, Illuminants.D65.toAbs())
            assertEquals(backgroundLuminance, CieLab(50.0, 0.0, 0.0).toXyz().toAbs().y)
            assertEquals(adaptingLuminance, CieXyz(0.0, 0.4, 0.0).toAbs().y)
        }
    }

    private fun testExample(
        sampleD65: CieXyzAbs,
        referenceWhite: CieXyzAbs,
        surround: Double,
        L_a: Double,
        Y_b: Double,
        hz: Double,
        Qz: Double,
        Jz: Double,
        Mz: Double,
        Cz: Double,
        Sz: Double,
        Vz: Double,
        Kz: Double,
        Wz: Double,
    ) {
        val cond = Zcam.ViewingConditions(
            surroundFactor = surround,
            adaptingLuminance = L_a,
            backgroundLuminance = Y_b,
            referenceWhite = referenceWhite,
        )

        val zcam = sampleD65.toZcam(cond)
        println(zcam)

        // TODO: assert for more exact values once the paper authors provide a clarification
        assertSimilar(zcam.hz, hz)
        assertSimilar(zcam.Qz, Qz)
        assertSimilar(zcam.Jz, Jz)
        assertSimilar(zcam.Mz, Mz)
        assertSimilar(zcam.Cz, Cz)
        assertSimilar(zcam.Sz, Sz)
        assertSimilar(zcam.Vz, Vz)
        assertSimilar(zcam.Kz, Kz, epsilon = 0.8)
        assertSimilar(zcam.Wz, Wz)

        // Now invert it using all combinations of methods
        val invertedResults = Zcam.LuminanceSource.values()
            .flatMap { ls -> Zcam.ChromaSource.values().map { cs -> ls to cs } }
            .map { (ls, cs) -> Triple(ls, cs, zcam.toXyzAbs(ls, cs)) }
        invertedResults.forEach { (ls, cs, inverted) ->
            val comment = "Inverted with $ls, $cs"
            println("$ls $cs $inverted")
            assertApprox(inverted.y, sampleD65.y, comment)
            assertApprox(inverted.x, sampleD65.x, comment)
            assertApprox(inverted.z, sampleD65.z, comment)
        }
    }
}

private fun assertSimilar(actual: Double, expected: Double, epsilon: Double = 0.1) {
    assertTrue(abs(actual - expected) <= epsilon, "Expected $expected, got $actual")
}
