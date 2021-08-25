import dev.kdrag0n.colorkt.Color.Companion.to
import dev.kdrag0n.colorkt.tristimulus.CieXyz
import dev.kdrag0n.colorkt.ucs.lab.Oklab
import dev.kdrag0n.colorkt.ucs.polar.Oklch
import kotlin.test.Test

class UcsTests {
    @Test
    fun testOklabXyz1() {
        val xyz = CieXyz(0.950, 1.000, 1.089)
        val lab = xyz.toLinearSrgb().to<Oklab>()
        assertApprox(lab.L, 1.000)
        assertApprox(lab.a, 0.000)
        assertApprox(lab.b, 0.000)
    }

    @Test
    fun testOklabXyz2() {
        val xyz = CieXyz(1.000, 0.000, 0.000)
        val lab = xyz.to<Oklab>()
        assertApprox(lab.L, 0.450)
        assertApprox(lab.a, 1.236)
        assertApprox(lab.b, -0.019)
    }

    @Test
    fun testOklabXyz3() {
        val xyz = CieXyz(0.000, 1.000, 0.000)
        val lab = xyz.to<Oklab>()
        assertApprox(lab.L, 0.922)
        assertApprox(lab.a, -0.671)
        assertApprox(lab.b, 0.263)
    }

    @Test
    fun testOklabXyz4() {
        val xyz = CieXyz(0.000, 0.000, 1.000)
        val lab = xyz.to<Oklab>()
        assertApprox(lab.L, 0.153)
        assertApprox(lab.a, -1.415)
        assertApprox(lab.b, -0.449)
    }

    @Test
    fun testLongConversion() {
        val jzczhz = Jzczhz(0.01, 0.01, 0.01)
        val oklch = jzczhz.to<Oklch>()
        assertApprox(oklch.L, 0.7143932056439198)
        assertApprox(oklch.C, 0.14848998140147077)
        assertApprox(oklch.h, 355.33984992080804)
    }
}
