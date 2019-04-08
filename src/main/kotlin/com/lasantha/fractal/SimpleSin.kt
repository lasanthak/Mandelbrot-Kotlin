package kotlin.com.lasantha.fractal

import kotlin.math.sin

object SimpleSin {
    fun calcSin(r: MatrixRange<Double>): Int {
        return if ((sin(mid(r.x1, r.x2) * 20) + sin(mid(r.y1, r.y2) * 20)) < 1) 0 else 1
    }

    private fun mid(a: Double, b: Double) = (a + b) / 2.0
}
