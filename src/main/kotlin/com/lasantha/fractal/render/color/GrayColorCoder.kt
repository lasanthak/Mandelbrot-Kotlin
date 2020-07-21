package com.lasantha.fractal.render.color

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

    override fun toRGB(n: Int, rxr: Double): Int {
        if (n >= maxN) {
            return ColorCoder.DEFAULT_COLOR
        }

        val v = ln(ln(rxr) / 2.0.pow(n)) / blendingFactor
        val gray = round(127.5 * (1 + cos(twoPI * v))).toInt()

        return encodeColor(gray, gray, gray)
    }
}