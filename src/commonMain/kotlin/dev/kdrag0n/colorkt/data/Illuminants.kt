package dev.kdrag0n.colorkt.data

import dev.kdrag0n.colorkt.tristimulus.CieXyz
import kotlin.jvm.JvmField

/**
 * Standard reference illuminants, typically used as reference white points.
 */
public object Illuminants {
    /**
     * CIE Standard Illuminant D65. ~6500K color temperature; approximates average daylight in Europe.
     *
     * @see <a href="https://en.wikipedia.org/wiki/Illuminant_D65">Wikipedia</a>
     */
    @JvmField
    public val D65: CieXyz = CieXyz(
        // xy chromaticities from the sRGB spec
        x = 0.3127 / 0.3290,
        y = 1.0,
        z = (1.0 - 0.3127 - 0.3290) / 0.3290,
    )

    /**
     * CIE Standard Illuminant D50. ~5000K color temperature.
     */
    @JvmField
    public val D50: CieXyz = CieXyz(
        x = 0.3457 / 0.3585,
        y = 1.0,
        z = (1.0 - 0.3457 - 0.3585) / 0.3585,
    )
}
