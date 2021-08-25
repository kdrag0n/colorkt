package dev.kdrag0n.colorkt.util.math

import kotlin.jvm.JvmInline

/**
 * 3-component vector of [Double] values.
 */
@JvmInline
public value class Vector3(
    /**
     * 1D array containing 3 [Double] values for this vector.
     */
    public val values: DoubleArray,
) {
    /**
     * Create a matrix with the 3 provided values.
     */
    public constructor(
        n1: Double, n2: Double, n3: Double,
    ) : this(doubleArrayOf(n1, n2, n3))

    /**
     * Divide each value in this vector by the corresponding value in [vec].
     */
    public operator fun div(vec: Vector3): Vector3 {
        val (l1, l2, l3) = this
        val (r1, r2, r3) = vec

        return Vector3(
            l1 / r1,
            l2 / r2,
            l3 / r3,
        )
    }

    /**
     * Convert this vector to a diagonal 3x3 matrix where values are on the diagonal axis.
     * @return diagonal matrix
     */
    public fun toDiagMatrix(): Matrix3 {
        val (n1, n2, n3) = this

        return Matrix3(
            n1, 0.0, 0.0,
            0.0, n2, 0.0,
            0.0, 0.0, n3,
        )
    }

    public operator fun component1(): Double = values[0]
    public operator fun component2(): Double = values[1]
    public operator fun component3(): Double = values[2]
}
