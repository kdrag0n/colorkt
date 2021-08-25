package dev.kdrag0n.colorkt.adaptation

import dev.kdrag0n.colorkt.tristimulus.CieXyz
import dev.kdrag0n.colorkt.tristimulus.CieXyz.Companion.toCieXyz
import dev.kdrag0n.colorkt.util.math.Matrix3

public object VonKries {
    /**
     * Create a one-step Von Kries chromatic adaptation matrix that adapts colors from reference white [src] to [dest],
     * using [transform] as the sensor space transformation matrix.
     *
     * Use [adapt] if you simply want to adapt a single color.
     */
    public fun createMatrix(src: CieXyz, dest: CieXyz, transform: Matrix3 = CAT02): Matrix3 {
        val srcLms = transform * src.toVector3()
        val destLms = transform * dest.toVector3()
        val catLms = destLms / srcLms
        return transform * catLms.toDiagMatrix() * transform.inv()
    }

    /**
     * Adapt [color] from refrence white [src] to [dest], using [transform] as the sensor space transformation matrix.
     * This uses the one-step Von Kries transform for chromatic adaptation.
     */
    public fun adapt(color: CieXyz, src: CieXyz, dest: CieXyz, transform: Matrix3 = CAT02): CieXyz {
        val matrix = createMatrix(src, dest, transform)
        val adapted = matrix * color.toVector3()
        return adapted.toCieXyz()
    }

    /**
     * CAT02 chromatic adaptation sensor space from CIECAT02.
     *
     * @see <a href="http://markfairchild.org/PDFs/PAP10.pdf">A Revision of CIECAM97s for Practical Applications</a>
     */
    public val CAT02: Matrix3 = Matrix3(
         0.7328, 0.4296, -0.1624,
        -0.7036, 1.6975,  0.0061,
         0.0030, 0.0136,  0.9834,
    )

    /**
     * CAT16 chromatic adaptation sensor space from CAM16.
     *
     * @see <a href="https://onlinelibrary.wiley.com/doi/abs/10.1002/col.22131">Comprehensive color solutions: CAM16, CAT16, and CAM16-UCS</a>
     */
    public val CAT16: Matrix3 = Matrix3(
         0.401288,  0.650173, -0.051461,
        -0.250268,  1.204414,  0.045854,
        -0.002079,  0.048952,  0.953127,
    )
}
