package dev.kdrag0n.colorkt.rgb

import kotlin.math.roundToInt

/**
 * A color in the standard sRGB color space.
 * This is the most common device color space, usually used for final output colors.
 *
 * @see <a href="https://en.wikipedia.org/wiki/SRGB">Wikipedia</a>
 */
public data class Srgb(
    override val r: Double,
    override val g: Double,
    override val b: Double,
) : Rgb {
    // Convenient constructors for quantized values

    /**
     * Constructor for 8-bit integer sRGB components.
     */
    public constructor(
        r: Int,
        g: Int,
        b: Int,
    ) : this(
        r.toDouble() / 255.0,
        g.toDouble() / 255.0,
        b.toDouble() / 255.0,
    )

    /**
     * Constructor for 8-bit packed integer sRGB colors, such as hex color codes.
     */
    public constructor(color: Int) : this(
        r = (color shr 16) and 0xff,
        g = (color shr 8) and 0xff,
        b = color and 0xff,
    )

    /**
     * Convert, or quantize, this color to 8 bits per channel and pack it into a 32-bit integer.
     * This is equivalent to the common hex color codes (e.g. #FF00FF).
     *
     * @return sRGB color packed into 32-bit integer
     */
    public fun toRgb8(): Int {
        return (quantize8(r) shl 16) or (quantize8(g) shl 8) or quantize8(b)
    }

    /**
     * Check whether this color is within the sRGB gamut.
     * @return true if color is in gamut, false otherwise
     */
    public fun isInGamut(): Boolean = !r.isNaN() && !g.isNaN() && !b.isNaN() &&
            r in 0.0..1.0 && g in 0.0..1.0 && b in 0.0..1.0

    internal companion object {
        internal fun register() { }

        // Clamp out-of-bounds values
        private fun quantize8(n: Double) = (n * 255.0).roundToInt() and 0xff
    }
}
