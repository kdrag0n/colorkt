package dev.kdrag0n.colorkt.util.conversion

/**
 * Exception thrown when there is no automatic conversation path in
 * [dev.kdrag0n.colorkt.util.conversion.ConversionGraph] for a specific pair of colors.
 */
public class UnsupportedConversionException : RuntimeException {
    public constructor() : super()
    public constructor(message: String) : super(message)
}
