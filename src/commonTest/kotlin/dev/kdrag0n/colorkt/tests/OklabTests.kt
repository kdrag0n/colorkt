package dev.kdrag0n.colorkt.tests

import dev.kdrag0n.colorkt.Color.Companion.convert
import dev.kdrag0n.colorkt.tristimulus.CieXyz
import dev.kdrag0n.colorkt.ucs.lab.Oklab
import kotlin.test.Test

class OklabTests {
    @Test
    fun oklabXyz1() {
        val xyz = CieXyz(0.950, 1.000, 1.089)
        val lab = xyz.toLinearSrgb().convert<Oklab>()
        assertApprox(lab.L, 1.000)
        assertApprox(lab.a, 0.000)
        assertApprox(lab.b, 0.000)
    }

    @Test
    fun oklabXyz2() {
        val xyz = CieXyz(1.000, 0.000, 0.000)
        val lab = xyz.convert<Oklab>()
        assertApprox(lab.L, 0.450)
        assertApprox(lab.a, 1.236)
        assertApprox(lab.b, -0.019)
    }

    @Test
    fun oklabXyz3() {
        val xyz = CieXyz(0.000, 1.000, 0.000)
        val lab = xyz.convert<Oklab>()
        assertApprox(lab.L, 0.922)
        assertApprox(lab.a, -0.671)
        assertApprox(lab.b, 0.263)
    }

    @Test
    fun oklabXyz4() {
        val xyz = CieXyz(0.000, 0.000, 1.000)
        val lab = xyz.convert<Oklab>()
        assertApprox(lab.L, 0.153)
        assertApprox(lab.a, -1.415)
        assertApprox(lab.b, -0.449)
    }
}
