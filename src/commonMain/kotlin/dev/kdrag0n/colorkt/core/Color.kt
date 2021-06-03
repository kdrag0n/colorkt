package dev.kdrag0n.colorkt.core

import dev.kdrag0n.colorkt.core.srgb.LinearSrgb

/**
 * Common interface for all colors.
 *
 * This makes no assumptions about the color, but all colors must have a conversion path to linear sRGB.
 * Linear sRGB was chosen as the canonical color space because of the original UCS implementations.
 * However, this may be changed to CIE 1931 XYZ in the future.
 */
interface Color {
    /**
     * Convert this color to the linear sRGB color space.
     *
     * @see dev.kdrag0n.colorkt.core.srgb.LinearSrgb
     * @return Color in linear sRGB
     */
    fun toLinearSrgb(): LinearSrgb
}
