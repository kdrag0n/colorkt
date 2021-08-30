package dev.kdrag0n.colorkt.conversion

import dev.kdrag0n.colorkt.Color
import kotlin.jvm.JvmStatic
import kotlin.reflect.KClass

internal typealias ColorType = KClass<out Color>

/**
 * One-way converter from color type F to T.
 */
public fun interface ColorConverter<F : Color, T : Color> {
    /**
     * Convert [color] from type F to T.
     */
    public fun convert(color: F): T
}

/**
 * Global color conversion graph, used for automatic conversions between different color spaces.
 */
public object ConversionGraph {
    // Adjacency list: [vertex] = edges
    private val graph = mutableMapOf<ColorType, MutableList<ConversionEdge>>()
    private val pathCache = HashMap<Pair<ColorType, ColorType>, List<ColorConverter<Color, Color>>>()

    init {
        // All first-party color spaces should be registered in order for conversions to work properly
        registerAllColors()
    }

    /**
     * Add a conversion from color type F to T, specified as generic types.
     * This is a convenient wrapper for [add].
     */
    @JvmStatic
    public inline fun <reified F : Color, reified T : Color> add(
        crossinline converter: (F) -> T,
    ): Unit = add(F::class, T::class) { converter(it as F) }

    /**
     * Add a one-way conversion from color type [from] to [to].
     * You should also add a matching reverse conversion, i.e. from [to] to [from].
     */
    @JvmStatic
    public fun add(
        from: ColorType,
        to: ColorType,
        converter: ColorConverter<Color, Color>,
    ) {
        val node = ConversionEdge(from, to, converter)

        if (from in graph) {
            graph[from]!! += node
        } else {
            graph[from] = mutableListOf(node)
        }
        if (to in graph) {
            graph[to]!! += node
        } else {
            graph[to] = mutableListOf(node)
        }
    }

    private fun findPath(from: ColorType, to: ColorType): List<ColorConverter<Color, Color>>? {
        val visited = HashSet<ConversionEdge>()
        val pathQueue = ArrayDeque(listOf(
            // Initial path: from node
            listOf(ConversionEdge(from, from) { it })
        ))

        while (pathQueue.isNotEmpty()) {
            // Get the first path from the queue
            val path = pathQueue.removeFirst()
            // Get the last node from the path to visit
            val node = path.last()

            if (node.to == to) {
                return path.map { it.converter }
            } else if (node !in visited) {
                visited += node
                val neighbors = graph[node.to] ?: continue
                pathQueue.addAll(neighbors.map { path + it })
            }
        }

        // No paths found
        return null
    }

    /**
     * Convert this color to color space [T].
     * @throws UnsupportedConversionException if no automatic conversion path exists
     * @return color as [T]
     */
    public inline fun <reified T : Color> Color.convert(): T = this as? T
        ?: convert(this, T::class) as T?
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
            ?: findPath(fromColor::class, toType)?.also { pathCache[pathKey] = it }
            ?: return null

        return path.fold(fromColor) { color, converter ->
            converter.convert(color)
        }
    }

    private data class ConversionEdge(
        val from: ColorType,
        val to: ColorType,
        val converter: ColorConverter<Color, Color>,
    )
}
