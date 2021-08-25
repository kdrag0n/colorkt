package dev.kdrag0n.colorkt.data

import dev.kdrag0n.colorkt.tristimulus.CieXyz

/**
 * Standard reference illuminants, typically used as reference white points.
 */
public object Illuminants {
    /**
     * CIE Standard Illuminant D65. ~6500K color temperature; approximates average daylight in Europe.
     *
     * @see <a href="https://en.wikipedia.org/wiki/Illuminant_D65">Wikipedia</a>
     */
    public val D65: CieXyz = CieXyz(
        x = 0.95047,
        y = 1.0,
        z = 1.08883,
    )

    /**
     * CIE Standard Illuminant D50. ~5000K color temperature.
     */
    public val D50: CieXyz = CieXyz(
        x = 0.9642,
        y = 1.0,
        z = 0.8251,
    )
}
