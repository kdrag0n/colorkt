package dev.kdrag0n.colorkt.core

import dev.kdrag0n.colorkt.core.srgb.LinearSrgb

interface Color {
    // All colors should have a conversion path to linear sRGB
    fun toLinearSrgb(): LinearSrgb
}
