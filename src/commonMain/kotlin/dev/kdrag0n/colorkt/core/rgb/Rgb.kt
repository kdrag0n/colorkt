package dev.kdrag0n.colorkt.core.rgb

import dev.kdrag0n.colorkt.core.Color

/**
 * Common interface for color spaces that express color with the following 3 components:
 *   - R: amount of red color
 *   - G: amount of green color
 *   - B: amount of blue color
 *
 * Implementations of this are usually device color spaces, used for final output colors.
 */
interface Rgb : Color {
    /**
     * Red color component.
     */
    val r: Double

    /**
     * Green color component.
     */
    val g: Double

    /**
     * Blue color component.
     */
    val b: Double
}
