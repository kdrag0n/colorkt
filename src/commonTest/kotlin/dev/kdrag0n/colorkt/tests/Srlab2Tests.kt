package dev.kdrag0n.colorkt.tests

import dev.kdrag0n.colorkt.data.Illuminants
import dev.kdrag0n.colorkt.tristimulus.CieXyz
import dev.kdrag0n.colorkt.tristimulus.CieXyz.Companion.toXyz
import dev.kdrag0n.colorkt.ucs.lab.Srlab2
import dev.kdrag0n.colorkt.ucs.lab.Srlab2.Companion.toSrlab2
import dev.kdrag0n.colorkt.ucs.lch.Srlch2.Companion.toSrlch2
import kotlin.test.Test

class Srlab2Tests {
    @Test
    fun colorioSample1() = xyzSrlabTest(
        xyz = CieXyz(0.1, 0.2, 0.3),
        srlab2 = Srlab2(51.232467008097125, -47.92421786981609, -14.255014329381225),
    )
    @Test
    fun colorioSample2() = xyzSrlabTest(
        xyz = CieXyz(0.8, 0.9, 0.1),
        srlab2 = Srlab2(96.26425102758816, -30.92082858867076, 103.76703583290106),
    )
    @Test
    fun colorioSample3() = xyzSrlabTest(
        xyz = Illuminants.D65_ASTM,
        srlab2 = Srlab2(99.99977248346777, -0.004069281557519844, -0.00039226988315022027),
    )
    @Test
    fun colorioSample4() = xyzSrlabTest(
        xyz = CieXyz(0.005, 0.006, 0.004),
        srlab2 = Srlab2(5.423523417804045, -2.6161648383355214, 3.6349016311770272),
    )

    private fun xyzSrlabTest(xyz: CieXyz, srlab2: Srlab2) {
        val actual = xyz.toLinearSrgb().toSrlab2()
        // Needs larger epsilon due to less accurate matrices
        assertApprox(actual.L, srlab2.L, epsilon = 0.1)
        assertApprox(actual.a, srlab2.a, epsilon = 0.1)
        assertApprox(actual.b, srlab2.b, epsilon = 0.1)

        // Invert
        val inverted = actual.toLinearSrgb().toXyz()
        assertApprox(inverted.x, xyz.x)
        assertApprox(inverted.y, xyz.y)
        assertApprox(inverted.z, xyz.z)

        // Test LCh inversion
        val lch = actual.toSrlch2()
        val lchInverted = lch.toSrlab2()
        assertApprox(lchInverted.L, actual.L)
        assertApprox(lchInverted.a, actual.a)
        assertApprox(lchInverted.b, actual.b)
    }
}
