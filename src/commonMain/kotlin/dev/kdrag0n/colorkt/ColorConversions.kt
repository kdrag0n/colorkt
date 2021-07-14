package dev.kdrag0n.colorkt

import dev.kdrag0n.colorkt.rgb.Hsl
import dev.kdrag0n.colorkt.rgb.LinearSrgb
import dev.kdrag0n.colorkt.rgb.Srgb
import dev.kdrag0n.colorkt.tristimulus.CieXyz
import dev.kdrag0n.colorkt.ucs.lab.CieLab
import dev.kdrag0n.colorkt.ucs.lab.Jzazbz
import dev.kdrag0n.colorkt.ucs.lab.Oklab
import dev.kdrag0n.colorkt.ucs.lab.Srlab2
import dev.kdrag0n.colorkt.ucs.polar.CieLch
import dev.kdrag0n.colorkt.ucs.polar.Jzczhz
import dev.kdrag0n.colorkt.ucs.polar.Oklch
import dev.kdrag0n.colorkt.ucs.polar.Srlch2

internal fun registerAllColors() {
    // RGB
    Hsl.register()
    LinearSrgb.register()
    Srgb.register()

    // Tristimulus
    CieXyz.register()

    // UCS Lab
    CieLab.register()
    Jzazbz.register()
    Oklab.register()
    Srlab2.register()

    // UCS polar
    CieLch.register()
    Jzczhz.register()
    Oklch.register()
    Srlch2.register()
}
