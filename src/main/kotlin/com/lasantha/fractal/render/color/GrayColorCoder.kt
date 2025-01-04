package com.lasantha.fractal.render.color

import com.lasantha.fractal.calculate.Result
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.round

/**
 * See: https://www.math.univ-toulouse.fr/~cheritat/wiki-draw/index.php/Mandelbrot_set
 */
class GrayColorCoder(override val maxN: Int, private val blendingFactor: Double) : ColorCoder {
    private val twoPI = 2 * PI

    override fun toRGB(res: Result): Int {
        if (res.n >= maxN) {
            return ColorCoder.INSIDE_COLOR
        }

        val v = ln(ln(res.rr) / 2.0.pow(res.n)) / blendingFactor
        val gray = round(127.5 * (1 + cos(twoPI * v))).toInt()

        return ColorCoder.encodeColor(gray, gray, gray)
    }
}
