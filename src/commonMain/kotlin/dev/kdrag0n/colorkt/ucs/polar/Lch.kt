package dev.kdrag0n.colorkt.ucs.polar

import dev.kdrag0n.colorkt.util.toDegrees
import dev.kdrag0n.colorkt.util.toRadians
import dev.kdrag0n.colorkt.ucs.lab.Lab
import kotlin.math.*

interface Lch {
    val L: Double
    val C: Double
    val h: Double

    companion object {
        internal fun Lab.toLch(): Triple<Double, Double, Double> {
            val hDeg = atan2(b, a).toDegrees()

            return Triple(
                L,
                sqrt(a.pow(2) + b.pow(2)),
                // Normalize the angle, as many will be negative
                if (hDeg < 0) hDeg + 360 else hDeg,
            )
        }

        internal fun Lch.toLab(): Triple<Double, Double, Double> {
            val hRad = h.toRadians()

            return Triple(
                L,
                C * cos(hRad),
                C * sin(hRad),
            )
        }
    }
}
