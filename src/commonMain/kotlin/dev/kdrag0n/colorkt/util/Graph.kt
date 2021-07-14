package dev.kdrag0n.colorkt.util

import dev.kdrag0n.colorkt.Color
import kotlin.reflect.KClass

internal typealias ColorType = KClass<out Color>

// This is intentionally public so that users can add custom color spaces.
object ConversionGraph {
    // Adjacency list: [vertex] = edges
    private val graph = mutableMapOf<ColorType, MutableList<ConversionEdge>>()

    inline fun <reified F : Color, reified T : Color> add(
        crossinline converter: (F) -> T,
    ) = add(F::class, T::class) { converter(it as F) }

    fun add(
        from: ColorType,
        to: ColorType,
        converter: (Color) -> Color,
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

    fun findPath(from: ColorType, to: ColorType): List<(Color) -> Color>? {
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

    private data class ConversionEdge(
        val from: ColorType,
        val to: ColorType,
        val converter: (Color) -> Color,
    )
}

// Auto-registering providers are internal because it makes no sense for external code to use this.
internal interface ConversionProvider {
    fun register()
}
