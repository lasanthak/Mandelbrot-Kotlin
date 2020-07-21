package com.lasantha.fractal.render.color

import kotlin.math.cos
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.round
import kotlin.math.sqrt

/**
 * See: https://www.math.univ-toulouse.fr/~cheritat/wiki-draw/index.php/Mandelbrot_set
 */
class RGBColorCoder(override val maxN: Int, private val blendingFactor: Double) : ColorCoder {
    private val rFactor = 1 / ln(2.0) // 1.4426950408889634
    private val gFactor = 1 / (3 * sqrt(2.0) * ln(2.0)) // 0.3400464821989298
    private val bFactor = 1 / (7 * 3.0.pow(0.125) * ln(2.0)) // 0.17965377284509387

    override fun toRGB(n: Int, rxr: Double): Int {
        if (n >= maxN) {
            return ColorCoder.DEFAULT_COLOR
        }

        val v = ln(ln(rxr) / 2.0.pow(n)) / blendingFactor
        val r = round(127.5 * (1 - cos(rFactor * v))).toInt()
        val g = round(127.5 * (1 - cos(gFactor * v))).toInt()
        val b = round(127.5 * (1 - cos(bFactor * v))).toInt()

        return encodeColor(r, g, b)
    }
}