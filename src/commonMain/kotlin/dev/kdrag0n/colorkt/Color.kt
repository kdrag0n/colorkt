package dev.kdrag0n.colorkt

import dev.kdrag0n.colorkt.util.ColorType
import dev.kdrag0n.colorkt.util.ConversionGraph

/**
 * Common interface for all colors.
 *
 * This makes no assumptions about the color itself, but colors are expected to register conversion paths in the graph.
 */
interface Color {
    companion object {
        init {
            // All colors should be registered in order for conversions to work properly
            registerAllColors()
        }

        inline fun <reified T : Color> Color.to() = convertTo(this, T::class) as? T?
            ?: error("No conversion path from ${this::class} to ${T::class}")

        fun convertTo(fromColor: Color, toType: ColorType): Color? {
            val path = ConversionGraph.findPath(fromColor::class, toType)
                ?: return null

            return path.fold(fromColor) { color, converter ->
                converter(color)
            }
        }
    }
}
