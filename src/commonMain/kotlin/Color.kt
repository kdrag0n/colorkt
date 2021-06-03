interface Color {
    // All colors should have a conversion path to linear sRGB
    fun toLinearSrgb(): LinearSrgb
}
