package dev.kdrag0n.colorkt.adaptation

import dev.kdrag0n.colorkt.tristimulus.CieXyz
import dev.kdrag0n.colorkt.tristimulus.CieXyz.Companion.toCieXyz
import dev.kdrag0n.colorkt.util.Matrix3

object VonKries {
    fun createMatrix(src: CieXyz, dest: CieXyz, transform: Matrix3 = CAT02): Matrix3 {
        val srcLms = transform * src.toVector3()
        val destLms = transform * dest.toVector3()
        val catLms = destLms / srcLms
        return transform * catLms.toDiagMatrix() * transform.inv()
    }

    fun adapt(color: CieXyz, src: CieXyz, dest: CieXyz, transform: Matrix3 = CAT02): CieXyz {
        val matrix = createMatrix(src, dest, transform)
        val adapted = color.toVector3() * matrix
        return adapted.toCieXyz()
    }

    val CAT02 = Matrix3(
         0.7328, 0.4296, -0.1624,
        -0.7036, 1.6975,  0.0061,
         0.0030, 0.0136,  0.9834,
    )

    val CAT16 = Matrix3(
         0.401288,  0.650173, -0.051461,
        -0.250268,  1.204414,  0.045854,
        -0.002079,  0.048952,  0.953127,
    )
}
