import kotlin.math.PI
import kotlin.math.pow

fun cbrt(x: Double) = x.pow(1.0 / 3.0)

fun Double.toRadians() = this * PI / 180.0
fun Double.toDegrees() = this * 180.0 / PI
