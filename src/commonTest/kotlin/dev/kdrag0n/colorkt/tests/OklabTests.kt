package dev.kdrag0n.colorkt.tests

import dev.kdrag0n.colorkt.tristimulus.CieXyz
import dev.kdrag0n.colorkt.ucs.lab.Oklab
import dev.kdrag0n.colorkt.conversion.ConversionGraph.convert
import kotlin.test.Test

class OklabTests {
    @Test
    fun oklabXyz1() = testOklabXyz(
        xyz = CieXyz(0.950, 1.000, 1.089),
        expected = Oklab(1.000, 0.000, 0.000),
    )

    @Test
    fun oklabXyz2() = testOklabXyz(
        xyz = CieXyz(1.000, 0.000, 0.000),
        expected = Oklab(0.450, 1.236, -0.019),
    )

    @Test
    fun oklabXyz3() = testOklabXyz(
        xyz = CieXyz(0.000, 1.000, 0.000),
        expected = Oklab(0.922, -0.671, 0.263),
    )

    @Test
    fun oklabXyz4() = testOklabXyz(
        xyz = CieXyz(0.000, 0.000, 1.000),
        expected = Oklab(0.153, -1.415, -0.449),
    )

    private fun testOklabXyz(
        xyz: CieXyz,
        expected: Oklab,
    ) {
        val lab = xyz.convert<Oklab>()
        assertApprox(lab.L, expected.L)
        assertApprox(lab.a, expected.a)
        assertApprox(lab.b, expected.b)

        val inverted = lab.toXyz()
        assertApprox(inverted.x, xyz.x)
        assertApprox(inverted.y, xyz.y)
        assertApprox(inverted.z, xyz.z)
    }
}
