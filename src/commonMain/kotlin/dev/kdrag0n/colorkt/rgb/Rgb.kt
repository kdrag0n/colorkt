package dev.kdrag0n.colorkt.rgb

import dev.kdrag0n.colorkt.Color

/**
 * Common interface for color spaces that express color with the following 3 components:
 *   - R: amount of red color
 *   - G: amount of green color
 *   - B: amount of blue color
 *
 * Implementations of this are usually device color spaces, used for final output colors.
 */
public interface Rgb : Color {
    /**
     * Red color component.
     */
    public val r: Double

    /**
     * Green color component.
     */
    public val g: Double

    /**
     * Blue color component.
     */
    public val b: Double
}
