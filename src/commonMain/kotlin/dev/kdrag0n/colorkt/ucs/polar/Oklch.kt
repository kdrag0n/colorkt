package dev.kdrag0n.colorkt.ucs.polar

import dev.kdrag0n.colorkt.core.Color
import dev.kdrag0n.colorkt.ucs.polar.Lch.Companion.toLab
import dev.kdrag0n.colorkt.ucs.polar.Lch.Companion.toLch
import dev.kdrag0n.colorkt.ucs.lab.Oklab

data class Oklch(
    override val L: Double,
    override val C: Double,
    override val h: Double = 0.0,
) : Color, Lch {
    override fun toLinearSrgb() = toOklab().toLinearSrgb()

    fun toOklab(): Oklab {
        val (l, a, b) = toLab()
        return Oklab(l, a, b)
    }

    companion object {
        fun Oklab.toOklch(): Oklch {
            val (l, c, h) = toLch()
            return Oklch(l, c, h)
        }
    }
}
