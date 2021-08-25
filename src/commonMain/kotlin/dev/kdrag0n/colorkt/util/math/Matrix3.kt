package dev.kdrag0n.colorkt.util.math

import kotlin.jvm.JvmInline

/**
 * 3x3 matrix, containing 9 [Double] values.
 */
@JvmInline
public value class Matrix3(
    /**
     * 1D array containing 9 values for this 3x3 matrix.
     */
    public val values: DoubleArray,
) {
    /**
     * Create a matrix with the 9 provided values.
     */
    public constructor(
        n1: Double, n2: Double, n3: Double,
        n4: Double, n5: Double, n6: Double,
        n7: Double, n8: Double, n9: Double,
    ) : this(doubleArrayOf(
        n1, n2, n3,
        n4, n5, n6,
        n7, n8, n9,
    ))

    /**
     * Multiply this matrix by [vector].
     * @return [Vector3]
     */
    public operator fun times(vector: Vector3): Vector3 = values.let { m ->
        val (r1, r2, r3) = vector

        Vector3(
            m[0]*r1 + m[1]*r2 + m[2]*r3,
            m[3]*r1 + m[4]*r2 + m[5]*r3,
            m[6]*r1 + m[7]*r2 + m[8]*r3,
        )
    }

    /**
     * Multiply this matrix by [matrix].
     * @return [Matrix3]
     */
    public operator fun times(matrix: Matrix3): Matrix3 = values.let { lhs ->
        matrix.values.let { rhs ->
            Matrix3(
                lhs[0] * rhs[0] + lhs[3] * rhs[1] + lhs[6] * rhs[2],
                lhs[1] * rhs[0] + lhs[4] * rhs[1] + lhs[7] * rhs[2],
                lhs[2] * rhs[0] + lhs[5] * rhs[1] + lhs[8] * rhs[2],
                lhs[0] * rhs[3] + lhs[3] * rhs[4] + lhs[6] * rhs[5],
                lhs[1] * rhs[3] + lhs[4] * rhs[4] + lhs[7] * rhs[5],
                lhs[2] * rhs[3] + lhs[5] * rhs[4] + lhs[8] * rhs[5],
                lhs[0] * rhs[6] + lhs[3] * rhs[7] + lhs[6] * rhs[8],
                lhs[1] * rhs[6] + lhs[4] * rhs[7] + lhs[7] * rhs[8],
                lhs[2] * rhs[6] + lhs[5] * rhs[7] + lhs[8] * rhs[8],
            )
        }
    }

    /**
     * Transpose this matrix, swapping rows and columns (i.e. flipping over the diagonal axis).
     * @return transposed matrix
     */
    public fun transpose(): Matrix3 = values.let { m ->
        Matrix3(
            m[0], m[3], m[6],
            m[1], m[4], m[7],
            m[2], m[5], m[8],
        )
    }

    /**
     * Create an inverse matrix that will result in an identity matrix when multiplied with this one.
     * Keep in mind that not all matrices are invertible; this method assumes that the provided matrix can be inverted.
     * @return inverse matrix
     */
    public fun inv(): Matrix3 {
        val (a, d, g, b, e, h, c, f, i) = this

        val A = e * i - f * h
        val B = f * g - d * i
        val C = d * h - e * g

        val det = a * A + b * B + c * C

        return Matrix3(
            A / det,
            B / det,
            C / det,
            (c * h - b * i) / det,
            (a * i - c * g) / det,
            (b * g - a * h) / det,
            (b * f - c * e) / det,
            (c * d - a * f) / det,
            (a * e - b * d) / det,
        )
    }

    public operator fun component1(): Double = values[0]
    public operator fun component2(): Double = values[1]
    public operator fun component3(): Double = values[2]
    public operator fun component4(): Double = values[3]
    public operator fun component5(): Double = values[4]
    public operator fun component6(): Double = values[5]
    public operator fun component7(): Double = values[6]
    public operator fun component8(): Double = values[7]
    public operator fun component9(): Double = values[8]
}
