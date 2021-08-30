# Color.kt

Color.kt is a modern color science library for Kotlin Multiplatform and Java. It includes modern perceptually-uniform color spaces and color appearance models, such as [Oklab](https://bottosson.github.io/posts/oklab/) and [ZCAM](https://www.osapublishing.org/oe/fulltext.cfm?uri=oe-29-4-6036&id=447640).

[**API documentation**](https://javadoc.io/doc/dev.kdrag0n/colorkt)

## Features

- Perceptually-uniform color spaces, each with LCh (lightness, chroma, hue) representations
  - [Oklab](https://bottosson.github.io/posts/oklab/)
  - [CIELAB](https://en.wikipedia.org/wiki/CIELAB_color_space)
  - [SRLAB2](https://www.magnetkern.de/srlab2.html)
- Color appearance models
  - [ZCAM](https://www.osapublishing.org/oe/fulltext.cfm?uri=oe-29-4-6036&id=447640)
- Hue-preserving gamut mapping
  - Preserve lightness, reduce chroma (default)
  - Project towards neutral 50% gray
  - Adaptive lightness and chroma preservation
- [CIE 1931 XYZ](https://en.wikipedia.org/wiki/CIE_1931_color_space) interchange color space
  - Relative luminance
  - Absolute luminance in nits (cd/m²) for HDR color spaces
- Automatic conversion graph
- Gamma-correct sRGB encoding and decoding
- Support for custom color spaces
- Idiomatic Java API

## Usage

![Latest version on Maven Central](https://img.shields.io/maven-central/v/dev.kdrag0n/colorkt)

Add this library as a dependency and replace `VERSION` with the latest version above:

```groovy
repositories {
    mavenCentral()
}

dependencies {
    implementation 'dev.kdrag0n:colorkt:VERSION'
}
```

If you're using the Kotlin Gradle DSL:

```kotlin
repositories {
    mavenCentral()
}

dependencies {
    implementation("dev.kdrag0n:colorkt:VERSION")
}
```

## Examples

### Increase colorfulness with CIELAB

Convert a hex sRGB color code to CIELAB, increase the chroma (colorfulness), and convert it back to sRGB as a hex color code:

```kotlin
val cielab = Srgb("#9b392f").convert<CieLab>()
val lch = cielab.convert<CieLch>()
val boosted1 = lch.copy(C = lch.C * 2).convert<Srgb>().toHex()
```

Do the same thing in Oklab:

```kotlin
val oklab = Srgb("#9b392f").convert<Oklab>()
val lch = oklab.convert<Oklch>()
val boosted2 = lch.copy(C = lch.C * 2).convert<Srgb>().toHex()
```

### Advanced color appearance model usage

Model a color using ZCAM:

```kotlin
// Brightness of the display
val luminance = 200.0 // nits (cd/m²)

// Conditions under which the color will be viewed
val cond = Zcam.ViewingConditions(
    surroundFactor = Zcam.ViewingConditions.SURROUND_AVERAGE,
    adaptingLuminance = 0.4 * luminance,
    // Mid-gray background at 50% luminance
    backgroundLuminance = CieLab(50.0, 0.0, 0.0).toXyz().y * luminance,
    // D65 is the only supported white point
    referenceWhite = Illuminants.D65.toAbs(luminance),
)

// Color to convert
val src = Srgb("#533b69")
// Use ZCAM to get perceptual color appearance attributes
val zcam = src.toLinear().toXyz().toAbs(luminance).toZcam(cond)
```

Increase the chroma (colorfulness):

```kotlin
val colorful = zcam.copy(chroma = zcam.chroma * 2)
```

Convert the color back to sRGB, while preserving hue and avoiding ugly results caused by hard clipping:

```kotlin
val srgb = colorful.clipToLinearSrgb()
```

Finally, print the new hex color code:

```kotlin
println(srgb.toHex())
```

## Automatic conversion

Color.kt makes it easy to convert between any two color spaces by automatically finding the shortest path in the color conversion graph. This simplifies long conversions, which can occur frequently when working with different color spaces. For example, converting from CIELCh to Oklab LCh is usually done like this:

```kotlin
val oklab = cielch.toCieLab().toXyz().toLinearSrgb().toOklab().toOklch()
```

With automatic conversion:

```kotlin
val oklab = cielch.convert<Oklch>()
```

However, keep in mind that there is a performance cost associated with automatic conversion because it requires searching the graph. Conversion paths are cached after the first use, but it is still less efficient than manual conversion; prefer manual, explicit conversions in performance-critical code.

## Custom color spaces

Color.kt includes several color spaces that should cover most use cases, but you can also add your own if necessary. Simply create a class that implements the Color interface, and implement some conversions to make the color space useful:

```kotlin
data class GrayColor(val brightness: Double) : Color {
    fun toLinearSrgb() = LinearSrgb(brightness, brightness, brightness)

    companion object {
        // sRGB luminosity function from https://en.wikipedia.org/wiki/Relative_luminance
        fun LinearSrgb.toGray() = GrayColor(0.2126 * r + 0.7251 * g + 0.0722 * b)
    }
}
```

Optionally, add your new color space to the automatic conversion graph for convenient usage:

```kotlin
ConversionGraph.add<LinearSrgb, GrayColor> { it.toGray() }
ConversionGraph.add<GrayColor, LinearSrgb> { it.toLinearSrgb() }
```

If you implement a new color space this way, please consider contributing it with a [pull request](https://github.com/kdrag0n/colorkt/compare) so that everyone can benefit from it!
