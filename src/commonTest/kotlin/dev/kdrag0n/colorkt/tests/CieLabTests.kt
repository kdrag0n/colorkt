package dev.kdrag0n.colorkt.tests

import dev.kdrag0n.colorkt.rgb.LinearSrgb.Companion.toLinear
import dev.kdrag0n.colorkt.rgb.Srgb
import dev.kdrag0n.colorkt.tristimulus.CieXyz.Companion.toXyz
import dev.kdrag0n.colorkt.ucs.lab.CieLab.Companion.toCieLab
import dev.kdrag0n.colorkt.ucs.lch.CieLch.Companion.toCieLch
import kotlin.test.Test

class CieLabTests {
    @Test
    fun neutralSrgb() {
        for (v in 0..255) {
            val srgb = Srgb(v, v, v)
            val lch = srgb.toLinear().toXyz().toCieLab().toCieLch()
            val inverted = lch.toCieLab().toXyz().toLinearSrgb().toSrgb()
            assertApprox(inverted.r, srgb.r)
            assertApprox(inverted.g, srgb.g)
            assertApprox(inverted.b, srgb.b)
        }
    }
}
