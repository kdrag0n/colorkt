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
     * Create a color from 8-bit integer sRGB components.
     */
    public constructor(
        r: Int,
        g: Int,
        b: Int,
    ) : this(
        r = r.toDouble() / 255.0,
        g = g.toDouble() / 255.0,
        b = b.toDouble() / 255.0,
    )

    /**
     * Create a color from a packed (A)RGB8 integer.
     */
    public constructor(color: Int) : this(
        r = (color shr 16) and 0xff,
        g = (color shr 8) and 0xff,
        b = color and 0xff,
    )

    /**
     * Create a color from a hex color code (e.g. #FA00FA).
     * Hex codes with and without leading hash (#) symbols are supported.
     */
    public constructor(color: String) : this(color.removePrefix("#").toInt(16))

    /**
     * Convert this color to an 8-bit packed RGB integer (32 bits total)
     *
     * This is equivalent to the integer value of hex color codes (e.g. #FA00FA).
     *
     * @return color as 32-bit integer in RGB8 format
     */
    public fun toRgb8(): Int = (quantize8(r) shl 16) or (quantize8(g) shl 8) or quantize8(b)

    /**
     * Convert this color to an 8-bit hex color code (e.g. #FA00FA).
     *
     * @return color as RGB8 hex code
     */
    public fun toHex(): String = "#" + toRgb8().toString(16).padStart(6, padChar = '0')

    private companion object {
        // Clamp out-of-bounds values
        private fun quantize8(n: Double) = (n * 255.0).roundToInt().coerceIn(0, 255)
    }
}
