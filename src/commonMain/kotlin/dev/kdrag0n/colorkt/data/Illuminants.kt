package dev.kdrag0n.colorkt.data

import dev.kdrag0n.colorkt.tristimulus.CieXyz
import kotlin.jvm.JvmField

/**
 * Standard reference illuminants, typically used as reference white points.
 */
public object Illuminants {
    /**
     * sRGB variant of CIE Standard Illuminant D65. ~6500K color temperature; approximates average daylight in Europe.
     * This uses the white point chromaticities defined in the sRGB specification.
     *
     * @see <a href="https://en.wikipedia.org/wiki/SRGB">Wikipedia: sRGB</a>
     */
    @JvmField
    public val D65: CieXyz = CieXyz(
        x = xyToX(0.3127, 0.3290),
        y = 1.0,
        z = xyToZ(0.3127, 0.3290),
    )

    /**
     * ASTM variant of CIE Standard Illuminant D65. ~6500K color temperature; approximates average daylight in Europe.
     * This uses the XYZ values defined in the ASTM E‑308 document.
     *
     * @see <a href="https://en.wikipedia.org/wiki/Illuminant_D65">Wikipedia: Illuminant D65</a>
     */
    @JvmField
    public val D65_ASTM: CieXyz = CieXyz(
        x = 0.95047,
        y = 1.0,
        z = 1.08883,
    )

    /**
     * Raw precise variant of CIE Standard Illuminant D65. ~6500K color temperature; approximates average daylight in Europe.
     * This uses XYZ values calculated from raw 1nm SPD data, combined with the CIE 1931 2-degree
     * standard observer.
     *
     * @see <a href="https://www.rit.edu/cos/colorscience/rc_useful_data.php">RIT - Useful Color Data</a>
     */
    @JvmField
    public val D65_CIE: CieXyz = CieXyz(
        x = 0.9504705586542832,
        y = 1.0,
        z = 1.088828736395884,
    )

    /**
     * CIE Standard Illuminant D50. ~5000K color temperature.
     */
    @JvmField
    public val D50: CieXyz = CieXyz(
        x = xyToX(0.3457, 0.3585),
        y = 1.0,
        z = xyToZ(0.3457, 0.3585),
    )

    private fun xyToX(x: Double, y: Double) = x / y
    private fun xyToZ(x: Double, y: Double) = (1.0 - x - y) / y
}
