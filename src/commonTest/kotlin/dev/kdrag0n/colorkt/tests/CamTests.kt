package dev.kdrag0n.colorkt.tests

import dev.kdrag0n.colorkt.cam.Zcam
import dev.kdrag0n.colorkt.cam.Zcam.Companion.toZcam
import dev.kdrag0n.colorkt.tristimulus.CieXyzAbs
import kotlin.test.Test

class CamTests {
    @Test
    fun testZcamExample1() {
        val cond = Zcam.ViewingConditions(
            surroundFactor = Zcam.ViewingConditions.SURROUND_AVERAGE,
            adaptingLuminance = 264.0,
            backgroundLuminance = 100.0,
            //referenceWhite = CieXyz100(256.0, 264.0, 202.0),
            referenceWhite = CieXyzAbs(250.92408, 264.0, 287.45112), // d65
        )

        //val sample = CieXyz100(185.0, 206.0, 163.0)
        val sample = CieXyzAbs(182.232347, 206.57991269, 231.87358528) // d65
        val zcam = sample.toZcam(cond)

        assertApprox(cond.F_l, 1.0970)
        assertApprox(cond.F_b, 0.6155)

        /*
        zcam.apply {
            dev.kdrag0n.colorkt.tests.assertApprox(Iz, 0.3947)
            dev.kdrag0n.colorkt.tests.assertApprox(az, -0.0165)
            dev.kdrag0n.colorkt.tests.assertApprox(bz, -0.0048)
            dev.kdrag0n.colorkt.tests.assertApprox(hz, 196.3524)
            dev.kdrag0n.colorkt.tests.assertApprox(Qz, 321.3464)
            dev.kdrag0n.colorkt.tests.assertApprox(Jz, 92.2520)
            dev.kdrag0n.colorkt.tests.assertApprox(Mz, 10.5252)
            dev.kdrag0n.colorkt.tests.assertApprox(Cz, 3.0216)
            dev.kdrag0n.colorkt.tests.assertApprox(Sz, 19.1314)
            dev.kdrag0n.colorkt.tests.assertApprox(Vz, 34.7022)
            dev.kdrag0n.colorkt.tests.assertApprox(Kz, 25.2994)
            dev.kdrag0n.colorkt.tests.assertApprox(Wz, 91.6837)
        }*/

        // inverse
        val inverted = zcam.toXyzAbs(Zcam.LuminanceSource.LIGHTNESS, Zcam.ChromaSource.COLORFULNESS)
        assertApprox(inverted.x, sample.x)
        assertApprox(inverted.y, sample.y)
        assertApprox(inverted.z, sample.z)
    }
}
