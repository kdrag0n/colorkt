package dev.kdrag0n.colorkt.tests

import dev.kdrag0n.colorkt.data.Illuminants
import dev.kdrag0n.colorkt.adaptation.VonKries
import dev.kdrag0n.colorkt.tristimulus.CieXyz
import kotlin.test.Test

class CatTests {
    @Test
    fun testCat02() {
        val subject = CieXyz(0.3, 0.7, 0.9)
        val src = Illuminants.D50
        val dest = Illuminants.D65

        val adapted = VonKries.adapt(subject, src, dest, VonKries.CAT02)

        // From Python library: colour-science
        // colour.adaptation.chromatic_adaptation(np.array([0.3, 0.7, 0.9]), np.array([0.9642, 1.0, 0.8251]), np.array([0.95047, 1.0, 0.108883]))
        assertApprox(adapted.x, 0.20286655)
        assertApprox(adapted.y, 0.66551123)
        assertApprox(adapted.z, 0.12604366)
    }
}
