package dev.kdrag0n.colorkt

import dev.kdrag0n.colorkt.util.conversion.ColorConverter
import dev.kdrag0n.colorkt.util.conversion.ColorType
import dev.kdrag0n.colorkt.util.conversion.ConversionGraph
import dev.kdrag0n.colorkt.util.conversion.UnsupportedConversionException
import kotlin.jvm.JvmStatic

/**
 * Common interface for all colors.
 *
 * This makes no assumptions about the color itself, but implementations are expected to register conversion paths in
 * the global conversion graph when possible.
 */
public interface Color {
    public companion object {
        private val pathCache = HashMap<Pair<ColorType, ColorType>, List<ColorConverter<Color, Color>>>()

        init {
            // All first-party color spaces should be registered in order for conversions to work properly
            registerAllColors()
        }

        /**
         * Convert this color to color space [T].
         * @throws UnsupportedConversionException if no automatic conversion path exists
         * @return color as [T]
         */
        public inline fun <reified T : Color> Color.convert(): T = this as? T
            ?: convert(this, T::class) as? T?
            ?: throw UnsupportedConversionException("No conversion path from ${this::class} to ${T::class}")

        /**
         * Convert [fromColor] to color space [toType].
         * @throws UnsupportedConversionException if no automatic conversion path exists
         * @return color as [toType]
         */
        @JvmStatic
        public fun convert(fromColor: Color, toType: ColorType): Color? {
            val pathKey = fromColor::class to toType
            val path = pathCache[pathKey]
                ?: ConversionGraph.findPath(fromColor::class, toType)?.also { pathCache[pathKey] = it }
                ?: return null

            return path.fold(fromColor) { color, converter ->
                converter.convert(color)
            }
        }
    }
}
