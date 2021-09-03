package dev.kdrag0n.colorkt.tests

import dev.kdrag0n.colorkt.data.Illuminants
import dev.kdrag0n.colorkt.rgb.LinearSrgb.Companion.toLinear
import dev.kdrag0n.colorkt.rgb.Srgb
import dev.kdrag0n.colorkt.tristimulus.CieXyz.Companion.toXyz
import dev.kdrag0n.colorkt.ucs.lab.CieLab.Companion.toCieLab
import dev.kdrag0n.colorkt.ucs.lch.CieLch.Companion.toCieLch
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class XyzTests {
    @Test
    fun neutralSrgbChroma() {
        var maxChroma = 0.0
        var avgChroma = 0.0
        for (v in 0..255) {
            val lch = Srgb(v, v, v).toLinear().toXyz().toCieLab().toCieLch()
            println(lch)

            // Testing against another reference white would break this
            assertEquals(lch.referenceWhite, Illuminants.D65)

            avgChroma += lch.chroma
            if (lch.chroma > maxChroma) {
                maxChroma = lch.chroma
            }
        }
        avgChroma /= 256

        println("max = $maxChroma")
        println("avg = $avgChroma")
        assertTrue(maxChroma <= 7.108895957933346e-14, "Max neutral chroma = $maxChroma")
        assertTrue(avgChroma <= 1.3133161994387564e-14, "Average neutral chroma = $avgChroma")
    }
}
