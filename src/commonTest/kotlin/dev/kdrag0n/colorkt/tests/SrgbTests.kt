package dev.kdrag0n.colorkt.tests

import dev.kdrag0n.colorkt.rgb.LinearSrgb.Companion.toLinear
import dev.kdrag0n.colorkt.rgb.Srgb
import dev.kdrag0n.colorkt.tristimulus.CieXyz.Companion.toXyz
import kotlin.test.Test
import kotlin.test.assertEquals

class SrgbTests {
    @Test
    fun srgbHexRoundTrip() {
        listOf("#ff0000", "#00ff00", "#0000ff").forEach { sample ->
            val parsed = Srgb(sample)
            val encoded = parsed.toHex()
            assertEquals(sample, encoded)
        }
    }

    @Test
    fun srgbIntToXyz() {
        val srgb = Srgb(0xf3a177)
        val xyz = srgb.toLinear().toXyz()
        xyz.apply {
            assertApprox(x, 0.53032247)
            assertApprox(y, 0.45876334)
            assertApprox(z, 0.23510203)
        }

        val inverted = xyz.toLinearSrgb().toSrgb()
        assertApprox(inverted.r, srgb.r)
        assertApprox(inverted.g, srgb.g)
        assertApprox(inverted.b, srgb.b)
    }
}
