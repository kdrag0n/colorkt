package dev.kdrag0n.colorkt.tests

import dev.kdrag0n.colorkt.Color.Companion.to
import dev.kdrag0n.colorkt.ucs.lab.Oklab.Companion.toOklab
import dev.kdrag0n.colorkt.ucs.polar.CieLch
import dev.kdrag0n.colorkt.ucs.polar.Oklch
import dev.kdrag0n.colorkt.ucs.polar.Oklch.Companion.toOklch
import kotlin.test.Test

class ConversionTests {
    @Test
    fun testLongConversion() {
        val jzczhz = CieLch(50.0, 20.0, 1.0)
        val autoOklch = jzczhz.to<Oklch>()
        val manualOklch = jzczhz.toCieLab().toXyz().toLinearSrgb().toOklab().toOklch()
        assertApprox(autoOklch.L, manualOklch.L)
        assertApprox(autoOklch.C, manualOklch.C)
        assertApprox(autoOklch.h, manualOklch.h)
    }
}
