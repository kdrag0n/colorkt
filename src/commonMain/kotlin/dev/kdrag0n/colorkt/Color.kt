package dev.kdrag0n.colorkt

/**
 * Common interface for all colors.
 *
 * This makes no assumptions about the color itself, but implementations are expected to register conversion paths in
 * the global [dev.kdrag0n.colorkt.conversion.ConversionGraph] when possible.
 */
public interface Color
