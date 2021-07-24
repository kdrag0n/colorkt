import dev.kdrag0n.colorkt.cam.Zcam
import dev.kdrag0n.colorkt.cam.Zcam.Companion.toZcam
import dev.kdrag0n.colorkt.tristimulus.CieXyz100
import kotlin.test.Test

class CamTests {
    @Test
    fun testZcamExample1() {
        val cond = Zcam.ViewingConditions(
            F_s = Zcam.ViewingConditions.SURROUND_AVERAGE,
            L_a = 264.0,
            Y_b = 100.0,
            //referenceWhite = CieXyz100(256.0, 264.0, 202.0),
            referenceWhite = CieXyz100(250.92408, 264.0, 287.45112), // d65
        )

        //val sample = CieXyz100(185.0, 206.0, 163.0)
        val sample = CieXyz100(182.232347, 206.57991269, 231.87358528) // d65
        val zcam = sample.toZcam(cond)
        println(cond)
        println(zcam)

        println("F_s = ${cond.F_s}, F_l = ${cond.F_l}, F_b = ${cond.F_b}")
        assertApprox(cond.F_l, 1.0970)
        assertApprox(cond.F_b, 0.6155)

        /*
        zcam.apply {
            assertApprox(Iz, 0.3947)
            assertApprox(az, -0.0165)
            assertApprox(bz, -0.0048)
            assertApprox(hz, 196.3524)
            assertApprox(Qz, 321.3464)
            assertApprox(Jz, 92.2520)
            assertApprox(Mz, 10.5252)
            assertApprox(Cz, 3.0216)
            assertApprox(Sz, 19.1314)
            assertApprox(Vz, 34.7022)
            assertApprox(Kz, 25.2994)
            assertApprox(Wz, 91.6837)
        }*/

        // inverse
        val inverted = zcam.toCieXyz100(Zcam.LuminanceSource.LIGHTNESS, Zcam.ChromaSource.COLORFULNESS)
        println("inverted = $inverted")
        assertApprox(inverted.x, sample.x)
        assertApprox(inverted.y, sample.y)
        assertApprox(inverted.z, sample.z)
    }
}
