package dev.kdrag0n.colorkt.ucs.lab

/**
 * Common interface for color spaces that express color with 3 components:
 *   - L: perceived lightness
 *   - a: amount of green/red color
 *   - b: amount of blue/yellow color
 *
 * Implementations of this are usually uniform color spaces.
 *
 * It may be helpful to convert these colors to polar [dev.kdrag0n.colorkt.ucs.polar.Lch] representations
 * for easier manipulation.
 */
interface Lab {
    /**
     * Perceived lightness component.
     */
    val L: Double

    /**
     * Green/red color component.
     */
    val a: Double

    /**
     * Blue/yellow color component.
     */
    val b: Double
}
