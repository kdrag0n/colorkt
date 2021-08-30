package dev.kdrag0n.colorkt.conversion

import dev.kdrag0n.colorkt.rgb.LinearSrgb
import dev.kdrag0n.colorkt.tristimulus.CieXyz
import dev.kdrag0n.colorkt.tristimulus.CieXyzAbs
import dev.kdrag0n.colorkt.ucs.lab.CieLab
import dev.kdrag0n.colorkt.ucs.lab.Oklab
import dev.kdrag0n.colorkt.ucs.lab.Srlab2
import dev.kdrag0n.colorkt.ucs.lch.CieLch
import dev.kdrag0n.colorkt.ucs.lch.Oklch
import dev.kdrag0n.colorkt.ucs.lch.Srlch2
import kotlin.jvm.JvmSynthetic

@JvmSynthetic
internal fun registerAllColors() {
    // RGB
    LinearSrgb.register()

    // Tristimulus
    CieXyz.register()
    CieXyzAbs.register()

    // UCS Lab
    CieLab.register()
    Oklab.register()
    Srlab2.register()

    // UCS polar
    CieLch.register()
    Oklch.register()
    Srlch2.register()
}
