import kotlin.math.roundToInt
import LinearSrgb.Companion.toLinearSrgb as realToLinearSrgb

data class Srgb(
    val r: Double,
    val g: Double,
    val b: Double,
) : Color {
    // Convenient constructors for quantized values
    constructor(r: Int, g: Int, b: Int) : this(r.toDouble() / 255.0, g.toDouble() / 255.0, b.toDouble() / 255.0)
    constructor(color: Int) : this(
        color shr 16,
        (color shr 8) and 0xff,
        color and 0xff,
    )

    override fun toLinearSrgb() = realToLinearSrgb()

    fun quantize8(): Int {
        return (quantize8(r) shl 16) or (quantize8(g) shl 8) or quantize8(b)
    }

    companion object {
        // Clamp out-of-bounds values
        private fun quantize8(n: Double) = (n * 255.0).roundToInt().coerceIn(0..255)
    }
}
